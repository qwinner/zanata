/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.apache.lucene.analysis.Analyzer;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.transform.ResultTransformer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.zanata.common.ContentState;
import org.zanata.common.LocaleId;
import org.zanata.hibernate.search.IndexFieldLabels;
import org.zanata.hibernate.search.TextContainerAnalyzerDiscriminator;
import org.zanata.model.HDocument;
import org.zanata.model.HLocale;
import org.zanata.model.HTextFlow;
import org.zanata.model.HTextFlowTarget;
import org.zanata.search.FilterConstraints;
import org.zanata.util.HTextFlowPosComparator;
import org.zanata.webtrans.shared.model.DocumentId;
import org.zanata.webtrans.shared.model.TransMemoryQuery;
import org.zanata.webtrans.shared.rpc.HasSearchType.SearchType;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Name("textFlowDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
@Slf4j
public class TextFlowDAO extends AbstractDAOImpl<HTextFlow, Long>
{
   // TODO replace all getSession() code to use entityManager
   private static final Version LUCENE_VERSION = Version.LUCENE_29;

   @In
   private FullTextEntityManager entityManager;

   @In
   LocaleDAO localeDAO;

   public TextFlowDAO()
   {
      super(HTextFlow.class);
   }

   public TextFlowDAO(Session session)
   {
      super(HTextFlow.class, session);
   }

   public HTextFlow getById(HDocument document, String id)
   {
      Criteria cr = getSession().createCriteria(HTextFlow.class);
      cr.add(Restrictions.naturalId().set("resId", id).set("document", document));
      cr.setCacheable(true).setComment("TextFlowDAO.getById");
      return (HTextFlow) cr.uniqueResult();
   }

   @SuppressWarnings("unchecked")
   public List<HTextFlow> findByIdList(List<Long> idList)
   {
      if (idList == null || idList.isEmpty())
      {
         return new ArrayList<HTextFlow>();
      }
      Query query = getSession().createQuery("FROM HTextFlow WHERE id in (:idList)");
      query.setParameterList("idList", idList);
      // caching could be expensive for long idLists
      query.setCacheable(false).setComment("TextFlowDAO.getByIdList");
      return query.list();
   }

   public HTextFlow getObsoleteById(HDocument document, String id)
   {
      Criteria cr = getSession().createCriteria(HTextFlow.class);
      cr.add(Restrictions.naturalId().set("resId", id).set("document", document)).add(Restrictions.eq("obsolete", true));
      cr.setCacheable(true).setComment("TextFlowDAO.getObsoleteById");
      return (HTextFlow) cr.uniqueResult();
   }

   @SuppressWarnings("unchecked")
   public List<HTextFlow> getNavigationByDocumentId(Long documentId, HLocale hLocale, ResultTransformer resultTransformer, FilterConstraints filterConstraints)
   {
      StringBuilder queryBuilder = new StringBuilder();
      // I can't write a HQL or criteria to achieve the same result. I gave up...
      // @formatter:off
      queryBuilder
            .append("SELECT tf.id, tft.state FROM HTextFlow tf ")
            .append(" LEFT JOIN ( SELECT tf_id, state, content0, content1, content2, content3, content4, content5 ")
            .append("             FROM HTextFlowTarget WHERE locale = :locale) tft ON tft.tf_id = tf.id ")
            .append(" WHERE tf.document_id = :docId ");
      queryBuilder
            .append(" AND ")
            .append(buildContentStateCondition(filterConstraints.isIncludeApproved(), filterConstraints.isIncludeFuzzy(), filterConstraints.isIncludeNew(), "tft"))
            .append(" AND (")
            .append(buildSearchCondition(filterConstraints.getSearchString(), "tf")) // search in source
            .append(" OR ")
            .append(buildSearchCondition(filterConstraints.getSearchString(), "tft")) // search in target
            .append(")")
      ;
      queryBuilder
            .append(" ORDER BY tf.pos");
      // @formatter:on
      log.debug("get navigation SQL query: {}", queryBuilder);
      Query query = getSession().createSQLQuery(queryBuilder.toString())
            .addScalar("id", Hibernate.LONG)
            .addScalar("state")
            .setParameter("docId", documentId)
            .setParameter("locale", hLocale.getId())
            .setResultTransformer(resultTransformer);

      return query.list();
   }

   /**
    * This will build a SQL query condition in where clause.
    * If all status are equal (i.e. all true or all false), it's treated as accept all and it will return '1'.
    *
    * @param acceptApproved accept approved status
    * @param acceptFuzzy accept fuzzy status
    * @param acceptUntranslated accept untranslated status
    * @param alias HTextFlowTarget alias
    * @return '1' if accept all status or a SQL condition clause with target content state conditions in parentheses '()' joined by 'or'
    */
   protected static String buildContentStateCondition(boolean acceptApproved, boolean acceptFuzzy, boolean acceptUntranslated, String alias)
   {
      if (acceptApproved == acceptFuzzy && acceptApproved == acceptUntranslated)
      {
         return "1";
      }
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      List<String> conditions = Lists.newArrayList();
      final String column = alias + ".state";
      if (acceptApproved)
      {
         conditions.add(column + "=2");
      }
      if (acceptFuzzy)
      {
         conditions.add(column + "=1");
      }
      if (acceptUntranslated)
      {
         conditions.add(column + "=0 or " + column + " is null");
      }
      Joiner joiner = Joiner.on(" or ");
      joiner.appendTo(builder, conditions);
      builder.append(")");
      return builder.toString();
   }

   /**
    * This will build a SQL query condition in where clause.
    * It can be used to search string in content0, content1 ... content5 in HTextFlow or HTextFlowTarget.
    * If search term is empty it will return '1'
    *
    * @param searchString search term
    * @param alias table name alias
    * @return '1' if searchString is empty or a SQL condition clause with lower(contentX) like '%searchString%' in parentheses '()' joined by 'or'
    */
   protected static String buildSearchCondition(String searchString, String alias)
   {
      if (Strings.isNullOrEmpty(searchString))
      {
         return "1";
      }
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      List<String> conditions = Lists.newArrayList();
      for (int i = 0; i < 6; i++)
      {
         conditions.add("lower(" + alias + ".content" + i + ") LIKE '%" + searchString.toLowerCase() + "%'");
      }
      Joiner joiner = Joiner.on(" or ");
      joiner.appendTo(builder, conditions);
      builder.append(")");
      return builder.toString();
   }




   public List<Object[]> getSearchResult(TransMemoryQuery query, LocaleId locale, final int maxResult) throws ParseException
   {
      String queryText = null;
      String[] multiQueryText = null;

      switch (query.getSearchType())
      {
      // 'Lucene' in the editor
      case RAW:
         queryText = query.getQueries().get(0);
         if (StringUtils.isBlank(queryText))
         {
            return new ArrayList<Object[]>();
         }
         break;

      // 'Fuzzy' in the editor
      case FUZZY:
         queryText = QueryParser.escape(query.getQueries().get(0));
         if (StringUtils.isBlank(queryText))
         {
            return new ArrayList<Object[]>();
         }
         break;

      // 'Phrase' in the editor
      case EXACT:
         queryText = "\"" + QueryParser.escape(query.getQueries().get(0)) + "\"";
         if (StringUtils.isBlank(queryText))
         {
            return new ArrayList<Object[]>();
         }
         break;

      // 'Fuzzy' in the editor, plus it is a plural entry
      case FUZZY_PLURAL:
         multiQueryText = new String[query.getQueries().size()];
         for (int i = 0; i < query.getQueries().size(); i++)
         {
            multiQueryText[i] = QueryParser.escape(query.getQueries().get(i));
            if (StringUtils.isBlank(multiQueryText[i]))
            {
               return new ArrayList<Object[]>();
            }
         }
         break;
      default:
         throw new RuntimeException("Unknown query type: " + query.getSearchType());
      }

      org.apache.lucene.search.Query textQuery;
      // Analyzer determined by the language
      String analyzerDefName = TextContainerAnalyzerDiscriminator.getAnalyzerDefinitionName( locale.getId() );
      Analyzer analyzer = entityManager.getSearchFactory().getAnalyzer(analyzerDefName);

      if (query.getSearchType() == SearchType.FUZZY_PLURAL)
      {
         int queriesSize = multiQueryText.length;
         if (queriesSize > IndexFieldLabels.TF_CONTENT_FIELDS.length)
         {
            log.warn("query contains {} fields, but we only index {}", queriesSize, IndexFieldLabels.TF_CONTENT_FIELDS.length);
         }
         String[] searchFields = new String[queriesSize];
         System.arraycopy(IndexFieldLabels.TF_CONTENT_FIELDS, 0, searchFields, 0, queriesSize);

         textQuery = MultiFieldQueryParser.parse(LUCENE_VERSION, multiQueryText, searchFields, analyzer);
      }
      else
      {
         MultiFieldQueryParser parser = new MultiFieldQueryParser(LUCENE_VERSION, IndexFieldLabels.TF_CONTENT_FIELDS, analyzer);
         textQuery = parser.parse(queryText);
      }
      FullTextQuery ftQuery = entityManager.createFullTextQuery(textQuery, HTextFlowTarget.class);

      ftQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
      @SuppressWarnings("unchecked")
      List<Object[]> matches = ftQuery.setMaxResults(maxResult).getResultList();
      return matches;
   }

   public int getTotalWords()
   {
      Query q = getSession().createQuery("select sum(tf.wordCount) from HTextFlow tf where tf.obsolete=0");
      q.setCacheable(true).setComment("TextFlowDAO.getTotalWords");
      Long totalCount = (Long) q.uniqueResult();
      return totalCount == null ? 0 : totalCount.intValue();
   }

   public int getTotalTextFlows()
   {
      Query q = getSession().createQuery("select count(*) from HTextFlow");
      q.setCacheable(true).setComment("TextFlowDAO.getTotalTextFlows");
      Long totalCount = (Long) q.uniqueResult();
      return totalCount == null ? 0 : totalCount.intValue();
   }

   public int getTotalActiveTextFlows()
   {
      Query q = getSession().createQuery("select count(*) from HTextFlow tf where tf.obsolete=0");
      q.setCacheable(true).setComment("TextFlowDAO.getTotalActiveTextFlows");
      Long totalCount = (Long) q.uniqueResult();
      return totalCount == null ? 0 : totalCount.intValue();
   }

   public int getTotalObsoleteTextFlows()
   {
      Query q = getSession().createQuery("select count(*) from HTextFlow tf where tf.obsolete=1");
      q.setCacheable(true).setComment("TextFlowDAO.getTotalObsoleteTextFlows");
      Long totalCount = (Long) q.uniqueResult();
      return totalCount == null ? 0 : totalCount.intValue();
   }

   public int getCountByDocument(Long documentId)
   {
      Query q = getSession().createQuery("select count(*) from HTextFlow tf where tf.obsolete=0 and tf.document.id = :id order by tf.pos");
      q.setParameter("id", documentId);
      q.setCacheable(true).setComment("TextFlowDAO.getCountByDocument");
      Long totalCount = (Long) q.uniqueResult();
      return totalCount == null ? 0 : totalCount.intValue();
   }

   @SuppressWarnings("unchecked")
   public List<HTextFlow> getTextFlows(DocumentId documentId, int startIndex, int maxSize)
   {
      Query q = getSession().createQuery("from HTextFlow tf where tf.obsolete=0 and tf.document.id = :id order by tf.pos");
      q.setParameter("id", documentId.getId());
      q.setFirstResult(startIndex);
      q.setMaxResults(maxSize);
      q.setCacheable(true).setComment("TextFlowDAO.getTransUnitList");
      return q.list();
   }

   /**
    * for a given locale, we first find text flow where has no target (targets
    * map has no key equals the locale), or (the text flow target has zero size
    * contents OR content state is NEW).
    * 
    * @param documentId document id (NOT the String type docId)
    * @param hLocale locale
    * @return a list of HTextFlow that has no translation for given locale.
    */
   public List<HTextFlow> getAllUntranslatedTextFlowByDocumentId(DocumentId documentId, HLocale hLocale)
   {
      // @formatter:off
      String query = "select distinct tf from HTextFlow tf left join tf.targets " +
            "where tf.obsolete = 0 and tf.document.id = :docId and " +
            "(:locale not in indices(tf.targets) or exists " + //text flow does not have a target for given locale
            "  (select tft.id from HTextFlowTarget tft where tft.textFlow.id = tf.id and tft.locale = :locale and tft.state = :contentState)" + //text flow has target but target has either empty contents or content state is NEW
            ") order by tf.pos";
      // @formatter:on

      Query textFlowQuery = getSession().createQuery(query);
      textFlowQuery.setParameter("docId", documentId.getId());
      textFlowQuery.setParameter("locale", hLocale);
      textFlowQuery.setParameter("contentState", ContentState.New);
      textFlowQuery.setCacheable(true).setComment("TextFlowDAO.getAllUntranslatedTextFlowByDocId");

      @SuppressWarnings("unchecked")
      List<HTextFlow> result = textFlowQuery.list();
      log.debug("doc {} has {} untranslated textFlow for locale {}", new Object[] { documentId, result.size(), hLocale.getLocaleId() });
      return result;
   }

   public List<HTextFlow> getTextFlowsByStatus(DocumentId documentId, HLocale hLocale, boolean filterTranslated, boolean filterNeedReview, boolean filterUntranslated)
   {
      List<HTextFlow> result = Lists.newArrayList();
      List<HTextFlow> untranslated = Lists.newArrayList();
      List<HTextFlow> translated = Lists.newArrayList();

      if (filterUntranslated)
      {
         // hard part. leave it alone.
         untranslated = getAllUntranslatedTextFlowByDocumentId(documentId, hLocale);
         result.addAll(untranslated);
      }
      if (filterNeedReview || filterTranslated)
      {
         // @formatter:off
         String queryString = "select distinct tf from HTextFlow tf inner join tf.targets as tft " +
               "where tf.document.id = :docId and tft.locale = :locale and tft.state in (:contentStates) " +
               "order by tf.pos";
         // @formatter:on
         List<ContentState> contentStates = Lists.newArrayList();
         if (filterNeedReview)
         {
            contentStates.add(ContentState.NeedReview);
         }
         if (filterTranslated)
         {
            contentStates.add(ContentState.Approved);
         }
         Query query = getSession().createQuery(queryString);
         query.setParameter("docId", documentId.getId());
         query.setParameter("locale", hLocale);
         query.setParameterList("contentStates", contentStates);
         query.setCacheable(true).setComment("TextFlowDAO.getTextFlowsByStatus");

         translated = query.list();
         result.addAll(translated);
      }

      if (!untranslated.isEmpty() && !translated.isEmpty())
      {
         Collections.sort(result, HTextFlowPosComparator.INSTANCE);
      }
      return result;
   }
}
