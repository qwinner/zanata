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
package org.zanata.webtrans.shared.validation;

import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.webtrans.client.resources.ValidationMessages;
import org.zanata.webtrans.shared.validation.action.HtmlXmlTagValidation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
@Test(groups = { "unit-tests" })
public class HtmlXmlTagValidationTests
{
   // TODO use TestMessages

   // mock message strings
   private static final String MOCK_XML_HTML_VALIDATOR_DESCRIPTION = "test xml html validator description";
   private static final String MOCK_XML_HTML_VALIDATOR_NAME = "test xml html validator name";
   private static final String MOCK_TAGS_OUT_OF_ORDER_MESSAGE = "mock tags out of order message";
   private static final String MOCK_TAGS_MISSING_MESSAGE = "mock tags missing message";
   private static final String MOCK_TAGS_ADDED_MESSAGE = "mock tags added message";

   private HtmlXmlTagValidation htmlXmlTagValidation;

   @Mock
   private ValidationMessages mockMessages;

   // captured tag lists sent to messages
   @Captor
   private ArgumentCaptor<List<String>> capturedTagsAdded;
   @Captor
   private ArgumentCaptor<List<String>> capturedTagsMissing;
   @Captor
   private ArgumentCaptor<List<String>> capturedTagsOutOfOrder;

   @BeforeMethod
   public void init()
   {
      MockitoAnnotations.initMocks(this);
      htmlXmlTagValidation = null;
      when(mockMessages.tagsAdded(capturedTagsAdded.capture())).thenReturn(MOCK_TAGS_ADDED_MESSAGE);
      when(mockMessages.tagsMissing(capturedTagsMissing.capture())).thenReturn(MOCK_TAGS_MISSING_MESSAGE);
      when(mockMessages.tagsWrongOrder(capturedTagsOutOfOrder.capture())).thenReturn(MOCK_TAGS_OUT_OF_ORDER_MESSAGE);
      when(mockMessages.xmlHtmlValidatorName()).thenReturn(MOCK_XML_HTML_VALIDATOR_NAME);
      when(mockMessages.xmlHtmlValidatorDescription()).thenReturn(MOCK_XML_HTML_VALIDATOR_DESCRIPTION);
   }

