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
package org.zanata.service.impl;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.xml.sax.InputSource;
import org.zanata.adapter.DTDAdapter;
import org.zanata.adapter.FileFormatAdapter;
import org.zanata.adapter.OpenOfficeAdapter;
import org.zanata.adapter.PlainTextAdapter;
import org.zanata.adapter.po.PoReader2;
import org.zanata.common.DocumentType;
import org.zanata.common.LocaleId;
import org.zanata.dao.DocumentDAO;
import org.zanata.exception.FileFormatAdapterException;
import org.zanata.exception.ZanataServiceException;
import org.zanata.model.HDocument;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.TranslationsResource;
import org.zanata.service.TranslationFileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.seam.ScopeType.STATELESS;

/**
 * Default implementation of the TranslationFileService interface.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("translationFileServiceImpl")
@Scope(STATELESS)
@AutoCreate
public class TranslationFileServiceImpl implements TranslationFileService
{
   private static String[] ODF_EXTENSIONS = {"odt", "fodt", "odp", "fodp", "ods", "fods", "odg", "fodg", "odb", "odf"};

   private static Set<String> SUPPORTED_EXTENSIONS = buildSupportedExtensionSet();

   private static Set<String> buildSupportedExtensionSet()
   {
      Set<String> supported = new HashSet<String>(Arrays.asList(ODF_EXTENSIONS));
      supported.add("txt");
      supported.add("dtd");
      return supported;
   }

   @Logger
   Log log;

   @In
   private DocumentDAO documentDAO;

   @Override
   public TranslationsResource parseTranslationFile(InputStream fileContents, String fileName, String localeId) throws ZanataServiceException
   {
      if( fileName.endsWith(".po") )
      {
         try
         {
            return parsePoFile(fileContents);
         }
         catch (Exception e)
         {
            throw new ZanataServiceException("Invalid PO file contents on file: " + fileName, e);
         }
      }
      else if (hasAdapterFor(fileName))
      {
         File tempFile = persistToTempFile(fileContents);
         TranslationsResource transRes;
         try
         {
            transRes = getAdapterFor(fileName).parseTranslationFile(tempFile.toURI(), localeId);
         }
         catch (FileFormatAdapterException e)
         {
            throw new ZanataServiceException("Error parsing translation file: " + fileName, e);
         }
         removeTempFile(tempFile);
         return transRes;
      }
      else
      {
         throw new ZanataServiceException("Unsupported Translation file: " + fileName);
      }
   }

   @Override
   public Resource parseDocumentFile(InputStream fileContents, String path, String fileName)
   {
      String docName = fileName;
      if (docName.endsWith(".pot"))
      {
         docName = docName.substring(0, docName.lastIndexOf('.'));
      }

      return parseUpdatedDocumentFile(fileContents, convertToValidPath(path) + docName, fileName);
   }

   @Override
   public Resource parseUpdatedDocumentFile(InputStream fileContents, String docId, String fileName)
   {
      if (fileName.endsWith(".pot"))
      {
         try
         {
            return parsePotFile(fileContents, docId);
         }
         catch (Exception e)
         {
            throw new ZanataServiceException("Invalid POT file contents on file: " + fileName, e);
         }
      }
      else
      {
         throw new ZanataServiceException("Unsupported Document file: " + fileName);
      }
   }

   @Override
   public Resource parseDocumentFile(URI documentFile, String path, String fileName) throws ZanataServiceException
   {
      return parseUpdatedDocumentFile(documentFile, convertToValidPath(path) + fileName, fileName);
   }

   @Override
   public Resource parseUpdatedDocumentFile(URI documentFile, String docId, String fileName) throws ZanataServiceException
   {
      if (hasAdapterFor(fileName))
      {
         FileFormatAdapter adapter = getAdapterFor(fileName);
         Resource doc;
         try
         {
            doc = adapter.parseDocumentFile(documentFile, new LocaleId("en"));
         }
         catch (FileFormatAdapterException e)
         {
            throw new ZanataServiceException("Error parsing document file: " + fileName, e);
         }
         doc.setName(docId);
         return doc;
      }
      else
      {
         throw new ZanataServiceException("Unsupported Document file: " + fileName);
      }
   }

   /**
    * A valid path is either empty, or has a trailing slash and no leading slash.
    * 
    * @param path
    * @return valid path
    */
   private String convertToValidPath(String path)
   {
      path = path.trim();
      while( path.startsWith("/") )
      {
         path = path.substring(1);
      }
      if( path.length() > 0 && !path.endsWith("/") )
      {
         path = path.concat("/");
      }
      return path;
   }

   private TranslationsResource parsePoFile( InputStream fileContents )
   {
      PoReader2 poReader = new PoReader2();
      return poReader.extractTarget(new InputSource(fileContents) );
   }

   private Resource parsePotFile(InputStream fileContents, String docId)
   {
      PoReader2 poReader = new PoReader2();
      // assume english as source locale
      Resource res = poReader.extractTemplate(new InputSource(fileContents), new LocaleId("en"), docId);
      return res;
   }

   // TODO replace these with values from DocumentType
   @Override
   public Set<String> getSupportedExtensions()
   {
      return SUPPORTED_EXTENSIONS;
   }

   @Override
   public boolean hasAdapterFor(String fileNameOrExtension)
   {
      return SUPPORTED_EXTENSIONS.contains(extractExtension(fileNameOrExtension));
   }

   @Override
   public FileFormatAdapter getAdapterFor(String fileNameOrExtension)
   {
      // FIXME throw exception when not found

      String extension = extractExtension(fileNameOrExtension);
      if (extension == null)
      {
         return null;
      }
      else
      {
         if (extension.equals("txt"))
         {
            return new PlainTextAdapter();
         }
         else if (extension.equals("dtd"))
         {
            return new DTDAdapter();
         }
         else if (Arrays.asList(ODF_EXTENSIONS).contains(extension))
         {
            return new OpenOfficeAdapter();
         }
         else
         {
            return null;
         }
      }
   }

   @Override
   public FileFormatAdapter getAdapterFor(DocumentType type)
   {
      switch (type)
      {
      case PLAIN_TEXT:
      {
         return new PlainTextAdapter();
      }
      case XML_DOCUMENT_TYPE_DEFINITION:
      {
         return new DTDAdapter();
      }
      case OPEN_DOCUMENT_DATABASE:
      case OPEN_DOCUMENT_FORMULA:
      case OPEN_DOCUMENT_GRAPHICS:
      case OPEN_DOCUMENT_GRAPHICS_FLAT:
      case OPEN_DOCUMENT_PRESENTATION:
      case OPEN_DOCUMENT_PRESENTATION_FLAT:
      case OPEN_DOCUMENT_SPREADSHEET:
      case OPEN_DOCUMENT_SPREADSHEET_FLAT:
      case OPEN_DOCUMENT_TEXT:
      case OPEN_DOCUMENT_TEXT_FLAT:
      {
         return new OpenOfficeAdapter();
      }
      default:
      {
         throw new RuntimeException("no adapter for document type " + type);
      }
      }
   }

   @Override
   public String extractExtension(String fileNameOrExtension)
   {
      if (fileNameOrExtension == null || fileNameOrExtension.length() == 0 || fileNameOrExtension.endsWith("."))
      {
         // could throw exception here
         return null;
      }

      String extension;
      if (fileNameOrExtension.contains("."))
      {
         extension = fileNameOrExtension.substring(fileNameOrExtension.lastIndexOf('.') + 1);
      }
      else
      {
         extension = fileNameOrExtension;
      }
      return extension;
   }

   @Override
   public File persistToTempFile(InputStream fileContents)
   {
      File tempFile = null;
      try
      {
         tempFile = File.createTempFile("zupload", ".tmp");
         byte[] buffer = new byte[4096]; // To hold file contents
         int bytesRead;
         FileOutputStream output = new FileOutputStream(tempFile);
         while ((bytesRead = fileContents.read(buffer)) != -1)
         {
            output.write(buffer, 0, bytesRead);
         }
         output.close();
      }
      catch (IOException e)
      {
         throw new ZanataServiceException("Error while writing uploaded file to temporary location", e);
      }
      return tempFile;
   }

   @Override
   public void removeTempFile(File tempFile)
   {
      if (tempFile != null)
      {
         if (!tempFile.delete())
         {
            log.warn("unable to remove temporary file {}, marking for delete on exit", tempFile.getAbsolutePath());
            tempFile.deleteOnExit();
         }
      }
   }

   @Override
   public boolean hasPersistedDocument(String projectSlug, String iterationSlug, String docPath, String docName)
   {
      HDocument doc = documentDAO.getByProjectIterationAndDocId(projectSlug, iterationSlug, docPath+docName);
      return doc.getRawDocument() != null;
   }

   @Override
   public String getFileExtension(String projectSlug, String iterationSlug, String docPath, String docName)
   {
      HDocument doc = documentDAO.getByProjectIterationAndDocId(projectSlug, iterationSlug, docPath+docName);
      return doc.getRawDocument().getType().getExtension();
   }

}
