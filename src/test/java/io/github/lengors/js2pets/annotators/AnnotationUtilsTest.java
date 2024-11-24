package io.github.lengors.js2pets.annotators;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.GenerationConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.lengors.js2pets.assertions.AssertionUtils;

@ExtendWith(MockitoExtension.class)
class AnnotationUtilsTest {
  @Test
  void shouldCorrectlyReturnNullabilityAnnotationsForJsr305WithJakartaValidation() {
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    Mockito
        .when(generationConfig.isUseJakartaValidation())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(false);

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(generationConfig);
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(generationConfig);

    AssertionUtils.assertListMatches(List.of(Nullable.class, jakarta.annotation.Nullable.class), nullableAnnotations);
    AssertionUtils.assertListMatches(List.of(NonNull.class, jakarta.annotation.Nonnull.class), nonNullableAnnotations);
  }

  @Test
  void shouldCorrectlyReturnNullabilityAnnotationsForJsr305WithoutJakartaValidation() {
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    Mockito
        .when(generationConfig.isUseJakartaValidation())
        .thenReturn(false);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(false);

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(generationConfig);
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(generationConfig);

    AssertionUtils.assertListMatches(List.of(Nullable.class, javax.annotation.Nullable.class), nullableAnnotations);
    AssertionUtils.assertListMatches(List.of(NonNull.class, javax.annotation.Nonnull.class), nonNullableAnnotations);
  }

  @Test
  void shouldCorrectlyReturnNullabilityAnnotationsForJsr303WithJakartaValidation() {
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    Mockito
        .when(generationConfig.isUseJakartaValidation())
        .thenReturn(true);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(false);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(true);

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(generationConfig);
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(generationConfig);

    AssertionUtils.assertListMatches(List.of(Nullable.class), nullableAnnotations);
    AssertionUtils.assertListMatches(
        List.of(NonNull.class, jakarta.validation.constraints.NotNull.class),
        nonNullableAnnotations);
  }

  @Test
  void shouldCorrectlyReturnNullabilityAnnotationsForJsr303WithoutJakartaValidation() {
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    Mockito
        .when(generationConfig.isUseJakartaValidation())
        .thenReturn(false);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(false);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(true);

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(generationConfig);
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(generationConfig);

    AssertionUtils.assertListMatches(List.of(Nullable.class), nullableAnnotations);
    AssertionUtils.assertListMatches(
        List.of(NonNull.class, javax.validation.constraints.NotNull.class),
        nonNullableAnnotations);
  }

  @Test
  void shouldCorrectlyReturnNullabilityAnnotationsForWithoutJsr303OrJsr305() {
    final var generationConfig = Mockito.mock(GenerationConfig.class);
    Mockito
        .when(generationConfig.isIncludeJsr305Annotations())
        .thenReturn(false);
    Mockito
        .when(generationConfig.isIncludeJsr303Annotations())
        .thenReturn(false);

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(generationConfig);
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(generationConfig);

    AssertionUtils.assertListMatches(List.of(Nullable.class), nullableAnnotations);
    AssertionUtils.assertListMatches(List.of(NonNull.class), nonNullableAnnotations);
  }
}
