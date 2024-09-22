package io.github.lengors.js2pets.annotators;

import org.jsonschema2pojo.CompositeAnnotator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.codemodel.JMethod;

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
}
