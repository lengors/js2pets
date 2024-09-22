package io.github.lengors.js2pets.factories;

import org.apache.commons.lang3.function.TriFunction;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.lengors.js2pets.assertions.AssertionUtils;
import io.github.lengors.js2pets.rules.ConstructorRule;
import io.github.lengors.js2pets.rules.ObjectRule;
import lombok.Getter;

import java.util.function.Supplier;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class EnhancedRuleFactoryTest {
  /**
   * Number of objects constructed of type {@link ConstructorRule}.
   */
  private static final int CONSTRUCTOR_RULE_CONSTRUCT_COUNT = 1;

  /**
   * Number of objects constructed of type {@link ObjectRule}.
   */
  private static final int OBJECT_RULE_CONSTRUCT_COUNT = 1;

  /**
   * Number of objects constructed of type {@link Jackson2Annotator}.
   */
  private static final int JACKSON2_ANNOTATOR_CONSTRUCT_COUNT = 1;

  /**
   * Number of arguments for construction of objects of type {@link ConstructorRule}.
   */
  private static final int CONSTRUCTOR_RULE_ARGUMENT_COUNT = 3;

  /**
   * Number of arguments for construction of objects of type {@link ObjectRule}.
   */
  private static final int OBJECT_RULE_ARGUMENT_COUNT = 2;

  /**
   * Number of arguments for construction of objects of type {@link Jackson2Annotator}.
   */
  private static final int JACKSON2_ANNOTATOR_ARGUMENT_COUNT = 1;

  /**
   * Mocked generation configuration.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private GenerationConfig generationConfig;

  /**
   * Mocked annotator.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private Annotator annotator;

  /**
   * Mocked schema store.
   */
  @Mock
  @Getter
  @MonotonicNonNull
  private SchemaStore schemaStore;

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithAllArguments() {
    testEnhancedRuleFactoryWithAllArguments(EnhancedRuleFactory::new, null);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithGenerationConfigArgument() {
    testEnhancedRuleFactoryWithGenerationConfigArgument(EnhancedRuleFactory::new, null);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithNoArguments() {
    testEnhancedRuleFactoryWithNoArguments(EnhancedRuleFactory::new, null);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithAllArgumentsAndIncludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithAllArguments(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithGenerationConfigAndIncludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithGenerationConfigArgument(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithNoArgumentsAndIncludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithNoArguments(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithAllArgumentsAndExcludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithAllArguments(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithGenerationConfigAndExcludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithGenerationConfigArgument(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false);
  }

  @Test
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithNoArgumentsAndExcludesNoArgsConstructor() {
    testEnhancedRuleFactoryWithNoArguments(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false);
  }

  private void testEnhancedRuleFactoryWithAllArguments(
      final TriFunction<GenerationConfig, Annotator, SchemaStore, ? extends EnhancedRuleFactory> ruleFactoryGenerator,
      final @Nullable Boolean expectedIncludeNoArgsConstructor) {
    AssertionUtils.assertNotNull(generationConfig);
    AssertionUtils.assertNotNull(annotator);
    AssertionUtils.assertNotNull(schemaStore);

    final var enhancedRuleFactory = ruleFactoryGenerator.apply(generationConfig, annotator, schemaStore);
    testEnhancedRuleFactory(enhancedRuleFactory, expectedIncludeNoArgsConstructor);

    Assertions.assertEquals(generationConfig, enhancedRuleFactory.getGenerationConfig());
    Assertions.assertEquals(annotator, enhancedRuleFactory.getAnnotator());
    Assertions.assertEquals(schemaStore, enhancedRuleFactory.getSchemaStore());
  }

  private void testEnhancedRuleFactoryWithGenerationConfigArgument(
      final Function<GenerationConfig, ? extends EnhancedRuleFactory> enhancedRuleFactoryGenerator,
      final @Nullable Boolean expectedIncludeNoArgsConstructor) {
    final var actualGenerationConfig = testEnhancedRuleFactoryWithDefaultJackson2Annotator(() -> {
      AssertionUtils.assertNotNull(generationConfig);
      return enhancedRuleFactoryGenerator.apply(generationConfig);
    }, expectedIncludeNoArgsConstructor);
    Assertions.assertEquals(generationConfig, actualGenerationConfig);
  }

  private void testEnhancedRuleFactoryWithNoArguments(
      final Supplier<? extends EnhancedRuleFactory> enhancedRuleFactoryGenerator,
      final @Nullable Boolean expectedIncludeNoArgsConstructor) {
    final var actualGenerationConfig = testEnhancedRuleFactoryWithDefaultJackson2Annotator(
        enhancedRuleFactoryGenerator,
        expectedIncludeNoArgsConstructor);
    Assertions.assertInstanceOf(DefaultGenerationConfig.class, actualGenerationConfig);
  }

  private static GenerationConfig testEnhancedRuleFactoryWithDefaultJackson2Annotator(
      final Supplier<? extends EnhancedRuleFactory> enhancedRuleFactorySupplier,
      final @Nullable Boolean expectedIncludeNoArgsConstructor) {
    try (var jackson2AnnotatorArgumentsCollector = new EnhancedMockedConstruction<>(Jackson2Annotator.class)) {
      final var enhancedRuleFactory = enhancedRuleFactorySupplier.get();

      testEnhancedRuleFactory(enhancedRuleFactory, expectedIncludeNoArgsConstructor);

      final var jacksonConstructed = jackson2AnnotatorArgumentsCollector.constructed();

      Assertions.assertEquals(JACKSON2_ANNOTATOR_CONSTRUCT_COUNT, jacksonConstructed.size());

      final var mockedJackson2Annotator = jacksonConstructed.getFirst();
      final var arguments = jackson2AnnotatorArgumentsCollector.getArguments(mockedJackson2Annotator);

      AssertionUtils.assertNotNull(arguments);
      Assertions.assertEquals(JACKSON2_ANNOTATOR_ARGUMENT_COUNT, arguments.size());

      final var actualGenerationConfig = arguments.getFirst();

      AssertionUtils.assertNotNull(actualGenerationConfig);
      Assertions.assertEquals(enhancedRuleFactory.getGenerationConfig(), actualGenerationConfig);
      Assertions.assertEquals(mockedJackson2Annotator, enhancedRuleFactory.getAnnotator());
      Assertions.assertInstanceOf(SchemaStore.class, enhancedRuleFactory.getSchemaStore());

      return (GenerationConfig) actualGenerationConfig;
    }
  }

  private static void testEnhancedRuleFactory(
      final EnhancedRuleFactory enhancedRuleFactory,
      final @Nullable Boolean expectedIncludeNoArgsConstructor) {
    try (var mockedConstruction = new EnhancedMockedConstruction<>(ConstructorRule.class)) {
      final var constructorRule = enhancedRuleFactory.getConstructorRule();

      Assertions.assertInstanceOf(ConstructorRule.class, constructorRule);

      final var constructed = mockedConstruction.constructed();

      Assertions.assertEquals(CONSTRUCTOR_RULE_CONSTRUCT_COUNT, constructed.size());

      final var mockedConstructorRule = constructed.getFirst();
      final var arguments = mockedConstruction.getArguments(mockedConstructorRule);

      AssertionUtils.assertNotNull(arguments);
      Assertions.assertEquals(CONSTRUCTOR_RULE_ARGUMENT_COUNT, arguments.size());

      final var actualRuleFactory = arguments.get(0);
      final var actualIncludeNoArgsConstructor = arguments.get(1);
      final var actualSuperConstructorRule = arguments.get(2);

      Assertions.assertEquals(enhancedRuleFactory, actualRuleFactory);
      Assertions.assertEquals(expectedIncludeNoArgsConstructor, actualIncludeNoArgsConstructor);
      Assertions.assertInstanceOf(org.jsonschema2pojo.rules.ConstructorRule.class, actualSuperConstructorRule);
    }

    try (var mockedConstruction = new EnhancedMockedConstruction<>(ObjectRule.class)) {
      final var objectRule = enhancedRuleFactory.getObjectRule();

      Assertions.assertInstanceOf(ObjectRule.class, objectRule);

      final var constructed = mockedConstruction.constructed();

      Assertions.assertEquals(OBJECT_RULE_CONSTRUCT_COUNT, constructed.size());

      final var mockedObjectRule = constructed.getFirst();
      final var arguments = mockedConstruction.getArguments(mockedObjectRule);

      AssertionUtils.assertNotNull(arguments);
      Assertions.assertEquals(OBJECT_RULE_ARGUMENT_COUNT, arguments.size());

      final var actualRuleFactory = arguments.get(0);
      final var actualSuperObjectRule = arguments.get(1);

      Assertions.assertEquals(enhancedRuleFactory, actualRuleFactory);
      Assertions.assertInstanceOf(org.jsonschema2pojo.rules.ObjectRule.class, actualSuperObjectRule);
    }
  }
}
