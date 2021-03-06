package org.zanata.webtrans.shared.validation.action;

import java.util.Collection;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.webtrans.client.resources.ValidationMessages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Test(groups = { "unit-tests" })
public class PrintfXSIExtensionValidationTest
{
   private static final String MOCK_VARIABLES_VALIDATOR_NAME = "test variable validator name";
   private static final String MOCK_VARIABLES_VALIDATOR_DESCRIPTION = "test variable validator description";
   private static final String MOCK_VARIABLES_ADDED_MESSAGE = "test variables added message";
   private static final String MOCK_VARIABLES_MISSING_MESSAGE = "test variables missing message";
   private static final String MIX_VAR_FORMAT_MESSAGE = "mix var format";
   private static final String VAR_IS_OUT_OF_RANGE = "var is out of range";
   private static final String VARIABLES_HAS_SAME_POSITION = "variables has same position";

   private PrintfXSIExtensionValidation printfVariablesValidation;

   @Mock
   private ValidationMessages mockMessages;
   @Captor
   private ArgumentCaptor<List<String>> capturedVarsAdded;
   @Captor
   private ArgumentCaptor<List<String>> capturedVarsMissing;
   @Captor
   private ArgumentCaptor<String> captureOutOfRangeVar;
   @Captor
   private ArgumentCaptor<Collection<String>> captureVars;

   @BeforeMethod
   public void init()
   {
      MockitoAnnotations.initMocks(this);

      when(mockMessages.varsAdded(capturedVarsAdded.capture())).thenReturn(MOCK_VARIABLES_ADDED_MESSAGE);
      when(mockMessages.varsMissing(capturedVarsMissing.capture())).thenReturn(MOCK_VARIABLES_MISSING_MESSAGE);
      when(mockMessages.mixVarFormats()).thenReturn(MIX_VAR_FORMAT_MESSAGE);
      when(mockMessages.varPositionOutOfRange(captureOutOfRangeVar.capture())).thenReturn(VAR_IS_OUT_OF_RANGE);
      when(mockMessages.varPositionDuplicated(captureVars.capture())).thenReturn(VARIABLES_HAS_SAME_POSITION);
      when(mockMessages.positionalPrintfVariablesValidatorName()).thenReturn(MOCK_VARIABLES_VALIDATOR_NAME);
      when(mockMessages.positionalPrintfVariablesValidatorDescription()).thenReturn(MOCK_VARIABLES_VALIDATOR_DESCRIPTION);

      printfVariablesValidation = new PrintfXSIExtensionValidation(mockMessages);
   }

   @Test
   public void idAndDescriptionAreSet()
   {
      assertThat(printfVariablesValidation.getId(), is(MOCK_VARIABLES_VALIDATOR_NAME));
      assertThat(printfVariablesValidation.getDescription(), is(MOCK_VARIABLES_VALIDATOR_DESCRIPTION));
   }

   @Test
   public void validPositionalVariables() {
      String source = "%s: Read error at byte %s, while reading %lu byte";
      String target = "%1$s：Read error while reading %3$lu bytes，at %2$s";
      printfVariablesValidation.validate(source, target);

      assertThat(printfVariablesValidation.hasError(), is(false));
      assertThat(printfVariablesValidation.getError().size(), is(0));
   }

   @Test
   public void mixPositionalVariablesWithNotPositional() {
      String source = "%s: Read error at byte %s, while reading %lu byte";
      String target = "%1$s：Read error while reading %lu bytes，at %2$s";
      printfVariablesValidation.validate(source, target);

      assertThat(printfVariablesValidation.hasError(), is(true));
      assertThat(printfVariablesValidation.getError().size(), is(3));

      assertThat(capturedVarsAdded.getValue(), contains("%lu"));
      assertThat(capturedVarsMissing.getValue(), contains("%3$lu"));
   }

   @Test
   public void positionalVariableOutOfRange() {
      String source = "%s: Read error at byte %s, while reading %lu byte";
      String target = "%3$s：Read error while reading %99$lu bytes，at %2$s";
      printfVariablesValidation.validate(source, target);

      assertThat(printfVariablesValidation.hasError(), is(true));
      assertThat(printfVariablesValidation.getError().size(), is(3));

      assertThat(capturedVarsAdded.getValue(), contains("%3$s", "%99$lu"));
      assertThat(capturedVarsMissing.getValue(), contains("%1$s", "%3$lu"));
      assertThat(captureOutOfRangeVar.getValue(), equalTo("%99$lu"));
   }

   @Test
   public void positionalVariablesHaveSamePosition() {
      String source = "%s: Read error at byte %s, while reading %lu byte";
      String target = "%3$s：Read error while reading %3$lu bytes, at %2$s";
      printfVariablesValidation.validate(source, target);

      assertThat(printfVariablesValidation.hasError(), is(true));
      assertThat(printfVariablesValidation.getError().size(), is(3));

      assertThat(capturedVarsAdded.getValue(), contains("%3$s"));
      assertThat(capturedVarsMissing.getValue(), contains("%1$s"));
      assertThat(captureVars.getValue(), contains("%3$s", "%3$lu"));
   }

   @Test
   public void invalidPositionalVariablesBringItAll() {
      String source = "%s of %d and %lu";
      String target = "%2$d %2$s %9$lu %z";
      printfVariablesValidation.validate(source, target);

      assertThat(printfVariablesValidation.getError(), containsInAnyOrder(MOCK_VARIABLES_ADDED_MESSAGE, MOCK_VARIABLES_MISSING_MESSAGE, VAR_IS_OUT_OF_RANGE, VARIABLES_HAS_SAME_POSITION, MIX_VAR_FORMAT_MESSAGE));
      assertThat(capturedVarsAdded.getValue(), contains("%2$s", "%9$lu", "%z"));
      assertThat(captureOutOfRangeVar.getValue(), equalTo("%9$lu"));
      assertThat(capturedVarsMissing.getValue(), contains("%1$s", "%3$lu"));
   }
}
