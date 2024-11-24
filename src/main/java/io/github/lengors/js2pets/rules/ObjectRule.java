package io.github.lengors.js2pets.rules;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.annotators.AnnotatorUtils;

/**
 * Object rule wrapper providing support for notifying the annotator when the type is finished generating and if the
 * annotator supports the respective callback.
 *
 * @author lengors
 */
public class ObjectRule implements Rule<JPackage, JType> {
  /**
   * Rule factory from where we get the annotator.
   */
  private final RuleFactory ruleFactory;

  /**
   * The object rule that must be obtained from the super rule's factory.
   */
  private final Rule<JPackage, JType> superObjectRule;

  /**
   * Constructs an object rule with given configuration.
   *
   * @param ruleFactory     A rule factory to inject.
   * @param superObjectRule The parent object rule to inject.
   */
  public ObjectRule(final RuleFactory ruleFactory, final Rule<JPackage, JType> superObjectRule) {
    this.ruleFactory = ruleFactory;
    this.superObjectRule = superObjectRule;
  }

  /**
   * Applies this rule to the given {@link JType}, notifying the annotator if applicable.
   *
   * @param nodeName        The name of the JSON node being processed.
   * @param node            The JSON node to which the rule is being applied.
   * @param parent          The parent JSON node, or null if there isn't one.
   * @param generatableType The package model where the generated type is contained.
   * @param currentSchema   The current schema being processed.
   * @return The {@link JType} after applying the rule.
   */
  @Override
  public JType apply(
      final String nodeName,
      final JsonNode node,
      final JsonNode parent,
      final JPackage generatableType,
      final Schema currentSchema) {
    final var newType = superObjectRule.apply(nodeName, node, parent, generatableType, currentSchema);
    AnnotatorUtils.type(ruleFactory.getAnnotator(), newType);
    return newType;
  }
}
