package io.github.lengors.js2pets.rules;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;

import io.github.lengors.js2pets.annotators.EnhancedAnnotator;
import lombok.Getter;

@ExtendWith(MockitoExtension.class)
class ConstructorRuleTest implements ConstructorRuleTestSuite {

  /**
   * Mock rule used to simulate the super constructor rule behavior.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private Rule<JDefinedClass, JDefinedClass> superConstructorRule;

  /**
   * Mock JSON node used for testing.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private JsonNode node;

  /**
   * Mock schema used for testing.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private Schema currentSchema;

  /**
   * Mock rule factory used to provide configuration and utilities for testing.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private RuleFactory ruleFactory;

  @Test
  void shouldCorrectlyIncludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithoutPluginImplementation(true);
  }

  @Test
  void shouldCorrectlyExcludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithoutPluginImplementation(false);
  }

  @Test
  void shouldFailToInferNoArgsConstructorInclusionDueToMojoNotUsed() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepareWithoutPluginImplementation(this, null)
        .testForFailure();
  }

  @Test
  void shouldCorrectlyInferIncludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithPluginImplementation(true);
  }

  @Test
  void shouldCorrectlyInferExcludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithPluginImplementation(false);
  }

  @Test
  void shouldCorrectlyInferDefaultInclusionOfNoArgsConstructor() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepare(this, 0)
        .testForSuccess(true);
  }

  @Test
  void shouldFailToInferNoArgsConstructorInclusionDueToMultipleExecutions() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepare(this, 2)
        .testForFailure();
  }

  @Test
  void shouldCorrectlyNotifyAnnotator() throws JClassAlreadyExistsException {
    final var annotator = Mockito.mock(EnhancedAnnotator.class);

    final var includeNoArgsConstructor = false;
    final var runner = ConstructorRuleTestRunner.prepareWithoutPluginImplementation(this, includeNoArgsConstructor);
    runner.testForSuccess(annotator, includeNoArgsConstructor);

    Mockito
        .verify(annotator, Mockito.only())
        .constructor(runner.getArgsConstructor());
  }

  private void testForSuccessWithoutPluginImplementation(final boolean includeNoArgsConstructor)
      throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepareWithoutPluginImplementation(this, includeNoArgsConstructor)
        .testForSuccess(includeNoArgsConstructor);
  }

  private void testForSuccessWithPluginImplementation(final boolean includeNoArgsConstructor)
      throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepare(this, includeNoArgsConstructor)
        .testForSuccess(includeNoArgsConstructor);
  }
}
