package io.github.lengors.js2pets.rules;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.InclusionLevel;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.rules.Rule;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import io.github.lengors.js2pets.rules.exceptions.ConfigurationPropertyMissingException;
import lombok.Getter;

import java.util.Map;
import java.util.Collections;

final class ConstructorRuleTestRunner {
  /**
   * The node name used for applying the rule, initialized as an empty string.
   */
  public static final String NODE_NAME = StringUtils.EMPTY;

  /**
   * The rule under test for generating constructors in a defined class.
   */
  private final Rule<JDefinedClass, JDefinedClass> constructorRule;

  /**
   * The configuration settings used for generating code.
   */
  private final GenerationConfig generationConfig;

  /**
   * The class being defined and modified in the tests.
   */
  private final JDefinedClass definedClass;

  /**
   * The test suite providing the mock components for testing.
   */
  private final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite;

  /**
   * The no-arguments constructor generated in the test.
   */
  private final JMethod noArgsConstructor;

  /**
   * The arguments constructor generated in the test.
   */
  @Getter
  private final JMethod argsConstructor;

  private ConstructorRuleTestRunner(
      final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite,
      final @Nullable Boolean includeNoArgsConstructor,
      final boolean usePluginImplementation) throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();

    this.js2petsConstructorRuleTestSuite = js2petsConstructorRuleTestSuite;
    definedClass = codeModel._class("io.github.lengors.js2pets.rules.Test");
    noArgsConstructor = definedClass.constructor(JMod.PUBLIC);
    argsConstructor = definedClass.constructor(JMod.PUBLIC);
    argsConstructor.param(Integer.class, "test");

    final var node = js2petsConstructorRuleTestSuite.getNode();
    final var currentSchema = js2petsConstructorRuleTestSuite.getCurrentSchema();
    final var superConstructorRule = js2petsConstructorRuleTestSuite.getSuperConstructorRule();
    Mockito
        .when(superConstructorRule.apply(NODE_NAME, node, node, definedClass, currentSchema))
        .thenReturn(definedClass);

    generationConfig = usePluginImplementation
        ? (GenerationConfig) Mockito.mock(AbstractMojo.class, Mockito
            .withSettings()
            .extraInterfaces(GenerationConfig.class))
        : Mockito.mock(GenerationConfig.class);

    if (includeNoArgsConstructor == null) {
      Mockito
          .when(js2petsConstructorRuleTestSuite
              .getRuleFactory()
              .getGenerationConfig())
          .thenReturn(generationConfig);
    }

    constructorRule = new ConstructorRule(
        js2petsConstructorRuleTestSuite.getRuleFactory(),
        includeNoArgsConstructor,
        superConstructorRule);
  }

  private JDefinedClass apply() {
    return constructorRule.apply(
        NODE_NAME,
        js2petsConstructorRuleTestSuite.getNode(),
        js2petsConstructorRuleTestSuite.getNode(),
        definedClass,
        js2petsConstructorRuleTestSuite.getCurrentSchema());
  }

  void testForSuccess(final Annotator annotator, final boolean expectedIncludeNoArgsConstructor) {
    Mockito
        .when(js2petsConstructorRuleTestSuite
            .getRuleFactory()
            .getAnnotator())
        .thenReturn(annotator);

    final var result = apply();
    final var constructors = IteratorUtils.toList(result.constructors());
    Assertions.assertEquals(expectedIncludeNoArgsConstructor, constructors.contains(noArgsConstructor));
    Assertions.assertTrue(constructors.contains(argsConstructor));
  }

  void testForSuccess(final boolean expectedIncludeNoArgsConstructor) {
    Mockito
        .when(generationConfig.getInclusionLevel())
        .thenReturn(InclusionLevel.NON_NULL);
    testForSuccess(new Jackson2Annotator(generationConfig), expectedIncludeNoArgsConstructor);
  }

  void testForFailure() {
    Assertions.assertThrows(ConfigurationPropertyMissingException.class, this::apply);
  }

  static ConstructorRuleTestRunner prepareWithoutPluginImplementation(
      final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite,
      final @Nullable Boolean includeNoArgsConstructor) throws JClassAlreadyExistsException {
    return new ConstructorRuleTestRunner(js2petsConstructorRuleTestSuite, includeNoArgsConstructor, false);
  }

  static ConstructorRuleTestRunner prepare(
      final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite,
      final int pluginExecutionCount) throws JClassAlreadyExistsException {
    return prepare(js2petsConstructorRuleTestSuite, pluginExecutionCount, Mockito.mock(Plugin.class));
  }

  static ConstructorRuleTestRunner prepare(
      final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite,
      final boolean includeNoArgsConstructor) throws JClassAlreadyExistsException {
    final var plugin = Mockito.mock(Plugin.class);
    final var runner = prepare(js2petsConstructorRuleTestSuite, 1, plugin);

    final var rootDom = Mockito.mock(Xpp3Dom.class);
    final var includeNoArgsConstructorDom = Mockito.mock(Xpp3Dom.class);

    Mockito
        .when(plugin.getConfiguration())
        .thenReturn(rootDom);
    Mockito
        .when(rootDom.getChild("includeNoArgsConstructor"))
        .thenReturn(includeNoArgsConstructorDom);
    Mockito
        .when(includeNoArgsConstructorDom.getValue())
        .thenReturn(String.valueOf(includeNoArgsConstructor));
    return runner;
  }

  private static ConstructorRuleTestRunner prepare(
      final ConstructorRuleTestSuite js2petsConstructorRuleTestSuite,
      final int pluginExecutionCount,
      final Plugin plugin) throws JClassAlreadyExistsException {
    final var runner = new ConstructorRuleTestRunner(js2petsConstructorRuleTestSuite, null, true);
    final var pluginDescriptor = Mockito.mock(PluginDescriptor.class);
    final var pluginExecution = Mockito.mock(PluginExecution.class);
    final var generatingConfig = (AbstractMojo) js2petsConstructorRuleTestSuite
        .getRuleFactory()
        .getGenerationConfig();
    Mockito
        .when(generatingConfig.getPluginContext())
        .thenReturn(Map.of("pluginDescriptor", pluginDescriptor));
    Mockito
        .when(pluginDescriptor.getPlugin())
        .thenReturn(plugin);
    Mockito
        .when(plugin.getExecutions())
        .thenReturn(Collections.nCopies(pluginExecutionCount, pluginExecution));
    return runner;
  }
}
