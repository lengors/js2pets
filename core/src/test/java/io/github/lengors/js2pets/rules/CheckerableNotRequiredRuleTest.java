package io.github.lengors.js2pets.rules;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JMod;

import io.github.lengors.js2pets.assertions.AssertionUtils;
import lombok.Getter;

@Getter
@ExtendWith(MockitoExtension.class)
class CheckerableNotRequiredRuleTest {

  /**
   * Mock rule used to simulate the super not required rule behavior.
   */
  @Mock
  @MonotonicNonNull
  private Rule<JDocCommentable, JDocCommentable> superNotRequiredRule;

  /**
   * Mock JSON node used for testing.
   */
  @Mock
  @MonotonicNonNull
  private JsonNode node;

  /**
   * Mock schema used for testing.
   */
  @Mock
  @MonotonicNonNull
  private Schema currentSchema;

  /**
   * Mock parent json node for required array.
   */
  @Mock
  @MonotonicNonNull
  private JsonNode superJsonNode;

  /**
   * Mock json node with required array.
   */
  @Mock
  @MonotonicNonNull
  private JsonNode requiredJsonNode;

  /**
   * Mock json node with required value for {@code field0}.
   */
  @Mock
  @MonotonicNonNull
  private JsonNode requiredField0JsonNode;

  @Test
  void shouldCorrectlyAnnotateNonRequiredPropertiesFieldsAndGettersWithNullable() throws JClassAlreadyExistsException {
    AssertionUtils.assertNotNull(superNotRequiredRule);

    final var codeModel = new JCodeModel();
    final var clazz = codeModel._class("io.github.lengors.js2pets.rules.Test");
    final var notRequiredRule = new CheckerableNotRequiredRule(superNotRequiredRule);

    final var field0 = clazz.field(JMod.PUBLIC, Integer.class, "field0");
    final var field1 = clazz.field(JMod.PUBLIC, Integer.class, "field1");
    final var getter0 = clazz.method(JMod.PUBLIC, Integer.class, "getField0");
    final var getter1 = clazz.method(JMod.PUBLIC, Integer.class, "getField1");

    testTarget(notRequiredRule, "field0", field0, Stream::noneMatch);
    testTarget(notRequiredRule, "field1", field1, Stream::anyMatch);
    testTarget(notRequiredRule, "field0", getter0, Stream::noneMatch);
    testTarget(notRequiredRule, "field1", getter1, Stream::anyMatch);
  }

  private <T extends JDocCommentable & JAnnotatable> void testTarget(
      final CheckerableNotRequiredRule notRequiredRule,
      final String nodeName,
      final T target,
      final BiFunction<Stream<String>, Predicate<Object>, Boolean> tester) {
    AssertionUtils.assertNotNull(superNotRequiredRule);
    AssertionUtils.assertNotNull(node);
    AssertionUtils.assertNotNull(currentSchema);
    AssertionUtils.assertNotNull(superJsonNode);
    AssertionUtils.assertNotNull(requiredJsonNode);
    AssertionUtils.assertNotNull(requiredField0JsonNode);

    Mockito
        .when(currentSchema.getContent())
        .thenReturn(superJsonNode);
    Mockito
        .when(superJsonNode.get("required"))
        .thenReturn(requiredJsonNode);
    Mockito
        .when(requiredJsonNode.elements())
        .thenReturn(IteratorUtils.arrayListIterator(new JsonNode[] {
            requiredField0JsonNode
        }));
    Mockito
        .when(requiredField0JsonNode.asText())
        .thenReturn("field0");
    Mockito
        .when(superNotRequiredRule.apply(nodeName, node, node, target, currentSchema))
        .thenReturn(target);

    final var result = notRequiredRule.apply(nodeName, node, node, target, currentSchema);

    Assertions.assertEquals(target, result);
    Assertions.assertTrue(tester.apply(target
        .annotations()
        .stream()
        .map(JAnnotationUse::getAnnotationClass)
        .map(JClass::fullName), Nullable.class.getName()::equals));
  }
}
