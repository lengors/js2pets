package io.github.lengors.js2pets.annotators;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.GenerationConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMod;

import io.github.lengors.js2pets.codemodel.CodeModelUtils;

@ExtendWith(MockitoExtension.class)
class NullabilityAnnotatorTest {
  @Test
  void shouldSkipAnnotationForNonDefinedClassType() {
    final var codeModel = new JCodeModel();
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    final var nullabilityAnnotator = new NullabilityAnnotator(generationConfig);
    final var booleanTypeSpy = Mockito.spy(codeModel.BOOLEAN);

    nullabilityAnnotator.type(booleanTypeSpy);

    Mockito
        .verify(booleanTypeSpy, Mockito.never())
        .owner();
  }

  @Test
  void shouldAnnotateEqualsMethodParameter() throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    final var nullabilityAnnotator = new NullabilityAnnotator(generationConfig);
    final var clazz = codeModel._class("io.github.lengors.js2pets.annotators.Test");
    final var method = clazz.method(JMod.PUBLIC, boolean.class, "equals");
    final var parameter = method.param(Object.class, "other");

    nullabilityAnnotator.type(clazz);

    Assertions.assertTrue(CodeModelUtils.containsAnnotation(parameter, Nullable.class));
  }

  @Test
  void shouldAnnotateMainConstructor() throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    final var nullabilityAnnotator = new NullabilityAnnotator(generationConfig);
    final var clazz = codeModel._class("io.github.lengors.js2pets.annotators.Test");
    final var field0 = clazz.field(JMod.PUBLIC, Integer.class, "field0");
    final var field1 = clazz.field(JMod.PUBLIC, Integer.class, "field1");
    final var constructor = clazz.constructor(JMod.PUBLIC);
    final var param0 = constructor.param(Integer.class, "field0");
    final var param1 = constructor.param(Integer.class, "field1");
    field0
        .annotate(JsonProperty.class)
        .param("value", "field0");
    field1
        .annotate(JsonProperty.class)
        .param("value", "field1");

    nullabilityAnnotator.type(clazz);

    Assertions.assertTrue(CodeModelUtils.containsAnnotation(param0, JsonProperty.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(param1, JsonProperty.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(constructor, JsonCreator.class));
  }

  @Test
  void shouldAnnotateGetterAndSetterMethodParameters() throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();
    final var generationConfig = Mockito.mock(GenerationConfig.class);

    Mockito
        .when(generationConfig.isUseJakartaValidation())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(false);

    final var nullabilityAnnotator = new NullabilityAnnotator(generationConfig);
    final var clazz = codeModel._class("io.github.lengors.js2pets.annotators.Test");
    final var field0 = clazz.field(JMod.PUBLIC, Integer.class, "field0");
    final var field1 = clazz.field(JMod.PUBLIC, Integer.class, "field1");
    final var getter0 = clazz.method(JMod.PUBLIC, Integer.class, "getField0");
    final var getter1 = clazz.method(JMod.PUBLIC, Integer.class, "getField1");
    final var setter0 = clazz.method(JMod.PUBLIC, void.class, "setField0");
    final var setter1 = clazz.method(JMod.PUBLIC, void.class, "setField1");
    final var param0 = setter0.param(Integer.class, "field0");
    final var param1 = setter1.param(Integer.class, "field1");

    field0
        .annotate(JsonProperty.class)
        .param("value", "field0");
    field1
        .annotate(JsonProperty.class)
        .param("value", "field1");
    param0
        .annotate(JsonProperty.class)
        .param("value", "field0");
    param1
        .annotate(JsonProperty.class)
        .param("value", "field1");
    field1.annotate(jakarta.validation.constraints.NotNull.class);
    nullabilityAnnotator.type(clazz);

    Assertions.assertTrue(CodeModelUtils.containsAnnotation(param0, Nullable.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(param1, NonNull.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(param1, jakarta.validation.constraints.NotNull.class));

    Assertions.assertTrue(CodeModelUtils.containsAnnotation(field0, Nullable.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(field1, NonNull.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(field1, jakarta.validation.constraints.NotNull.class));

    Assertions.assertTrue(CodeModelUtils.containsAnnotation(getter0, Nullable.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(getter1, NonNull.class));
    Assertions.assertTrue(CodeModelUtils.containsAnnotation(getter1, jakarta.validation.constraints.NotNull.class));
  }
}