   @Test
   public void idIsSet()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      assertThat(htmlXmlTagValidation.getId(), is(MOCK_XML_HTML_VALIDATOR_NAME));
   }

   @Test
   public void descriptionIsSet()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      assertThat(htmlXmlTagValidation.getDescription(), is(MOCK_XML_HTML_VALIDATOR_DESCRIPTION));
   }

   @Test
   public void matchingHtmlNoError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<html><title>HTML TAG Test</title><table><tr><td>column 1 row 1</td><td>column 2 row 1</td></tr></table></html>";
      String target = "<html><title>HTML TAG Test</title><table><tr><td>column 1 row 1</td><td>column 2 row 1</td></tr></table></html>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(false));
      assertThat(htmlXmlTagValidation.getError().size(), is(0));
   }

   @Test
   public void matchingXmlNoError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<group><users><user>name</user></users></group>";
      String target = "<group><users><user>nombre</user></users></group>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(false));
      assertThat(htmlXmlTagValidation.getError().size(), is(0));
   }

   @Test
   public void addedTagError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<group><users><user>1</user></users></group>";
      String target = "<group><users><user>1</user></users><foo></group>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_ADDED_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsAdded.getValue(), hasItem("<foo>"));
      assertThat(capturedTagsAdded.getValue().size(), is(1));
   }

   @Test
   public void addedTagsError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<group><users><user>1</user></users></group>";
      String target = "<foo><group><users><bar><user>1</user></users></group><moo>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_ADDED_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsAdded.getValue(), hasItems("<foo>", "<bar>", "<moo>"));
      assertThat(capturedTagsAdded.getValue().size(), is(3));
   }

   @Test
   public void missingTagError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<html><title>HTML TAG Test</title><foo><table><tr><td>column 1 row 1</td><td>column 2 row 1</td></tr></table></html>";
      String target = "<html><title>HTML TAG Test</title><table><tr><td>column 1 row 1</td><td>column 2 row 1</td></tr></table></html>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_MISSING_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsMissing.getValue(), hasItem("<foo>"));
      assertThat(capturedTagsMissing.getValue().size(), is(1));
   }

   @Test
   public void missingTagsError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<html><title>HTML TAG Test</title><p><table><tr><td>column 1 row 1</td></tr></table></html>";
      String target = "<title>HTML TAG Test</title><table><tr><td>column 1 row 1</td></tr></table>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_MISSING_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsMissing.getValue(), hasItems("<html>", "<p>", "</html>"));
      assertThat(capturedTagsMissing.getValue().size(), is(3));
   }

   @Test
   public void orderOnlyValidatedWithSameTags()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five>";
      String target = "<two></five></four><three><six>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_MISSING_MESSAGE));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_ADDED_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(2));

      assertThat(capturedTagsMissing.getValue(), hasItem("<one>"));
      assertThat(capturedTagsMissing.getValue().size(), is(1));
      assertThat(capturedTagsAdded.getValue(), hasItem("<six>"));
      assertThat(capturedTagsAdded.getValue().size(), is(1));
   }

   @Test
   public void lastTagMovedToFirstError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "<six><one><two><three></four></five>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsOutOfOrder.getValue(), hasItem("<six>"));
      assertThat("when one tag has moved, only that tag should be reported out of order", capturedTagsOutOfOrder.getValue().size(), is(1));
   }

   @Test
   public void firstTagMovedToLastError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "<two><three></four></five><six><one>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsOutOfOrder.getValue(), hasItem("<one>"));
      assertThat("when one tag has moved, only that tag should be reported out of order", capturedTagsOutOfOrder.getValue().size(), is(1));
   }

   @Test
   public void tagMovedToMiddleError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "<two><three><one></four></five><six>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsOutOfOrder.getValue(), hasItem("<one>"));
      assertThat("when one tag has moved, only that tag should be reported out of order", capturedTagsOutOfOrder.getValue().size(), is(1));
   }

   @Test
   public void reversedTagsError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "<six></five></four><three><two><one>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      // <one> is the first in-order tag, so is not reported
      assertThat(capturedTagsOutOfOrder.getValue(), hasItems("<six>", "</five>", "</four>", "<three>", "<two>"));
      assertThat(capturedTagsOutOfOrder.getValue().size(), is(5));
   }

   @Test
   public void reportFirstTagsOutOfOrder()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "</four></five><six><one><two><three>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsOutOfOrder.getValue(), hasItems("</four>", "</five>", "<six>"));
      assertThat(capturedTagsOutOfOrder.getValue().size(), is(3));
   }

   @Test
   public void reportLeastTagsOutOfOrder()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></four></five><six>";
      String target = "<six></four></five><one><two><three>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      // <one><two><three> in order
      // should not use </four></five> as there are less tags
      assertThat("should report the least number of tags to move to restore order", capturedTagsOutOfOrder.getValue(), hasItems("</four>", "</five>", "<six>"));
      assertThat(capturedTagsOutOfOrder.getValue().size(), is(3));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void swapSomeTagsError()
   {
      htmlXmlTagValidation = new HtmlXmlTagValidation(mockMessages);
      String source = "<one><two><three></three></two><four></four></one>";
      String target = "<one><two></two><four></three><three></four></one>";
      htmlXmlTagValidation.validate(source, target);

      assertThat(htmlXmlTagValidation.hasError(), is(true));
      assertThat(htmlXmlTagValidation.getError(), hasItem(MOCK_TAGS_OUT_OF_ORDER_MESSAGE));
      assertThat(htmlXmlTagValidation.getError().size(), is(1));

      assertThat(capturedTagsOutOfOrder.getValue(), hasItems("<three>", "</three>"));
      assertThat(capturedTagsOutOfOrder.getValue(), not(anyOf(hasItem("<one>"), hasItem("<two>"), hasItem("</two>"), hasItem("<four>"), hasItem("</four>"), hasItem("</one>"))));
      assertThat(capturedTagsOutOfOrder.getValue().size(), is(2));
   }
}


 