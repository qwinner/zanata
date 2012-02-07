package org.zanata.webtrans.client.resources;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface TableEditorMessages extends Messages
{

   @DefaultMessage("Source comment: ")
   String sourceCommentLabel();

   @DefaultMessage("Warning: This Translation Unit is being edited by someone else.")
   String notifyInEdit();

   @DefaultMessage("Message has been copied to the target.")
   String notifyCopied();

   @DefaultMessage("Please open the target in the editor first.")
   String notifyUnopened();

   @DefaultMessage("No selected text flow to copy to.")
   String noTextFlowToCopy();

   @DefaultMessage("Not logged in!")
   String notifyNotLoggedIn();

   @DefaultMessage("Failed to load data from Server")
   String notifyLoadFailed();

   @DefaultMessage("An unknown error occurred")
   String notifyUnknownError();

   @DefaultMessage("Failed to update Translation Unit: {0}")
   String notifyUpdateFailed(String errorMessage);

   @DefaultMessage("Saved change to Translation Unit")
   String notifyUpdateSaved();

   @DefaultMessage("Validation error - See validation message")
   String notifyValidationError();

   @DefaultMessage("Failed to Stop Editing TransUnit")
   String notifyStopFailed();

   @DefaultMessage("Failed to Lock TransUnit")
   String notifyLockFailed();
}