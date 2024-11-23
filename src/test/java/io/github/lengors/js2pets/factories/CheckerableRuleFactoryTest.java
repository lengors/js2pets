package io.github.lengors.js2pets.factories;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.CompositeAnnotator;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.InclusionLevel;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.NotRequiredRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.lengors.js2pets.annotators.CheckerableAnnotator;
import io.github.lengors.js2pets.assertions.AssertionUtils;
import io.github.lengors.js2pets.rules.CheckerableNotRequiredRule;

@ExtendWith({ MockitoExtension.class })
class CheckerableRuleFactoryTest {
  /**
   * Mocked generation configuration.
   */
  @Mock
  @MonotonicNonNull
  private GenerationConfig generationConfig;

  /**
   * Mocked annotator.
   */
  @Mock
  @MonotonicNonNull
  private CheckerableAnnotator annotator;

  /**
   * Mocked schema store.
   */
  @Mock
  @MonotonicNonNull
  private SchemaStore schemaStore;

  @Test
  void shouldInstantiateComposeAnnotatorWhenCustomAnnotatorIsMissing() throws IllegalAccessException {
    testWithMissingOrNoopAnnotator(null);
  }

  @Test
  void shouldInstantiateComposeAnnotatorWhenCustomAnnotatorIsNoopAnnotator() throws IllegalAccessException {
    testWithMissingOrNoopAnnotator(NoopAnnotator.class);
  }

  @Test
  void shouldReturnSameAnnotatorWhenCustomAnnotatorIsCheckerableAnnotator() {
    final var ruleAnnotator = instantiateAnnotator(CheckerableAnnotator.class);
    Assertions.assertEquals(annotator, ruleAnnotator);
  }

  @Test
  void shouldThrowWhenCustomAnnotatorIsNeitherNoopOrCheckerableAnnotator() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> instantiateAnnotator(AbstractAnnotator.class));
  }

  @Test
  void shouldSetAnnotatorCorrectly() {
    AssertionUtils.assertNotNull(generationConfig);
    AssertionUtils.assertNotNull(annotator);

    Mockito
        .when(generationConfig.getInclusionLevel())
        .thenReturn(InclusionLevel.NON_NULL);
    Mockito
        .when(generationConfig.getCustomAnnotator())
        .thenAnswer(invocation -> CheckerableAnnotator.class);

    final var ruleFactory = new CheckerableRuleFactory(generationConfig);

    Assertions.assertInstanceOf(Jackson2Annotator.class, ruleFactory.getAnnotator());

    ruleFactory.setAnnotator(annotator);

    Assertions.assertEquals(annotator, ruleFactory.getAnnotator());
  }

  @Test
  void shouldInstantiateNotRequiredRuleAsCheckerableNotRequiredRule() throws IllegalAccessException {
    final var ruleFactory = new CheckerableRuleFactory();
    final var rule = ruleFactory.getNotRequiredRule();

    Assertions.assertInstanceOf(CheckerableNotRequiredRule.class, rule);

    final var superRuleField = FieldUtils.getField(rule.getClass(), "superNotRequiredRule", true);
    final var superRule = superRuleField.get(rule);

    Assertions.assertInstanceOf(NotRequiredRule.class, superRule);
  }

  private Annotator instantiateAnnotator(final @Nullable Class<? extends Annotator> annotatorClass) {
    AssertionUtils.assertNotNull(generationConfig);
    AssertionUtils.assertNotNull(annotator);
    AssertionUtils.assertNotNull(schemaStore);

    Mockito
        .when(generationConfig.getCustomAnnotator())
        .thenAnswer(invocation -> annotatorClass);

    final var ruleFactory = new CheckerableRuleFactory(generationConfig, annotator, schemaStore);
    return ruleFactory.getAnnotator();
  }

  private void testWithMissingOrNoopAnnotator(final @Nullable Class<? extends Annotator> annotatorClass)
      throws IllegalAccessException {
    final var ruleAnnotator = instantiateAnnotator(annotatorClass);
    final var expectedAnnotatorCount = 2;

    Assertions.assertInstanceOf(CompositeAnnotator.class, ruleAnnotator);

    final var annotatorsField = FieldUtils.getField(ruleAnnotator.getClass(), "annotators", true);
    final var annotators = (Annotator[]) annotatorsField.get(ruleAnnotator);

    AssertionUtils.assertNotNull(annotators);

    Assertions.assertEquals(expectedAnnotatorCount, annotators.length);
    Assertions.assertEquals(annotator, annotators[0]);
    Assertions.assertInstanceOf(CheckerableAnnotator.class, annotators[1]);
  }
}
