package io.github.lengors.js2pets.rules;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.collections4.IteratorUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;

/**
 * Not required rule for adding support to jsonschema2pojo for Checkerframework.
 *
 * @author lengors
 * @deprecated Use {@link io.github.lengors.js2pets.annotators.NullabilityAnnotator} instead with an instance of
 *             {@link io.github.lengors.js2pets.factories.EnhancedRuleFactory}.
 */
@Deprecated(since = "1.2.0", forRemoval = true)
public class CheckerableNotRequiredRule implements Rule<JDocCommentable, JDocCommentable> {

  /**
   * The object rule that must be obtained from the super rule's factory.
   */
  private final Rule<JDocCommentable, JDocCommentable> superNotRequiredRule;

  /**
   * Instantiates the rule with a parent rule injected.
   *
   * @param superNotRequiredRule The parent rule injected.
   */
  public CheckerableNotRequiredRule(final Rule<JDocCommentable, JDocCommentable> superNotRequiredRule) {
    this.superNotRequiredRule = superNotRequiredRule;
  }

  /**
   * Applies this rule by annotating the given generatable with {@link Nullable} if it's either a non-required field or
   * the respective getter.
   *
   * @param nodeName        The name of the JSON node being processed.
   * @param node            The JSON node to which the rule is being applied.
   * @param parent          The parent JSON node, or null if there isn't one.
   * @param generatableType The property field, getter or setter that is being generated from the JSON schema.
   * @param currentSchema   The current schema being processed.
   * @return The property field, getter or setter after applying the rule.
   */
  @Override
  public JDocCommentable apply(
      final String nodeName,
      final JsonNode node,
      final JsonNode parent,
      final JDocCommentable generatableType,
      final Schema currentSchema) {
    final var resultType = superNotRequiredRule.apply(nodeName, node, parent, generatableType, currentSchema);

    final var required = Optional
        .ofNullable(currentSchema
            .getContent()
            .get("required"))
        .map(JsonNode::elements)
        .map(IteratorUtils::toList)
        .orElseGet(Collections::emptyList);
    if (required
        .stream()
        .map(JsonNode::asText)
        .anyMatch(nodeName::equals)) {
      return resultType;
    }

    if (generatableType instanceof JFieldVar fieldVar) {
      fieldVar.annotate(Nullable.class);
    } else if (generatableType instanceof JMethod method && !method
        .type()
        .fullName()
        .equals("void")) {
      method.annotate(Nullable.class);
    }

    return resultType;
  }
}
