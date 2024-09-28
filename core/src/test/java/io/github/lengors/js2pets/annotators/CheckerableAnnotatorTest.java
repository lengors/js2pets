package io.github.lengors.js2pets.annotators;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMod;

@ExtendWith(MockitoExtension.class)
class CheckerableAnnotatorTest {
  @Test
  void shouldSkipAnnotationForNonDefinedClassType() {
    final var codeModel = new JCodeModel();
    final var checkerableAnnotator = new CheckerableAnnotator();
    final var booleanTypeSpy = Mockito.spy(codeModel.BOOLEAN);

    checkerableAnnotator.type(booleanTypeSpy);

    Mockito
        .verify(booleanTypeSpy, Mockito.never())
        .owner();
  }

  @Test
  void shouldAnnotateEqualsMethodParameter() throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();
    final var checkerableAnnotator = new CheckerableAnnotator();
    final var clazz = codeModel._class("io.github.lengors.js2pets.annotators.Test");
    final var method = clazz.method(JMod.PUBLIC, boolean.class, "equals");
    final var parameter = method.param(Object.class, "other");

    checkerableAnnotator.type(clazz);

    Assertions.assertTrue(parameter
        .annotations()
        .stream()
        .map(JAnnotationUse::getAnnotationClass)
        .map(JClass::fullName)
        .anyMatch(Nullable.class.getName()::equals));
  }

  @Test
  void shouldAnnotateSetterMethodParameters() throws JClassAlreadyExistsException {
    final var codeModel = new JCodeModel();
    final var checkerableAnnotator = new CheckerableAnnotator();
    final var clazz = codeModel._class("io.github.lengors.js2pets.annotators.Test");
    final var field1 = clazz.field(JMod.PUBLIC, Integer.class, "field1");
    final var setter0 = clazz.method(JMod.PUBLIC, void.class, "setField0");
    final var setter1 = clazz.method(JMod.PUBLIC, void.class, "setField1");
    final var param1 = setter1.param(Integer.class, "field1");

    clazz.field(JMod.PUBLIC, Integer.class, "field0");
    setter0.param(Integer.class, "field0");
    field1.annotate(Nullable.class);

    checkerableAnnotator.type(clazz);

    Assertions.assertTrue(param1
        .annotations()
        .stream()
        .map(JAnnotationUse::getAnnotationClass)
        .map(JClass::fullName)
        .anyMatch(Nullable.class.getName()::equals));
  }
}
