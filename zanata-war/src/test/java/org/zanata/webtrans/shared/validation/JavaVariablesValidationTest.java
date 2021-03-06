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
import org.zanata.webtrans.shared.validation.action.JavaVariablesValidation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 *
 * @author David Mason, damason@redhat.com
 *
 **/
@Test(groups = { "unit-tests" })
public class JavaVariablesValidationTest
{
   // TODO use TestMessages

   private static final String MOCK_VARIABLES_VALIDATOR_NAME = "test variable validator name";
   private static final String MOCK_VARIABLES_VALIDATOR_DESCRIPTION = "test variable validator description";
   private static final String MOCK_VARIABLES_ADDED_MESSAGE = "test variables added message";
   private static final String MOCK_VARIABLES_MISSING_MESSAGE = "test variables missing message";

   private JavaVariablesValidation javaVariablesValidation;

   @Mock 
   private ValidationMessages mockMessages;
   @Captor
   private ArgumentCaptor<List<String>> capturedVarsAdded;
   @Captor
   private ArgumentCaptor<List<String>> capturedVarsMissing;

   @BeforeMethod
   public void init()
   {
      MockitoAnnotations.initMocks(this);
      javaVariablesValidation = null;
      when(mockMessages.varsAdded(capturedVarsAdded.capture())).thenReturn(MOCK_VARIABLES_ADDED_MESSAGE);
      when(mockMessages.varsMissing(capturedVarsMissing.capture())).thenReturn(MOCK_VARIABLES_MISSING_MESSAGE);
      when(mockMessages.javaVariablesValidatorName()).thenReturn(MOCK_VARIABLES_VALIDATOR_NAME);
      when(mockMessages.javaVariablesValidatorDescription()).thenReturn(MOCK_VARIABLES_VALIDATOR_DESCRIPTION);
   }

   @Test
   public void idIsSet()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      assertThat(javaVariablesValidation.getId(), is(MOCK_VARIABLES_VALIDATOR_NAME));
   }

   @Test
   public void descriptionIsSet()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      assertThat(javaVariablesValidation.getDescription(), is(MOCK_VARIABLES_VALIDATOR_DESCRIPTION));
   }

   @Test
   public void noErrorForMatchingVars()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "Testing string with variable {0} and {1}";
      String target = "{1} and {0} included, order not relevant";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   @Test
   public void missingVarInTarget()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "Testing string with variable {0}";
      String target = "Testing string with no variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItem("{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(1));
   }

   @Test
   public void missingVarsThroughoutTarget()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "{0} variables in all parts {1} of the string {2}";
      String target = "Testing string with no variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItems("{0}", "{1}", "{2}"));
      assertThat(capturedVarsMissing.getValue().size(), is(3));
   }

   @Test
   public void addedVarInTarget()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "Testing string with no variables";
      String target = "Testing string with variable {0}";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_ADDED_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsAdded.getValue(), hasItem("{0}"));
      assertThat(capturedVarsAdded.getValue().size(), is(1));
   }

   @Test
   public void addedVarsThroughoutTarget()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "Testing string with no variables";
      String target = "{0} variables in all parts {1} of the string {2}";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_ADDED_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsAdded.getValue(), hasItems("{0}", "{1}", "{2}"));
      assertThat(capturedVarsAdded.getValue().size(), is(3));
   }

   @Test
   public void bothAddedAndMissingVars()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "String with {0} and {1} only, not 2";
      String target = "String with {1} and {2}, not 0";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItems(MOCK_VARIABLES_ADDED_MESSAGE, MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(2));

      assertThat(capturedVarsAdded.getValue(), hasItem("{2}"));
      assertThat(capturedVarsAdded.getValue().size(), is(1));
      assertThat(capturedVarsMissing.getValue(), hasItem("{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(1));
   }

   public void disturbanceInTheForce()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.";
      String target = "At time on date, there was a disturbance in the force on planet Earth";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItems("{1}", "{2}", "{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(3));
   }

   public void diskContainsFiles()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "The disk \"{1}\" contains {0} file(s).";
      String target = "The disk contains some files";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItems("{1}", "{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(2));
   }

   public void doesNotDetectEscapedVariables()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "This string does not contain \\{0\\} style variables";
      String target = "This string does not contain java style variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   public void doesNotDetectQuotedVariables()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "This string does not contain '{0}' style variables";
      String target = "This string does not contain java style variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   public void doesNotDetectVariablesInQuotedText()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "This 'string does not contain {0} style' variables";
      String target = "This string does not contain java style variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   public void ignoresEscapedQuotes()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "This string does not contain \\'{0}\\' style variables";
      String target = "This string does not contain java style variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItem("{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(1));
   }

   public void advancedQuoting()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "'''{'0}'''''{0}'''";
      String target = "From examples on MessageFormat page, should not contain any variables";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   public void translatedChoicesStillMatch()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "There {0,choice,0#are no things|1#is one thing|1<are many things}.";
      String target = "Es gibt {0,choice,0#keine Dinge|1#eine Sache|1<viele Dinge}.";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(false));
      assertThat(javaVariablesValidation.getError().size(), is(0));
   }

   public void choiceFormatAndRecursion()
   {
      javaVariablesValidation = new JavaVariablesValidation(mockMessages);
      String source = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
      String target = "There are 0 files";
      javaVariablesValidation.validate(source, target);

      assertThat(javaVariablesValidation.hasError(), is(true));
      assertThat(javaVariablesValidation.getError(), hasItem(MOCK_VARIABLES_MISSING_MESSAGE));
      assertThat(javaVariablesValidation.getError().size(), is(1));

      assertThat(capturedVarsMissing.getValue(), hasItem("{0}"));
      assertThat(capturedVarsMissing.getValue().size(), is(1));
   }

   //TODO tests for format type

   //TODO test 3 or 4 levels of recursion

}


 