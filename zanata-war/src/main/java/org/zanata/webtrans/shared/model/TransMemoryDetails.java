package org.zanata.webtrans.shared.model;

import com.google.common.base.Objects;
import com.google.gwt.user.client.rpc.IsSerializable;

public class TransMemoryDetails implements IsSerializable
{

   private String sourceComment;
   private String targetComment;
   private String projectName;
   private String iterationName;
   private String docId;
   private String resId;
   private String msgContext;
   private String lastModifiedBy;
   private String lastModifiedDate;

   @SuppressWarnings("unused")
   private TransMemoryDetails()
   {
   }

   public TransMemoryDetails(String sourceComment, String targetComment, String projectName, String iterationName, String docId, String resId, String msgContext, String lastModifiedBy, String lastModifiedDate)
   {
      this.sourceComment = sourceComment;
      this.targetComment = targetComment;
      this.projectName = projectName;
      this.iterationName = iterationName;
      this.docId = docId;
      this.resId = resId;
      this.msgContext = msgContext;
      this.lastModifiedBy = lastModifiedBy;
      this.lastModifiedDate = lastModifiedDate;
   }

   public String getSourceComment()
   {
      return sourceComment;
   }

   public String getTargetComment()
   {
      return targetComment;
   }

   public String getProjectName()
   {
      return projectName;
   }

   public String getIterationName()
   {
      return iterationName;
   }

   public String getDocId()
   {
      return docId;
   }


   public String getResId()
   {
      return resId;
   }

   public String getMsgContext()
   {
      return msgContext;
   }

   public String getLastModifiedBy()
   {
      return lastModifiedBy;
   }

   public String getLastModifiedDate()
   {
      return lastModifiedDate;
   }

   @Override
   public String toString()
   {
      // @formatter:off
      return Objects.toStringHelper(this).
            add("sourceComment", sourceComment).
            add("targetComment", targetComment).
            add("projectName", projectName).
            add("iterationName", iterationName).
            add("docId", docId).
            add("resId", resId).
            add("msgContext", msgContext).
            add("lastModifiedBy", lastModifiedBy).
            add("lastModifiedDate", lastModifiedDate).
            toString();
      // @formatter:on
   }
}
