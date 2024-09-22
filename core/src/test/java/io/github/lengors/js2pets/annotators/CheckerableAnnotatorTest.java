package io.github.lengors.js2pets.annotators;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    final var annotations = parameter.annotations();
    Assertions.assertFalse(annotations.isEmpty());

    final var annotationClass = annotations
        .stream()
        .toList()
        .getFirst()
        .getAnnotationClass();

    Assertions.assertEquals(Nullable.class.getName(), annotationClass.fullName());
  }
}
