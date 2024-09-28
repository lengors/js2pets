package io.github.lengors.js2pets.rules;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.annotators.EnhancedAnnotator;
import io.github.lengors.js2pets.assertions.AssertionUtils;
import lombok.Getter;

@Getter
@ExtendWith(MockitoExtension.class)
class ObjectRuleTest {
  /**
   * The node name used for applying the rule, initialized as an empty string.
   */
  private static final String NODE_NAME = StringUtils.EMPTY;

  /**
   * The rule under test for generating types in a package.
   */
  @Mock
  @MonotonicNonNull
  private Rule<JPackage, JType> superObjectRule;

  @Test
  void shouldCorrectlyNotifyAnnotator() {
    AssertionUtils.assertNotNull(superObjectRule);
    final var annotator = Mockito.mock(EnhancedAnnotator.class);
    final var type = Mockito.mock(JType.class);
    final var ruleFactory = Mockito.mock(RuleFactory.class);
    final var node = Mockito.mock(JsonNode.class);
    final var currentSchema = Mockito.mock(Schema.class);

    final var codeModel = new JCodeModel();
    final var jPackage = codeModel._package("io.github.lengors.js2pets.rules");

    Mockito
        .when(superObjectRule.apply(NODE_NAME, node, node, jPackage, currentSchema))
        .thenReturn(type);
    Mockito
        .when(ruleFactory.getAnnotator())
        .thenReturn(annotator);

    final var rule = new ObjectRule(ruleFactory, superObjectRule);
    final var result = rule.apply(NODE_NAME, node, node, jPackage, currentSchema);

    Assertions.assertEquals(type, result);
    Mockito
        .verify(annotator, Mockito.only())
        .type(type);
  }
}
