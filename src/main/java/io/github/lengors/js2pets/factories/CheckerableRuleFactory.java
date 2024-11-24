package io.github.lengors.js2pets.factories;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.CompositeAnnotator;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.Rule;

import com.sun.codemodel.JDocCommentable;

import io.github.lengors.js2pets.annotators.CheckerableAnnotator;
import io.github.lengors.js2pets.rules.CheckerableNotRequiredRule;

/**
 * {@link CheckerableRuleFactory} is a custom implementation of the {@link org.jsonschema2pojo.rules.RuleFactory} that
 * extends the behavior of {@link EnhancedRuleFactory} by providing with a custom {@code NotRequiredRule} and making
 * sure the {@link CheckerableAnnotator} is used. This allows jsonschema2pojo to properly support the Checkerframework.
 *
 * @author lengors
 * @deprecated Use {@link io.github.lengors.js2pets.annotators.NullabilityAnnotator} instead with an instance of
 *             {@link EnhancedRuleFactory}.
 */
@Deprecated(since = "1.2.0", forRemoval = true)
public class CheckerableRuleFactory extends EnhancedRuleFactory {
  /**
   * Constructs an {@link CheckerableRuleFactory} with the specified configuration, annotator, schema store, and no-args
   * constructor inclusion setting.
   *
   * @param generationConfig         The generation configuration settings.
   * @param annotator                The annotator used for code generation.
   * @param schemaStore              The schema store used to resolve schema references.
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  protected CheckerableRuleFactory(
      final GenerationConfig generationConfig,
      final Annotator annotator,
      final SchemaStore schemaStore,
      final @Nullable Boolean includeNoArgsConstructor) {
    super(generationConfig, composeAnnotator(annotator, generationConfig), schemaStore, includeNoArgsConstructor);
  }

  /**
   * Constructs an {@link CheckerableRuleFactory} with the specified configuration and no-args constructor inclusion
   * setting. Uses the {@link org.jsonschema2pojo.Jackson2Annotator} by default.
   *
   * @param generationConfig         The generation configuration settings.
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  protected CheckerableRuleFactory(
      final GenerationConfig generationConfig,
      final @Nullable Boolean includeNoArgsConstructor) {
    this(generationConfig,
        DEFAULT_ANNOTATOR_FUNCTION.apply(generationConfig),
        DEFAULT_SCHEMA_STORE_SUPPLIER.get(),
        includeNoArgsConstructor);
  }

  /**
   * Constructs an {@link CheckerableRuleFactory} with a default configuration and the specified no-args constructor
   * inclusion setting.
   *
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  protected CheckerableRuleFactory(final @Nullable Boolean includeNoArgsConstructor) {
    this(DEFAULT_GENERATION_CONFIG_SUPPLIER.get(), includeNoArgsConstructor);
  }

  /**
   * Constructs an {@link CheckerableRuleFactory} with the specified configuration, annotator, and schema store, using
   * the default setting for including no-args constructors.
   *
   * @param generationConfig The generation configuration settings.
   * @param annotator        The annotator used for code generation.
   * @param schemaStore      The schema store used to resolve schema references.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  public CheckerableRuleFactory(
      final GenerationConfig generationConfig,
      final Annotator annotator,
      final SchemaStore schemaStore) {
    this(generationConfig, annotator, schemaStore, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Constructs an {@link CheckerableRuleFactory} with the specified configuration, using the default setting for
   * including no-args constructors and the default annotator and schema store.
   *
   * @param generationConfig The generation configuration settings.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  public CheckerableRuleFactory(final GenerationConfig generationConfig) {
    this(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Constructs an {@link CheckerableRuleFactory} with default settings. Uses default values for the generation
   * configuration, annotator, schema store, and no-args constructor inclusion.
   *
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  public CheckerableRuleFactory() {
    this(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Returns a custom {@code NotRequiredRule} that annotates getters and fields with {@link Nullable} annotation.
   *
   * @return A {@link CheckerableNotRequiredRule} configured according to the factory settings.
   */
  @Override
  public Rule<JDocCommentable, JDocCommentable> getNotRequiredRule() {
    return new CheckerableNotRequiredRule(super.getNotRequiredRule());
  }

  /**
   * Sets annotator to be used by factory. Automatically injects {@link CheckerableAnnotator} if the custom annotator is
   * missing or is the {@link NoopAnnotator}. If a custom annotator is present it must extend
   * {@link CheckerableAnnotator}.
   *
   * @param annotator The annotator to set to.
   * @throws IllegalArgumentException If the custom annotator is set, different from {@link NoopAnnotator} and does not
   *                                  extend the {@link CheckerableAnnotator}.
   */
  @Override
  public void setAnnotator(final Annotator annotator) {
    super.setAnnotator(composeAnnotator(annotator, getGenerationConfig()));
  }

