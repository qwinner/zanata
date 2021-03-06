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
package org.zanata.page.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.zanata.page.AbstractPage;

public class CreateVersionPage extends AbstractPage
{
   @FindBy(id = "iterationForm:slugField:slug")
   private WebElement versionIdField;

   @FindBy(id = "iterationForm:projectTypeField:projectType")
   private WebElement projectTypeSelection;

   @FindBy(id = "iterationForm:statusField:status")
   private WebElement statusSelection;

   @FindBy(id = "iterationForm:save")
   private WebElement saveButton;

   public CreateVersionPage(final WebDriver driver)
   {
      super(driver);
   }

   public CreateVersionPage inputVersionId(String versionId)
   {
      versionIdField.sendKeys(versionId);
      return this;
   }

   public CreateVersionPage selectProjectType(String projectType)
   {
      new Select(projectTypeSelection).selectByVisibleText(projectType);
      return this;
   }

   public CreateVersionPage selectStatus(String status)
   {
      new Select(statusSelection).selectByVisibleText(status);
      return this;
   }

   public ProjectVersionPage saveVersion()
   {
      clickSaveAndCheckErrors(saveButton);
      return new ProjectVersionPage(getDriver());
   }
}
