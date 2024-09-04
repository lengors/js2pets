package io.github.lengors.js2pets.factories;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.sun.codemodel.JDefinedClass;

import io.github.lengors.js2pets.rules.ConstructorRule;

/**
 * {@link EnhancedRuleFactory} is a custom implementation of the {@link RuleFactory} that provides additional
 * functionality for generating or excluding no-argument constructors based on configuration settings. It extends the
 * jsonschema2pojo's {@link RuleFactory} and allows the creation of {@link ConstructorRule} instances with more flexible
 * configurations.
 * <br />
 * <br />
 * This factory can be configured to either include or exclude no-args constructors through various constructors.
 * Additionally, it offers nested factory classes for predefined configurations.
 *
 * @author lengors
 */
public class EnhancedRuleFactory extends RuleFactory {
  /**
   * Default value indicating that whether to include no-argument constructors or not is infer from the
   * jsonschema2pojo's plugin configuration.
   */
  private static final @Nullable Boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = null;

  /**
   * Flag determining whether the no-args constructor should be included in the resulting classes. If null, the
   * configuration is determined from jsonschema2pojo's plugin configuration.
   */
  private final @Nullable Boolean includeNoArgsConstructor;

  /**
   * Constructs an {@link EnhancedRuleFactory} with the specified configuration, annotator, schema store, and no-args
   * constructor inclusion setting.
   *
   * @param generationConfig         The generation configuration settings.
   * @param annotator                The annotator used for code generation.
   * @param schemaStore              The schema store used to resolve schema references.
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   */
  protected EnhancedRuleFactory(
      final GenerationConfig generationConfig,
      final Annotator annotator,
      final SchemaStore schemaStore,
      final @Nullable Boolean includeNoArgsConstructor) {
    super(generationConfig, annotator, schemaStore);
    this.includeNoArgsConstructor = includeNoArgsConstructor;
  }

  /**
   * Constructs an {@link EnhancedRuleFactory} with the specified configuration and no-args constructor inclusion
   * setting. Uses the {@link Jackson2Annotator} by default.
   *
   * @param generationConfig         The generation configuration settings.
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   */
  protected EnhancedRuleFactory(
      final GenerationConfig generationConfig,
      final @Nullable Boolean includeNoArgsConstructor) {
    this(generationConfig, new Jackson2Annotator(generationConfig), new SchemaStore(), includeNoArgsConstructor);
  }

  /**
   * Constructs an {@link EnhancedRuleFactory} with a default configuration and the specified no-args constructor
   * inclusion setting.
   *
   * @param includeNoArgsConstructor Whether to include the no-args constructor.
   */
  protected EnhancedRuleFactory(final @Nullable Boolean includeNoArgsConstructor) {
    this(new DefaultGenerationConfig(), includeNoArgsConstructor);
  }

  /**
   * Constructs an {@link EnhancedRuleFactory} with the specified configuration, annotator, and schema store, using the
   * default setting for including no-args constructors.
   *
   * @param generationConfig The generation configuration settings.
   * @param annotator        The annotator used for code generation.
   * @param schemaStore      The schema store used to resolve schema references.
   */
  public EnhancedRuleFactory(
      final GenerationConfig generationConfig,
      final Annotator annotator,
      final SchemaStore schemaStore) {
    this(generationConfig, annotator, schemaStore, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Constructs an {@link EnhancedRuleFactory} with the specified configuration, using the default setting for including
   * no-args constructors and the default annotator and schema store.
   *
   * @param generationConfig The generation configuration settings.
   */
  public EnhancedRuleFactory(final GenerationConfig generationConfig) {
    this(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Constructs an {@link EnhancedRuleFactory} with default settings. Uses default values for the generation
   * configuration, annotator, schema store, and no-args constructor inclusion.
   */
  public EnhancedRuleFactory() {
    this(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
  }

  /**
   * Returns a custom {@code ConstructorRule} that controls the inclusion of no-argument constructors. This rule extends
   * the base constructor rule with the ability to optionally remove no-args constructors based on the factory's
   * configuration.
   *
   * @return A {@link ConstructorRule} configured according to the factory settings.
   */
  @Override
  public Rule<JDefinedClass, JDefinedClass> getConstructorRule() {
    return new ConstructorRule(this, includeNoArgsConstructor, super.getConstructorRule());
  }

  /**
   * {@link ExcludeNoArgsConstructor} is a specialized {@link EnhancedRuleFactory} that always excludes no-argument
   * constructors. This is achieved by setting the includeNoArgsConstructor flag to false.
   * <br />
   * <br />
   * This nested class provides a convenient way to instantiate a factory with predefined behavior.
   * <br />
   * <br />
   * Example:
   *
   * <pre>
   * EnhancedRuleFactory factory = new EnhancedRuleFactory.ExcludeNoArgsConstructor();
   * </pre>
   *
   * @author lengors
   */
  public static class ExcludeNoArgsConstructor extends EnhancedRuleFactory {
    /**
     * Default value indicating that no-argument constructors should be excluded.
     */
    private static final @Nullable Boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = false;

    /**
     * Constructs an {@link ExcludeNoArgsConstructor} factory with the specified configuration, annotator, and schema
     * store.
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
     * Constructs an {@link ExcludeNoArgsConstructor} factory with the specified configuration. Uses the default
     * annotator and schema store.
     *
     * @param generationConfig The generation configuration settings.
     */
    public ExcludeNoArgsConstructor(final GenerationConfig generationConfig) {
      super(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link ExcludeNoArgsConstructor} factory with default settings.
     */
    public ExcludeNoArgsConstructor() {
      super(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }
  }

  /**
   * {@link IncludeNoArgsConstructor} is a specialized {@link EnhancedRuleFactory} that always includes no-argument
   * constructors. This is achieved by setting the includeNoArgsConstructor flag to true.
   * <br />
   * <br />
   * This nested class provides a convenient way to instantiate a factory with predefined behavior.
   * <br />
   * <br />
   * Example:
   *
   * <pre>
   * EnhancedRuleFactory factory = new EnhancedRuleFactory.IncludeNoArgsConstructor();
   * </pre>
   *
   * @author lengors
   */
  public static class IncludeNoArgsConstructor extends EnhancedRuleFactory {
    /**
     * Default value indicating that no-argument constructors should be included.
     */
    private static final @Nullable Boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = true;

    /**
     * Constructs an {@link IncludeNoArgsConstructor} factory with the specified configuration, annotator, and schema
     * store.
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
     * Constructs an {@link IncludeNoArgsConstructor} factory with the specified configuration. Uses the default
     * annotator and schema store.
     *
     * @param generationConfig The generation configuration settings.
     */
    public IncludeNoArgsConstructor(final GenerationConfig generationConfig) {
      super(generationConfig, DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }

    /**
     * Constructs an {@link IncludeNoArgsConstructor} factory with default settings.
     */
    public IncludeNoArgsConstructor() {
      super(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    }
  }
}
