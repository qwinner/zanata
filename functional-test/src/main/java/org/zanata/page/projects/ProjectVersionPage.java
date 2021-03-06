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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.zanata.page.AbstractPage;
import org.zanata.page.webtrans.DocumentsViewPage;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectVersionPage extends AbstractPage
{
   @FindBy(className = "rich-table-row")
   private List<WebElement> localeTableRows;

   public ProjectVersionPage(final WebDriver driver)
   {
      super(driver);
   }

   @SuppressWarnings("unused")
   public List<String> getTranslatableLocales()
   {
      List<String> rows = Lists.transform(localeTableRows, new Function<WebElement, String>()
      {
         @Override
         public String apply(WebElement tr)
         {
            log.debug("table row: {}", tr.getText());
            List<WebElement> links = tr.findElements(By.tagName("a"));
            return getLocaleLinkText(links.get(0));
         }
      });

      return ImmutableList.copyOf(rows);
   }

   @SuppressWarnings("unused")
   public List<String> getTranslatableLanguages()
   {
      List<String> rows = Lists.transform(localeTableRows, new Function<WebElement, String>()
      {
         @Override
         public String apply(WebElement tr)
         {
            log.debug("table row: {}", tr.getText());
            WebElement nativeName = tr.findElement(By.className("nativeName"));
            return nativeName.getText();
         }
      });

      return ImmutableList.copyOf(rows);
   }

   private static String getLocaleLinkText(WebElement languageLink)
   {
      String nativeName = languageLink.findElement(By.className("nativeName")).getText();
      return languageLink.getText().replace(nativeName, "");
   }

   public DocumentsViewPage translate(String locale)
   {

      for (WebElement tableRow : localeTableRows)
      {
         List<WebElement> links = tableRow.findElements(By.tagName("a"));
         Preconditions.checkState(links.size() == 2, "each translatable locale row should have 2 links");
         WebElement localeCell = links.get(0);
         if (getLocaleLinkText(localeCell).equals(locale))
         {
            localeCell.click();
            return new DocumentsViewPage(getDriver());
         }
      }
      throw new IllegalArgumentException("can not translate locale: " + locale);
   }
}
