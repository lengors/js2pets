package io.github.lengors.js2pets.rules;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

import io.github.lengors.js2pets.annotators.AnnotatorUtils;
import io.github.lengors.js2pets.rules.exceptions.ConfigurationPropertyMissingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Constructor rule wrapper that removes the no-args constructor if the respective flag is enabled. This rule also
 * notifies the annotator if it supports constructor callbacks.
 * <br />
 * <br />
 * This class extends the functionality provided by the jsonschema2pojo library by adding a customizable rule for
 * generating or omitting no-argument constructors based on configuration settings.
 *
 * @author lengors
 */
public class ConstructorRule implements Rule<JDefinedClass, JDefinedClass> {
  /**
   * Rule factory from where we get generation configuration among other utilities.
   */
  private final RuleFactory ruleFactory;

  /**
   * Flag determining whether the no-args constructor should be included in the resulting set of constructors or not.
   * Leaving the flag null will lead the rule to try to infer it from the plugin's configuration.
   */
  private final AtomicReference<Boolean> includeNoArgsConstructorRef;

  /**
   * The constructor rule that must be obtained from the super rule's factory.
   */
  private final Rule<JDefinedClass, JDefinedClass> superConstructorRule;

  /**
   * Constructs constructor rule with given configuration.
   *
   * @param ruleFactory              The rule factory managing this constructor rule.
   * @param includeNoArgsConstructor If it should include the no-args constructor or not, or infer it from
   *                                 configuration.
   * @param superConstructorRule     The super constructor rule to which part of the generation is delegated to.
   * @throws IllegalArgumentException                                                         Thrown if the include
   *                                                                                          no-args constructor flag
   *                                                                                          is disabled and
   *                                                                                          generating builders using
   *                                                                                          inner classes is enabled.
   * @throws io.github.lengors.js2pets.rules.exceptions.ConfigurationPropertyMissingException Thrown if the include
   *                                                                                          no-args constructor flag
   *                                                                                          value isn't set and
   *                                                                                          cannot be inferred.
   */
  public ConstructorRule(
      final RuleFactory ruleFactory,
      final @Nullable Boolean includeNoArgsConstructor,
      final Rule<JDefinedClass, JDefinedClass> superConstructorRule) {
    this.ruleFactory = ruleFactory;
    this.includeNoArgsConstructorRef = includeNoArgsConstructor != null
        ? new AtomicReference<>(includeNoArgsConstructor)
        : new AtomicReference<>();
    this.superConstructorRule = superConstructorRule;

    final var generationConfig = ruleFactory.getGenerationConfig();
    if (generationConfig.isGenerateBuilders()
        && generationConfig.isUseInnerClassBuilders()
        && !isIncludeNoArgsConstructor(ruleFactory, includeNoArgsConstructorRef)) {
      throw new IllegalArgumentException("Generation of builders is not supported with current configuration");
    }
  }

  /**
   * Applies this rule to the given {@link JDefinedClass}, potentially removing the no-args constructor and notifying
   * the annotator if applicable.
   *
   * @param nodeName      The name of the JSON node being processed.
   * @param node          The JSON node to which the rule is being applied.
   * @param parent        The parent JSON node, or null if there isn't one.
   * @param type          The Java class that is being generated from the JSON schema.
   * @param currentSchema The current schema being processed.
   * @return The {@link JDefinedClass} after applying the rule.
   * @throws io.github.lengors.js2pets.rules.exceptions.ConfigurationPropertyMissingException Thrown if the include
   *                                                                                          no-args constructor flag
   *                                                                                          value isn't set and
   *                                                                                          cannot be inferred.
   */
  @Override
  public JDefinedClass apply(
      final String nodeName,
      final JsonNode node,
      final JsonNode parent,
      final JDefinedClass type,
      final Schema currentSchema) {
    final var clazz = superConstructorRule.apply(nodeName, node, parent, type, currentSchema);

    if (!isIncludeNoArgsConstructor(ruleFactory, includeNoArgsConstructorRef)) {
      removeConstructors(clazz, ruleFactory);
    }

    final var annotator = ruleFactory.getAnnotator();
    IteratorUtils.forEach(clazz.constructors(), constructor -> AnnotatorUtils.constructor(annotator, constructor));

    return clazz;
  }

