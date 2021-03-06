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
package org.zanata.webtrans.client.presenter;

import org.zanata.common.ContentState;
import org.zanata.webtrans.shared.model.DiffMode;
import org.zanata.webtrans.shared.rpc.NavOption;
import org.zanata.webtrans.shared.rpc.ThemesOption;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.inject.Singleton;


@Singleton
public class UserConfigHolder
{
   public static final Predicate<ContentState> FUZZY_OR_NEW_PREDICATE = new Predicate<ContentState>()
   {
      @Override
      public boolean apply(ContentState contentState)
      {
         return contentState == ContentState.New || contentState == ContentState.NeedReview;
      }
   };
   public static final Predicate<ContentState> FUZZY_PREDICATE = new Predicate<ContentState>()
   {
      @Override
      public boolean apply(ContentState contentState)
      {
         return contentState == ContentState.NeedReview;
      }
   };
   public static final Predicate<ContentState> NEW_PREDICATE = new Predicate<ContentState>()
   {
      @Override
      public boolean apply(ContentState contentState)
      {
         return contentState == ContentState.New;
      }
   };
   private ConfigurationState state;

   public static final int DEFAULT_DOC_LIST_PAGE_SIZE = 25;
   public static final int DEFAULT_EDITOR_PAGE_SIZE = 25;
   public static final boolean DEFAULT_SHOW_ERROR = false;
   public static final boolean DEFAULT_SHOW_SAVE_APPROVED_WARNING = true;
   public static final boolean DEFAULT_FILTER = false;
   public static final boolean DEFAULT_DISPLAY_BUTTONS = true;
   public static final boolean DEFAULT_ENTER_SAVES_APPROVED = false;
   public static final boolean DEFAULT_USE_CODE_MIRROR = false;
   public static final boolean DEFAULT_SPELL_CHECK = true;
   public static final DiffMode DEFAULT_TM_DISPLAY_MODE = DiffMode.NORMAL;
   public static final boolean DEFAULT_SHOW_PANEL = true;

   public UserConfigHolder()
   {
      // default state
      state = new ConfigurationState();
      state.displayButtons = DEFAULT_DISPLAY_BUTTONS;
      state.enterSavesApproved = DEFAULT_ENTER_SAVES_APPROVED;
      state.editorPageSize = DEFAULT_EDITOR_PAGE_SIZE;
      state.documentListPageSize = DEFAULT_DOC_LIST_PAGE_SIZE;
      state.navOption = NavOption.FUZZY_UNTRANSLATED;
      state.showError = DEFAULT_SHOW_ERROR;
      state.useCodeMirrorEditor = DEFAULT_USE_CODE_MIRROR;
      state.showSaveApprovedWarning = DEFAULT_SHOW_SAVE_APPROVED_WARNING;
      state.transMemoryDisplayMode = DEFAULT_TM_DISPLAY_MODE;

      state.filterByNeedReview = DEFAULT_FILTER;
      state.filterByTranslated = DEFAULT_FILTER;
      state.filterByUntranslated = DEFAULT_FILTER;

      state.spellCheckEnabled = DEFAULT_SPELL_CHECK;

      state.showTMPanel = DEFAULT_SHOW_PANEL;
      state.showGlossaryPanel = DEFAULT_SHOW_PANEL;
      state.showOptionalTransUnitDetails = DEFAULT_SHOW_PANEL;
      state.displayTheme = ThemesOption.THEMES_DEFAULT;
   }

   public void setEnterSavesApproved(boolean enterSavesApproved)
   {
      state = new ConfigurationState(state);
      state.enterSavesApproved = enterSavesApproved;
   }

   public void setDisplayButtons(boolean displayButtons)
   {
      state = new ConfigurationState(state);
      state.displayButtons = displayButtons;
   }

   public void setNavOption(NavOption navOption)
   {
      state = new ConfigurationState(state);
      state.navOption = navOption;
   }

   public Predicate<ContentState> getContentStatePredicate()
   {
      if (state.getNavOption() == NavOption.FUZZY_UNTRANSLATED)
      {
         return FUZZY_OR_NEW_PREDICATE;
      }
      else if (state.getNavOption() == NavOption.FUZZY)
      {
         return FUZZY_PREDICATE;
      }
      else
      {
         return NEW_PREDICATE;
      }
   }

   public void setEditorPageSize(int editorPageSize)
   {
      state = new ConfigurationState(state);
      state.editorPageSize = editorPageSize;
   }

   public void setDocumentListPageSize(int documentListPageSize)
   {
      state = new ConfigurationState(state);
      state.documentListPageSize = documentListPageSize;
   }

   public ConfigurationState getState()
   {
      return state;
   }

   /**
    * Sets all properties of the given state into this holder.
    *
    * @param state configuration state holder
    */
   public void setState( ConfigurationState state )
   {
      this.state = new ConfigurationState(state);
   }

   public void setShowError(boolean showError)
   {
      state = new ConfigurationState(state);
      state.showError = showError;
   }

   public void setUseCodeMirrorEditor(boolean useCodeMirrorEditor)
   {
      state = new ConfigurationState(state);
      state.useCodeMirrorEditor = useCodeMirrorEditor;
   }