  private static Annotator composeAnnotator(final Annotator annotator, final GenerationConfig generationConfig) {
    final var customAnnotator = generationConfig.getCustomAnnotator();
    if (customAnnotator == null || NoopAnnotator.class.equals(customAnnotator)) {
      return new CompositeAnnotator(annotator, new CheckerableAnnotator());
    }
    if (!CheckerableAnnotator.class.isAssignableFrom(customAnnotator)) {
      throw new IllegalArgumentException("Custom annotator must be a CheckerableAnnotator or subclass of");
    }
    return annotator;
  }

  /**
   * {@link CheckerableRuleFactory.ExcludeNoArgsConstructor} is a specialized {@link CheckerableRuleFactory} that always
   * excludes no-argument constructors. This is achieved by setting the includeNoArgsConstructor flag to false.
   * <br />
   * <br />
   * This nested class provides a convenient way to instantiate a factory with predefined behavior.
   * <br />
   * <br />
   * Example:
   *
   * <pre>
   * CheckerableRuleFactory factory = new CheckerableRuleFactory.ExcludeNoArgsConstructor();
   * </pre>
   *
   * @author lengors
   * @deprecated Use {@link io.github.lengors.js2pets.annotators.NullabilityAnnotator} instead with an instance of
   *             {@link EnhancedRuleFactory.ExcludeNoArgsConstructor}.
   */
  @Deprecated(since = "1.2.0", forRemoval = true)
  public static class ExcludeNoArgsConstructor extends CheckerableRuleFactory {
    /**
     * Default value indicating that no-argument constructors should be excluded.
     */
    private static final @Nullable Boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = false;

    /**
     * Constructs an {@link CheckerableRuleFactory.ExcludeNoArgsConstructor} factory with the specified configuration,
     * annotator, and schema store.
     *
     * @param generationConfig The generation configuration settings.
     * @param annotator        The annotator used for code generation.
     * @param schemaStore      The schema store used to resolve schema references.
     */
    public ExcludeNoArgsConstructor(
        final GenerationConfig generationConfig,
        final Annotator annotator,
        final SchemaStore schemaStore) {
      super(generationConfig, annotator, schemaStore, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link CheckerableRuleFactory.ExcludeNoArgsConstructor} factory with the specified configuration.
     * Uses the default annotator and schema store.
     *
     * @param generationConfig The generation configuration settings.
     */
    public ExcludeNoArgsConstructor(final GenerationConfig generationConfig) {
      super(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link CheckerableRuleFactory.ExcludeNoArgsConstructor} factory with default settings.
     */
    public ExcludeNoArgsConstructor() {
      super(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }
  }

  /**
   * {@link CheckerableRuleFactory.IncludeNoArgsConstructor} is a specialized {@link CheckerableRuleFactory} that always
   * includes no-argument constructors. This is achieved by setting the includeNoArgsConstructor flag to true.
   * <br />
   * <br />
   * This nested class provides a convenient way to instantiate a factory with predefined behavior.
   * <br />
   * <br />
   * Example:
   *
   * <pre>
   * CheckerableRuleFactory factory = new CheckerableRuleFactory.IncludeNoArgsConstructor();
   * </pre>
   *
   * @author lengors
   * @deprecated Use {@link io.github.lengors.js2pets.annotators.NullabilityAnnotator} instead with an instance of
   *             {@link EnhancedRuleFactory.IncludeNoArgsConstructor}.
   */
  @Deprecated(since = "1.2.0", forRemoval = true)
  public static class IncludeNoArgsConstructor extends CheckerableRuleFactory {
    /**
     * Default value indicating that no-argument constructors should be included.
     */
    private static final @Nullable Boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = true;

    /**
     * Constructs an {@link CheckerableRuleFactory.IncludeNoArgsConstructor} factory with the specified configuration,
     * annotator, and schema store.
     *
     * @param generationConfig The generation configuration settings.
     * @param annotator        The annotator used for code generation.
     * @param schemaStore      The schema store used to resolve schema references.
     */
    public IncludeNoArgsConstructor(
        final GenerationConfig generationConfig,
        final Annotator annotator,
        final SchemaStore schemaStore) {
      super(generationConfig, annotator, schemaStore, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link CheckerableRuleFactory.IncludeNoArgsConstructor} factory with the specified configuration.
     * Uses the default annotator and schema store.
     *
     * @param generationConfig The generation configuration settings.
     */
    public IncludeNoArgsConstructor(final GenerationConfig generationConfig) {
      super(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link CheckerableRuleFactory.IncludeNoArgsConstructor} factory with default settings.
     */
    public IncludeNoArgsConstructor() {
      super(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }
  }
}
