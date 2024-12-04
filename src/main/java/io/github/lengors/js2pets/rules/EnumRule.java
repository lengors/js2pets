package io.github.lengors.js2pets.rules;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.annotators.AnnotatorUtils;

/**
 * Enum rule wrapper providing support for notifying the annotator when the type is finished generating and if the
 * annotator supports the respective callback.
 *
 * @author lengors
 */
public class EnumRule implements Rule<JClassContainer, JType> {
  /**
   * Rule factory from where we get the annotator.
   */
  private final RuleFactory ruleFactory;

  /**
   * The enum rule that must be obtained from the super rule's factory.
   */
  private final Rule<JClassContainer, JType> superEnumRule;

  /**
   * Constructs an enum rule with given configuration.
   *
   * @param ruleFactory   A rule factory to inject.
   * @param superEnumRule The parent enum rule to inject.
   */
  public EnumRule(final RuleFactory ruleFactory, final Rule<JClassContainer, JType> superEnumRule) {
    this.ruleFactory = ruleFactory;
    this.superEnumRule = superEnumRule;
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
      final JClassContainer generatableType,
      final Schema currentSchema) {
    final var newType = superEnumRule.apply(nodeName, node, parent, generatableType, currentSchema);
    AnnotatorUtils.type(ruleFactory.getAnnotator(), newType);
    return newType;
  }
}