  private static <T> Optional<T> invokeMethod(
      final Method method,
      final Object target,
      final Class<? extends T> targetType) {
    try {
      return Optional
          .ofNullable(method.invoke(target))
          .filter(targetType::isInstance)
          .map(targetType::cast);
    } catch (final IllegalAccessException | InvocationTargetException exception) {
      return Optional.empty();
    }
  }

  private static boolean isIncludeNoArgsConstructor(
      final RuleFactory ruleFactory,
      final AtomicReference<Boolean> includeNoArgsConstructorRef) {
    return includeNoArgsConstructorRef.updateAndGet(previous -> {
      if (previous != null) {
        return previous;
      }

      final var generationConfig = ruleFactory.getGenerationConfig();
      final var method = MethodUtils.getAccessibleMethod(generationConfig.getClass(), "getPluginContext");
      if (method == null) {
        throw new ConfigurationPropertyMissingException(INCLUDE_NO_ARGS_CONSTRUCTOR_KEY);
      }

      final var pluginContext = invokeMethod(method, generationConfig, Map.class)
          .orElseGet(Collections::emptyMap);
      final var optPlugin = Optional
          .ofNullable(pluginContext.get("pluginDescriptor"))
          .filter(PluginDescriptor.class::isInstance)
          .map(PluginDescriptor.class::cast)
          .map(PluginDescriptor::getPlugin);

      final var executionsCount = optPlugin
          .map(Plugin::getExecutions)
          .map(List::size)
          .orElse(0);

      if (executionsCount > 1) {
        throw new ConfigurationPropertyMissingException(INCLUDE_NO_ARGS_CONSTRUCTOR_KEY);
      }

      return optPlugin
          .map(Plugin::getConfiguration)
          .filter(Xpp3Dom.class::isInstance)
          .map(Xpp3Dom.class::cast)
          .map(configuration -> configuration.getChild(INCLUDE_NO_ARGS_CONSTRUCTOR_KEY))
          .map(Xpp3Dom::getValue)
          .map(Boolean::parseBoolean)
          .orElse(DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR);
    });
  }

  private static @Nullable Object readFieldValue(
      final JDefinedClass target,
      final Field field,
      final RuleFactory ruleFactory) {
    if (Modifier.isStatic(field.getModifiers())) {
      return null;
    }

    try {
      return FieldUtils.readField(field, target, true);
    } catch (final IllegalAccessException exception) {
      ruleFactory
          .getLogger()
          .warn(String.format("Could not access JDefinedClass{} field Field{name=%s}", field.getName()), exception);
    }
    return null;
  }

  private static void removeConstructors(final JDefinedClass clazz, final RuleFactory ruleFactory) {
    final var knownConstructors = IteratorUtils.toList(clazz.constructors());
    for (final var field : FieldUtils.getAllFields(clazz.getClass())) {
      if (readFieldValue(clazz, field, ruleFactory) instanceof Collection<?> collection) {
        final var noArgsConstructors = new ArrayList<JMethod>();
        for (final var element : collection) {
          if (element instanceof JMethod method
              && method.listParams().length == 0
              && knownConstructors.contains(method)) {
            noArgsConstructors.add(method);
          }
        }
        noArgsConstructors.forEach(method -> remove(collection, method));
      }
    }
  }

  private static boolean remove(final Collection<?> collection, final Object value) {
    try {
      return collection.remove(value);
    } catch (final UnsupportedOperationException exception) {
      return false;
    }
  }

  /**
   * The key used to retrieve the "includeNoArgsConstructor" configuration property.
   */
  private static final String INCLUDE_NO_ARGS_CONSTRUCTOR_KEY = "includeNoArgsConstructor";

  /**
   * The default value for whether the no-args constructor should be included.
   */
  private static final boolean DEFAULT_INCLUDE_NO_ARGS_CONSTRUCTOR = true;
}
