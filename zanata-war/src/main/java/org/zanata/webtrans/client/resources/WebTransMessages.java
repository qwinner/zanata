package org.zanata.webtrans.client.resources;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface WebTransMessages extends Messages
{
   @DefaultMessage("Save")
   String save();

   @DefaultMessage("(No Content)")
   String noContent();

   @DefaultMessage("{0}% ({1,number,#.#}hrs) {2}")
   String statusBarPercentageHrs(int approved, double remainingHours, String by);

   @DefaultMessage("{0,number,#.#}")
   String statusBarLabelHours(double remainingHours);

   @DefaultMessage("{0}%")
   String statusBarLabelPercentage(int approved);

   @DefaultMessage("http://zanata.org/")
   String hrefHelpLink();

   @DefaultMessage("{0} to {1} - Zanata Web Translation")
   String windowTitle(String workspaceName, String localeName);

   @DefaultMessage("{0} to {1} - {2}")
   String windowTitle2(String workspaceName, String localeName, String title);

   @DefaultMessage("First Page")
   String firstPage();

   @DefaultMessage("Previous Page")
   String prevPage();

   @DefaultMessage("Next Page")
   String nextPage();

   @DefaultMessage("Last Page")
   String lastPage();

   @DefaultMessage("Show Translation Suggestions")
   String showTranslationMemoryPanel();

   @DefaultMessage("Show Glossary Suggestions")
   String showGlossaryPanel();

   @DefaultMessage("Chat room")
   String chatRoom();

   @DefaultMessage("Hide Translation Memory and Glossary")
   String hideSouthPanel();

   @DefaultMessage("Restore Translation Memory and Glossary")
   String restoreSouthPanel();

   @DefaultMessage("Load")
   String load();

   @DefaultMessage("Restore Defaults")
   String restoreDefaults();

   @DefaultMessage("Loading")
   String loading();

   @DefaultMessage("Failed to load document from Server")
   String loadDocFailed();

   @DefaultMessage("Path")
   String columnHeaderPath();

   @DefaultMessage("Document")
   String columnHeaderDocument();

   @DefaultMessage("Statistic ({0})")
   String columnHeaderStatistic(String option);

   @DefaultMessage("Translated ({0})")
   String columnHeaderTranslated(String option);

   @DefaultMessage("Untranslated ({0})")
   String columnHeaderUntranslated(String option);

   @DefaultMessage("Remaining hours")
   String columnHeaderRemaining();

   @DefaultMessage("No document selected")
   String noDocumentSelected();

   @DefaultMessage("Project-wide Search & Replace")
   String projectWideSearchAndReplace();
   
   @DefaultMessage("Document List")
   String documentListTitle();

   @DefaultMessage("{0} has quit workspace")
   String hasQuitWorkspace(String user);

   @DefaultMessage("{0} has joined workspace")
   String hasJoinedWorkspace(String user);

   @DefaultMessage("Searching")
   String searching();

   @DefaultMessage("Search found results in {0} documents")
   @AlternateMessage({ "one", "Search found results in 1 document" })
   String searchFoundResultsInDocuments(@PluralCount int numDocs);

   @DefaultMessage("Showing results for search \"{0}\" ({1} text flows in {2} documents)")
   @AlternateMessage({
      "one|one", "Showing results for search \"{0}\" (1 text flow in 1 document)",
      "other|one", "Showing results for search \"{0}\" ({1} text flows in 1 document)"})
   String showingResultsForProjectWideSearch(String searchString, @PluralCount int textFlows, @PluralCount int documents);

   @DefaultMessage("Search \"{0}\" returned no results")
   String searchForPhraseReturnedNoResults(String searchString);

   @DefaultMessage("There are no search results to display")
   String noSearchResults();

   @DefaultMessage("Search failed")
   String searchFailed();

   @DefaultMessage("Preview")
   String fetchPreview();

   @DefaultMessage("Hide preview")
   String hidePreview();

   @DefaultMessage("Preview replacement in all selected text flows.")
   String previewSelectedDescription();

   @DefaultMessage("Select text flows to enable preview.")
   String previewSelectedDisabledDescription();

   @DefaultMessage("Previewing")
   String fetchingPreview();

   @DefaultMessage("Fetched preview")
   String fetchedPreview();

   @DefaultMessage("Failed to fetch preview")
   String previewFailed();

   @DefaultMessage("Replace")
   String replace();

   @DefaultMessage("Replace text in all selected text flows.")
   String replaceSelectedDescription();

   @DefaultMessage("Select text flows and view preview to enable replace.")
   String replaceSelectedDisabledDescription();

   @DefaultMessage("Preview is required before replacing text")
   String previewRequiredBeforeReplace();

   @DefaultMessage("Replacing")
   String replacing();

   @DefaultMessage("Replaced")
   String replaced();

   @DefaultMessage("Replaced \"{0}\" with \"{1}\" in {2} row {3} (\"{4}...\")")
   String replacedTextInOneTextFlow(String searchText, String replacement, String docName, int oneBasedRowIndex, String truncatedText);

   @DefaultMessage("Replaced \"{0}\" with \"{1}\" in {2} text flows")
   @AlternateMessage({ "one", "Replaced \"{0}\" with \"{1}\" in 1 text flow" })
   String replacedTextInMultipleTextFlows(String searchText, String replacement, @PluralCount int numFlows);

   @DefaultMessage("Successfully replaced text")
   String replacedTextSuccess();

   @DefaultMessage("Replace text failed")
   String replaceTextFailure();

   @DefaultMessage("No replacement text has been entered")
   String noReplacementPhraseEntered();

   @DefaultMessage("No replacements to make")
   String noReplacementsToMake();

   @DefaultMessage("Replace not possible in read-only workspace")
   String cannotReplaceInReadOnlyMode();

   @DefaultMessage("View in editor")
   String viewDocInEditor();

   @DefaultMessage("Show this document in editor view")
   String viewDocInEditorDetailed();

   @DefaultMessage("Search document in editor")
   String searchDocInEditor();

   @DefaultMessage("Show this document in the editor with the current search active")
   String searchDocInEditorDetailed();

   @DefaultMessage("Index")
   String rowIndex();

   @DefaultMessage("Source")
   String source();

   @DefaultMessage("Target")
   String target();

   @DefaultMessage("Actions")
   String actions();

   @DefaultMessage("Undo")
   String undo();

   @DefaultMessage("Undoing")
   String undoInProgress();

   @DefaultMessage("Undo replacement failed")
   String undoReplacementFailure();

   @DefaultMessage("Undo successful")
   String undoSuccess();

   // @formatter:off
   @Description("Message for unsuccessful undo")
   @DefaultMessage("{0} items can not be undone. {1} items are reverted")
   @AlternateMessage({
         "one|=0", "Item can not be undone",
         "other|=0", "Items can not be undone"
   })
   String undoUnsuccessful(@PluralCount int unsuccessfulCount, @PluralCount int successfulCount);
   // @formatter:on

   @DefaultMessage("Undo failed")
   String undoFailure();

   @DefaultMessage("Undo not possible in read-only workspace")
   String cannotUndoInReadOnlyMode();

   @DefaultMessage("Select entire document")
   String selectAllInDocument();

   @DefaultMessage("Select or deselect all matching text flows in this document")
   String selectAllInDocumentDetailed();

   @DefaultMessage("No text flows selected")
   String noTextFlowsSelected();

   @DefaultMessage("{0} text flows selected")
   @AlternateMessage({ "one", "1 text flow selected" })
   String numTextFlowsSelected(@PluralCount int selectedFlows);

   @DefaultMessage("Notification")
   String notification();

   @DefaultMessage("Available Keyboard Shortcuts")
   String availableKeyShortcutsTitle();

   @DefaultMessage("Select text flows in all documents")
   String selectAllTextFlowsKeyShortcut();

   @DefaultMessage("Focus search phrase")
   String focusSearchPhraseKeyShortcut();

   @DefaultMessage("Focus replacement phrase")
   String focusReplacementPhraseKeyShortcut();

   @DefaultMessage("Show available shortcuts")
   String showAvailableKeyShortcuts();

   @DefaultMessage("Application")
   String applicationScope();

   @DefaultMessage("Editing text")
   String editScope();

   @DefaultMessage("Editor navigation")
   String navigationScope();
   
   @DefaultMessage("Translation memory")
   String tmScope();
   
   @DefaultMessage("Glossary")
   String glossaryScope();

   @DefaultMessage("Toggle individual row action buttons (changes for next search)")
   String toggleRowActionButtons();

   @DefaultMessage("Replace text in selected rows")
   String replaceSelectedKeyShortcut();

   @DefaultMessage("Show document list")
   String showDocumentListKeyShortcut();

   @DefaultMessage("Show editor view")
   String showEditorKeyShortcut();

   @DefaultMessage("Show project-wide search view")
   String showProjectWideSearch();

   @DefaultMessage("Warning! This is a public channel")
   String thisIsAPublicChannel();

   @DefaultMessage("Only show documents that contain the search text with matching case")
   String docListFilterCaseSensitiveDescription();

   @DefaultMessage("Only show documents with full path and name in the search text")
   String docListFilterExactMatchDescription();

   @DefaultMessage("Enter text to filter the document list. Use commas (,) to separate multiple searches")
   String docListFilterDescription();

   @DefaultMessage("Disable 'Replace' button until previews have been generated for all selected text flows")
   String requirePreviewDescription();

   @DefaultMessage("Navigate to next row")
   String navigateToNextRow();
   
   @DefaultMessage("Navigate to previous row")
   String navigateToPreviousRow();

   @DefaultMessage("Open editor in selected row")
   String openEditorInSelectedRow();

   @DefaultMessage("Close keyboard shortcuts list")
   String closeShortcutView();
   
   @DefaultMessage("Search translation memory")
   String searchTM();

   @DefaultMessage("Search glossary")
   String searchGlossary();

   @DefaultMessage("Chat")
   String chatScope();

   @DefaultMessage("Publish chat content")
   String publishChatContent();

   @DefaultMessage("Version")
   String versionNumber();

   @DefaultMessage("User")
   String modifiedBy();

   @DefaultMessage("Date")
   String modifiedDate();

   @DefaultMessage("Paste into Editor")
   String pasteIntoEditor();

   @DefaultMessage("Translation History")
   String translationHistory();

   @Description("Tab text for translation history comparison")
   @DefaultMessage("Compare ver. {0} and {1}")
   String translationHistoryComparison(String versionOne, String versionTwo);

   @DefaultMessage("Select 2 entries to compare")
   String translationHistoryComparisonTitle();

   @Description("latest version in translation history")
   @DefaultMessage("{0} Latest")
   String latestVersion(String versionNum);

   @Description("current unsaved value in editor for translation history display")
   @DefaultMessage("Unsaved")
   String unsaved();

   @DefaultMessage("Flip entries")
   String flipComparingEntries();


   @DefaultMessage("Workspace is set to read only")
   String notifyReadOnlyWorkspace();

   @DefaultMessage("Workspace is set to edit mode")
   String notifyEditableWorkspace();


   @DefaultMessage("Close")
   String close();

   @DefaultMessage("Options")
   String options();

   @DefaultMessage("Editor options")
   String editorOptions();

   @DefaultMessage("Validation options")
   String validationOptions();

   @DefaultMessage("Navigation key/button")
   String navOption();

   @DefaultMessage("Page size")
   String pageSize(); 

   @DefaultMessage("Read only")
   String readOnly();

   @DefaultMessage("Advanced user configuration")
   String otherConfiguration();

   @DefaultMessage("When unexpected error happens, a popup window will display and show it")
   String showErrorsTooltip();

   @DefaultMessage("Switch between CodeMirror Editor (supports syntax highlight) and plain textarea (no syntax highlight but support spell check)")
   String useCodeMirrorEditorTooltip();

   @DefaultMessage("Show warning when 'Save as Approved' triggered via keyboard shortcut")
   String showSaveApprovedWarningTooltip();

   @DefaultMessage("Go to row if on current page")
   String goToRowOnCurrentPage();

   @DefaultMessage("Style")
   String style();

   @DefaultMessage("Description")
   String description();

   @DefaultMessage("Blue color")
   String blueColor();

   @DefaultMessage("Red color + crossed out")
   String redColorCrossedOut();

   @DefaultMessage("Plain text")
   String plainText();

   @DefaultMessage("Text contain in result but not in search term")
   String tmInsertTagDesc();

   @DefaultMessage("Text contain in search term but not in result")
   String tmDelTagDesc();

   @DefaultMessage("Text contain in both search term and result")
   String tmPlainTextDesc();

   @DefaultMessage("New replacement text ")
   String searchReplaceInsertTagDesc();

   @DefaultMessage("Old text to be replaced")
   String searchReplaceDelTagDesc();

   @DefaultMessage("No changes")
   String searchReplacePlainTextDesc();

   @DefaultMessage("Color legend")
   String colorLegend();

   @DefaultMessage("Concurrent edit detected. Reset value for current row.")
   String concurrentEdit();

   @DefaultMessage("Other user has saved a newer version (Latest) while you are editing (Unsaved). Please resolve conflicts.")
   String concurrentEditTitle();

   @DefaultMessage("Editor")
   String editor();

   @DefaultMessage("By words")
   String byWords();

   @DefaultMessage("By message")
   String byMessage();

   @DefaultMessage("Refresh current page")
   String refreshCurrentPage();

   @DefaultMessage("Enable spell check in editor (Firefox only)")
   String spellCheckTooltip();

   @DefaultMessage("Translation Memory (TM) options")
   String transMemoryOption();

   @DefaultMessage("Display configuration")
   String displayConfiguration();

   @DefaultMessage("Configure how you want your editor to look like")
   String displayConfigurationTooltip();

   @DefaultMessage("Display optional Trans Unit Details")
   String showTransUnitDetails();

   @DefaultMessage("Only display Translation Unit Details when there is meta data otherwise hide it")
   String showTransUnitDetailsTooltip();
}
