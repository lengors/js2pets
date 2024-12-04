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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.lengors.js2pets.annotators.AnnotatorUtils;
import io.github.lengors.js2pets.annotators.CheckerableAnnotator;
import io.github.lengors.js2pets.assertions.AssertionUtils;
import io.github.lengors.js2pets.rules.ConstructorRule;
import io.github.lengors.js2pets.rules.EnumRule;
import io.github.lengors.js2pets.rules.ObjectRule;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Stream;

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
   * Number of objects constructed of type {@link EnumRule}.
   */
  private static final int ENUM_RULE_CONSTRUCT_COUNT = 1;

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
   * Number of arguments for construction of enums of type {@link EnumRule}.
   */
  private static final int ENUM_RULE_ARGUMENT_COUNT = 2;

  /**
   * Number of arguments for construction of objects of type {@link Jackson2Annotator}.
   */
  private static final int JACKSON2_ANNOTATOR_ARGUMENT_COUNT = 1;

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
  private Annotator annotator;

  /**
   * Mocked schema store.
   */
  @Mock
  @MonotonicNonNull
  private SchemaStore schemaStore;

  @ParameterizedTest
  @MethodSource("enhancedRuleFactoriesWithAllArguments")
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithAllArguments(
      final AllArgumentsTestParameters parameters) {
    testEnhancedRuleFactoryWithAllArguments(
        parameters.ruleFactoryGenerator(),
        parameters.expectedIncludeNoArgsConstructor(),
        parameters.configurer());
  }

  @ParameterizedTest
  @MethodSource("enhancedRuleFactoriesWithGenerationConfigArgument")
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithGenerationConfigArgument(
      final GenerationConfigArgumentTestParameters parameters) {
    testEnhancedRuleFactoryWithGenerationConfigArgument(
        parameters.ruleFactoryGenerator(),
        parameters.expectedIncludeNoArgsConstructor());
  }

  @ParameterizedTest
  @MethodSource("enhancedRuleFactoriesWithNoArguments")
  void shouldInstantiateConstructorRuleForEnhancedRuleFactoryWithNoArguments(
      final NoArgumentsTestParameters parameters) {
    testEnhancedRuleFactoryWithNoArguments(
        parameters.ruleFactoryGenerator(),
        parameters.expectedIncludeNoArgsConstructor());
  }

  private void testEnhancedRuleFactoryWithAllArguments(
      final TriFunction<GenerationConfig, Annotator, SchemaStore, ? extends EnhancedRuleFactory> ruleFactoryGenerator,
      final @Nullable Boolean expectedIncludeNoArgsConstructor,
      final @Nullable Consumer<GenerationConfig> configurer) {
    AssertionUtils.assertNotNull(generationConfig);
    AssertionUtils.assertNotNull(annotator);
    AssertionUtils.assertNotNull(schemaStore);

    if (configurer != null) {
      configurer.accept(generationConfig);
    }

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

  private static void annotateWithCheckerableAnnotator(final GenerationConfig generationConfig) {
    Mockito
        .when(generationConfig.getCustomAnnotator())
        .thenAnswer(invocation -> CheckerableAnnotator.class);
  }

  private static Stream<AllArgumentsTestParameters> enhancedRuleFactoriesWithAllArguments() {
    return Stream.of(
        new AllArgumentsTestParameters(EnhancedRuleFactory::new, null),
        new AllArgumentsTestParameters(
            CheckerableRuleFactory::new,
            null,
            EnhancedRuleFactoryTest::annotateWithCheckerableAnnotator),
        new AllArgumentsTestParameters(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true),
        new AllArgumentsTestParameters(
            CheckerableRuleFactory.IncludeNoArgsConstructor::new,
            true,
            EnhancedRuleFactoryTest::annotateWithCheckerableAnnotator),
        new AllArgumentsTestParameters(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false),
        new AllArgumentsTestParameters(
            CheckerableRuleFactory.ExcludeNoArgsConstructor::new,
            false,
            EnhancedRuleFactoryTest::annotateWithCheckerableAnnotator));
  }

  private static Stream<GenerationConfigArgumentTestParameters> enhancedRuleFactoriesWithGenerationConfigArgument() {
    return Stream.of(
        new GenerationConfigArgumentTestParameters(EnhancedRuleFactory::new, null),
        new GenerationConfigArgumentTestParameters(CheckerableRuleFactory::new, null),
        new GenerationConfigArgumentTestParameters(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true),
        new GenerationConfigArgumentTestParameters(CheckerableRuleFactory.IncludeNoArgsConstructor::new, true),
        new GenerationConfigArgumentTestParameters(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false),
        new GenerationConfigArgumentTestParameters(CheckerableRuleFactory.ExcludeNoArgsConstructor::new, false));
  }

  private static Stream<NoArgumentsTestParameters> enhancedRuleFactoriesWithNoArguments() {
    return Stream.of(
        new NoArgumentsTestParameters(EnhancedRuleFactory::new, null),
        new NoArgumentsTestParameters(CheckerableRuleFactory::new, null),
        new NoArgumentsTestParameters(EnhancedRuleFactory.IncludeNoArgsConstructor::new, true),
        new NoArgumentsTestParameters(CheckerableRuleFactory.IncludeNoArgsConstructor::new, true),
        new NoArgumentsTestParameters(EnhancedRuleFactory.ExcludeNoArgsConstructor::new, false),
        new NoArgumentsTestParameters(CheckerableRuleFactory.ExcludeNoArgsConstructor::new, false));
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
      final var annotator = enhancedRuleFactory.getAnnotator();

      AssertionUtils.assertNotNull(actualGenerationConfig);
      Assertions.assertEquals(enhancedRuleFactory.getGenerationConfig(), actualGenerationConfig);
      Assertions.assertTrue(AnnotatorUtils
          .streamAnnotators(annotator)
          .anyMatch(mockedJackson2Annotator::equals));
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

    try (var mockedEnumRuleConstruction = new EnhancedMockedConstruction<>(EnumRule.class)) {
      final var enumRule = enhancedRuleFactory.getEnumRule();

      Assertions.assertInstanceOf(EnumRule.class, enumRule);

      final var constructed = mockedEnumRuleConstruction.constructed();

      Assertions.assertEquals(ENUM_RULE_CONSTRUCT_COUNT, constructed.size());

      final var mockedEnumRule = constructed.getFirst();
      final var arguments = mockedEnumRuleConstruction.getArguments(mockedEnumRule);

      AssertionUtils.assertNotNull(arguments);
      Assertions.assertEquals(ENUM_RULE_ARGUMENT_COUNT, arguments.size());

      final var actualRuleFactory = arguments.get(0);
      final var actualSuperEnumRule = arguments.get(1);

      Assertions.assertEquals(enhancedRuleFactory, actualRuleFactory);
      Assertions.assertInstanceOf(org.jsonschema2pojo.rules.EnumRule.class, actualSuperEnumRule);
    }

    try (var mockedObjectRuleConstruction = new EnhancedMockedConstruction<>(ObjectRule.class)) {
      final var objectRule = enhancedRuleFactory.getObjectRule();

      Assertions.assertInstanceOf(ObjectRule.class, objectRule);

      final var constructed = mockedObjectRuleConstruction.constructed();

      Assertions.assertEquals(OBJECT_RULE_CONSTRUCT_COUNT, constructed.size());

      final var mockedObjectRule = constructed.getFirst();
      final var arguments = mockedObjectRuleConstruction.getArguments(mockedObjectRule);

      AssertionUtils.assertNotNull(arguments);
      Assertions.assertEquals(OBJECT_RULE_ARGUMENT_COUNT, arguments.size());

      final var actualRuleFactory = arguments.get(0);
      final var actualSuperObjectRule = arguments.get(1);

      Assertions.assertEquals(enhancedRuleFactory, actualRuleFactory);
      Assertions.assertInstanceOf(org.jsonschema2pojo.rules.ObjectRule.class, actualSuperObjectRule);
    }
  }

  private record AllArgumentsTestParameters(
      TriFunction<GenerationConfig, Annotator, SchemaStore, ? extends EnhancedRuleFactory> ruleFactoryGenerator,
      @Nullable Boolean expectedIncludeNoArgsConstructor,
      @Nullable Consumer<GenerationConfig> configurer) {
    AllArgumentsTestParameters(
        final TriFunction<GenerationConfig, Annotator, SchemaStore, ? extends EnhancedRuleFactory> ruleFactoryGenerator,
        final @Nullable Boolean expectedIncludeNoArgsConstructor) {
      this(ruleFactoryGenerator, expectedIncludeNoArgsConstructor, null);
    }
  }

  private record GenerationConfigArgumentTestParameters(
      Function<GenerationConfig, ? extends EnhancedRuleFactory> ruleFactoryGenerator,
      @Nullable Boolean expectedIncludeNoArgsConstructor) {
  }

  private record NoArgumentsTestParameters(
      Supplier<? extends EnhancedRuleFactory> ruleFactoryGenerator,
      @Nullable Boolean expectedIncludeNoArgsConstructor) {
  }
}
