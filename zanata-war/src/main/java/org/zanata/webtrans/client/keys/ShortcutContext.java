/*
 * Copyright 2012, Red Hat, Inc. and individual contributors
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
package org.zanata.webtrans.client.keys;

import org.zanata.webtrans.client.presenter.KeyShortcutPresenter;

/**
 * View contexts within the application that may have key shortcuts bound and
 * may be activated and deactivated to enable or disable bound shortcuts.
 * 
 * @see KeyShortcutPresenter#register(KeyShortcut)
 * @see KeyShortcutPresenter#setContextActive(ShortcutContext, boolean)
 */
public enum ShortcutContext
{

   /**
    * For shortcuts that should always be active. Presenters should not
    * deactivate this context.
    */
   Application,

   /**
    * Used by {@link SearchResultsPresenter}
    */
   ProjectWideSearch,

   /**
    * Used by {@link TableEditorPresenter}
    */
   Navigation,

   /**
    * Used by {@link InlineTargetCellEditor}
    */
   Edit,
   
   /**
    * Used by {@link TransMemoryPresenter}
    */
   TM,
   
   /**
    * Used by {@link GlossaryPresenter}
    */
   Glossary,
   
   /**
    * Used by {@link WorkspaceUsersPresenter}
    */
   Chat;
}