package net.openl10n.flies.webtrans.shared.model;

import java.io.Serializable;

import net.openl10n.flies.common.TransUnitCount;

public class DocumentStatus implements Serializable
{
   private static final long serialVersionUID = 1L;

   TransUnitCount count;
   private DocumentId documentid;

   private DocumentStatus()
   {
   }

   public DocumentStatus(DocumentId id, TransUnitCount count)
   {
      this.documentid = id;
      this.count = count;
   }

   public void setDocumentid(DocumentId documentid)
   {
      this.documentid = documentid;
   }

   public DocumentId getDocumentid()
   {
      return documentid;
   }

   public TransUnitCount getCount()
   {
      return count;
   }
}