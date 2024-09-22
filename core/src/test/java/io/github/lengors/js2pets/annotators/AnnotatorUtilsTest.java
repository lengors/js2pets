package io.github.lengors.js2pets.annotators;

import org.jsonschema2pojo.CompositeAnnotator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

@ExtendWith(MockitoExtension.class)
class AnnotatorUtilsTest {
  @Test
  void shouldCorrectlyNotifyAnnotatorWithConstructor() {

    final var testConstructor = Mockito.mock(JMethod.class);
    final var enhancedAnnotator = Mockito.mock(EnhancedAnnotator.class);
    final var compositeAnnotator = new CompositeAnnotator(enhancedAnnotator);

    AnnotatorUtils.constructor(compositeAnnotator, testConstructor);

    Mockito
        .verify(enhancedAnnotator, Mockito.only())
        .constructor(testConstructor);
  }

  @Test
  void shouldCorrectlyNotifyAnnotatorWithType() {

    final var testType = Mockito.mock(JType.class);
    final var enhancedAnnotator = Mockito.mock(EnhancedAnnotator.class);
    final var compositeAnnotator = new CompositeAnnotator(enhancedAnnotator);

    AnnotatorUtils.type(compositeAnnotator, testType);

    Mockito
        .verify(enhancedAnnotator, Mockito.only())
        .type(testType);
  }
}