   public void setFilterByUntranslated(boolean filterByUntranslated)
   {
      state = new ConfigurationState(state);
      state.filterByUntranslated = filterByUntranslated;
   }

   public void setFilterByNeedReview(boolean filterByNeedReview)
   {
      state = new ConfigurationState(state);
      state.filterByNeedReview = filterByNeedReview;
   }

   public void setFilterByTranslated(boolean filterByTranslated)
   {
      state = new ConfigurationState(state);
      state.filterByTranslated = filterByTranslated;
   }

   public void setShowSaveApprovedWarning(boolean showSaveApprovedWarning)
   {
      state = new ConfigurationState(state);
      state.showSaveApprovedWarning = showSaveApprovedWarning;
   }

   public void setSpellCheckEnabled(boolean spellCheckEnabled)
   {
      state = new ConfigurationState(state);
      state.spellCheckEnabled = spellCheckEnabled;
   }

   public void setTMDisplayMode(DiffMode diffMode)
   {
      state = new ConfigurationState(state);
      state.transMemoryDisplayMode = diffMode;
   }

   public void setDisplayTheme(ThemesOption theme)
   {
      state = new ConfigurationState(state);
      state.displayTheme = theme;
   }

   public void setShowTMPanel(boolean show)
   {
      state = new ConfigurationState(state);
      state.showTMPanel = show;
   }

   public void setShowGlossaryPanel(boolean show)
   {
      state = new ConfigurationState(state);
      state.showGlossaryPanel = show;
   }

   public void setShowOptionalTransUnitDetails(boolean show)
   {
      state = new ConfigurationState(state);
      state.showOptionalTransUnitDetails = show;
   }

   public boolean isAcceptAllStatus()
   {
      return state.isFilterByNeedReview() == state.isFilterByTranslated() && state.isFilterByNeedReview() == state.isFilterByUntranslated();
   }

   /**
    * Immutable object represents configuration state
    */
   public static class ConfigurationState implements IsSerializable
   {
      private boolean enterSavesApproved;
      private boolean displayButtons;
      private int editorPageSize;
      private int documentListPageSize;
      private NavOption navOption;
      private boolean showError;
      private boolean useCodeMirrorEditor;

      private boolean filterByUntranslated;
      private boolean filterByNeedReview;
      private boolean filterByTranslated;

      private boolean showSaveApprovedWarning;
      private boolean spellCheckEnabled;
      private DiffMode transMemoryDisplayMode;
      private ThemesOption displayTheme;

      private boolean showTMPanel;
      private boolean showGlossaryPanel;
      private boolean showOptionalTransUnitDetails;

      // Needed for GWT serialization
      private ConfigurationState()
      {
      }

      private ConfigurationState(ConfigurationState old)
      {
         this.enterSavesApproved = old.isEnterSavesApproved();
         this.displayButtons = old.isDisplayButtons();
         this.editorPageSize = old.getEditorPageSize();
         this.documentListPageSize = old.getDocumentListPageSize();
         this.navOption = old.getNavOption();
         this.showError = old.isShowError();
         this.useCodeMirrorEditor = old.isUseCodeMirrorEditor();
         this.filterByUntranslated = old.isFilterByUntranslated();
         this.filterByNeedReview = old.isFilterByNeedReview();
         this.filterByTranslated = old.isFilterByTranslated();
         this.showSaveApprovedWarning = old.isShowSaveApprovedWarning();
         this.spellCheckEnabled = old.isSpellCheckEnabled();
         this.transMemoryDisplayMode = old.getTransMemoryDisplayMode();
         this.displayTheme = old.getDisplayTheme();
         this.showTMPanel = old.isShowTMPanel();
         this.showGlossaryPanel = old.isShowGlossaryPanel();
         this.showOptionalTransUnitDetails = old.isShowOptionalTransUnitDetails();
      }

      public boolean isEnterSavesApproved()
      {
         return enterSavesApproved;
      }

      public boolean isDisplayButtons()
      {
         return displayButtons;
      }

      public int getEditorPageSize()
      {
         return editorPageSize;
      }

      public int getDocumentListPageSize()
      {
         return documentListPageSize;
      }

      public NavOption getNavOption()
      {
         return navOption;
      }

      public boolean isShowError()
      {
         return showError;
      }

      public boolean isUseCodeMirrorEditor()
      {
         return useCodeMirrorEditor;
      }

      public boolean isFilterByUntranslated()
      {
         return filterByUntranslated;
      }

      public boolean isFilterByNeedReview()
      {
         return filterByNeedReview;
      }

      public boolean isFilterByTranslated()
      {
         return filterByTranslated;
      }

      public boolean isShowSaveApprovedWarning()
      {
         return showSaveApprovedWarning;
      }

      public boolean isSpellCheckEnabled()
      {
         return spellCheckEnabled;
      }

      public DiffMode getTransMemoryDisplayMode()
      {
         return transMemoryDisplayMode;
      }

      public ThemesOption getDisplayTheme()
      {
         return displayTheme;
      }

      public boolean isShowTMPanel()
      {
         return showTMPanel;
      }

      public boolean isShowGlossaryPanel()
      {
         return showGlossaryPanel;
      }

      public boolean isShowOptionalTransUnitDetails()
      {
         return showOptionalTransUnitDetails;
      }
   }
}
