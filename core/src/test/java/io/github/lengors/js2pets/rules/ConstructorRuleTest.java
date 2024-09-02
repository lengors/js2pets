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

/**
 * Unit tests for the {@link ConstructorRule} class.
 *
 * This test class verifies the correct behavior of the {@link ConstructorRule}, particularly its ability to include or
 * exclude no-args constructors based on configuration and to notify annotators if required.
 *
 * @author lengors
 */
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

  /**
   * Tests that the rule correctly includes a no-args constructor when configured to do so.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldCorrectlyIncludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithoutPluginImplementation(true);
  }

  /**
   * Tests that the rule correctly excludes a no-args constructor when configured to do so.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldCorrectlyExcludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithoutPluginImplementation(false);
  }

  /**
   * Tests that the rule fails to infer the inclusion of a no-args constructor when the Mojo configuration is not used.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldFailToInferNoArgsConstructorInclusionDueToMojoNotUsed() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepareWithoutPluginImplementation(this, null)
        .testForFailure();
  }

  /**
   * Tests that the rule correctly infers the inclusion of a no-args constructor based on plugin configuration.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldCorrectlyInferIncludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithPluginImplementation(true);
  }

  /**
   * Tests that the rule correctly infers the exclusion of a no-args constructor based on plugin configuration.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldCorrectlyInferExcludeNoArgsConstructor() throws JClassAlreadyExistsException {
    testForSuccessWithPluginImplementation(false);
  }

  /**
   * Tests that the rule correctly infers the default inclusion of a no-args constructor.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldCorrectlyInferDefaultInclusionOfNoArgsConstructor() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepare(this, 0)
        .testForSuccess(true);
  }

  /**
   * Tests that the rule fails to infer the inclusion of a no-args constructor due to multiple executions.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
  @Test
  void shouldFailToInferNoArgsConstructorInclusionDueToMultipleExecutions() throws JClassAlreadyExistsException {
    ConstructorRuleTestRunner
        .prepare(this, 2)
        .testForFailure();
  }

  /**
   * Tests that the rule correctly notifies the annotator about the constructor if configured.
   *
   * @throws JClassAlreadyExistsException if a class with the same name already exists.
   */
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
