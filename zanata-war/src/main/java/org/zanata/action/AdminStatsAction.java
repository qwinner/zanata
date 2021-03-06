/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.zanata.dao.DocumentDAO;
import org.zanata.dao.PersonDAO;
import org.zanata.dao.ProjectDAO;
import org.zanata.dao.ProjectIterationDAO;
import org.zanata.dao.TextFlowDAO;
import org.zanata.dao.TextFlowTargetDAO;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
@Name("adminStatsAction")
@Scope(ScopeType.PAGE)
public class AdminStatsAction implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   @Logger
   Log log;

   @In
   ProjectDAO projectDAO;

   @In
   ProjectIterationDAO projectIterationDAO;

   @In
   PersonDAO personDAO;

   @In
   TextFlowDAO textFlowDAO;

   @In
   TextFlowTargetDAO textFlowTargetDAO;

   @In
   DocumentDAO documentDAO;

   public int getTotalProjectCount()
   {
      return projectDAO.getTotalProjectCount();
   }

   public int getTotalActiveProjectCount()
   {
      return projectDAO.getTotalActiveProjectCount();
   }

   public int getTotalReadOnlyProjectCount()
   {
      return projectDAO.getTotalReadOnlyProjectCount();
   }

   public int getTotalObsoleteProjectCount()
   {
      return projectDAO.getTotalObsoleteProjectCount();
   }

   public int getTotalProjectIterCount()
   {
      return projectIterationDAO.getTotalProjectIterCount();
   }

   public int getTotalActiveProjectIterCount()
   {
      return projectIterationDAO.getTotalActiveProjectIterCount();
   }

   public int getTotalReadOnlyProjectIterCount()
   {
      return projectIterationDAO.getTotalReadOnlyProjectIterCount();
   }

   public int getTotalObsoleteProjectIterCount()
   {
      return projectIterationDAO.getTotalObsoleteProjectIterCount();
   }

   public int getTotalTranslator()
   {
      return personDAO.getTotalTranslator();
   }

   public int getTotalDocuments()
   {
      return documentDAO.getTotalDocument();
   }

   public int getTotalActiveDocuments()
   {
      return documentDAO.getTotalActiveDocument();
   }

   public int getTotalObsoleteDocuments()
   {
      return documentDAO.getTotalObsoleteDocument();
   }

   public int getTotalTextFlows()
   {
      return textFlowDAO.getTotalTextFlows();
   }

   public int getTotalActiveTextFlows()
   {
      return textFlowDAO.getTotalActiveTextFlows();
   }

   public int getTotalObsoleteTextFlows()
   {
      return textFlowDAO.getTotalObsoleteTextFlows();
   }

   public int getTotalTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalTextFlowTargets();
   }

   public int getTotalActiveTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalTextFlowTargets();
   }

   public int getTotalObsoleteTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalTextFlowTargets();
   }

   public int getTotalApprovedTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalApprovedTextFlowTargets();
   }

   public int getTotalNeedReviewTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalNeedReviewTextFlowTargets();
   }

   public int getTotalUntranslatedTextFlowTargets()
   {
      return textFlowTargetDAO.getTotalNewTextFlowTargets();
   }
}


 