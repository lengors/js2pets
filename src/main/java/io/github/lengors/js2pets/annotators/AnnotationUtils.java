package io.github.lengors.js2pets.annotators;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.GenerationConfig;

/**
 * Annotation utilities.
 *
 * @author lengors
 */
public final class AnnotationUtils {
  /**
   * Checkerframework nullable annotation.
   */
  public static final List<Class<? extends Annotation>> CHECKERFRAMEWORK_NULLABLE_ANNOTATION = List.of(Nullable.class);

  /**
   * Checkerframework nullability annotations.
   */
  public static final Set<String> CHECKERFRAMEWORK_NULLABILITY_ANNOTATIONS = Stream
      .of(NonNull.class, Nullable.class)
      .map(Class::getName)
      .collect(Collectors.toUnmodifiableSet());

  /**
   * Non-nullable annotations.
   */
  public static final List<Class<? extends Annotation>> NON_NULLABLE_ANNOTATIONS = List.of(
      jakarta.validation.constraints.NotNull.class,
      javax.validation.constraints.NotNull.class,
      jakarta.annotation.Nonnull.class,
      javax.annotation.Nonnull.class,
      NonNull.class);

  /**
   * Nullable annotations.
   */
  public static final List<Class<? extends Annotation>> NULLABLE_ANNOTATIONS = List.of(
      jakarta.annotation.Nullable.class,
      javax.annotation.Nullable.class,
      Nullable.class);

  private AnnotationUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets the non-nullable annotations based on generation configuration.
   *
   * @param generationConfig The generation configuration.
   * @return The non-nullable annotation classes.
   */
  public static List<Class<? extends Annotation>> getNonNullableAnnotations(final GenerationConfig generationConfig) {
    final var annotations = new ArrayList<Class<? extends Annotation>>();
    if (generationConfig.isIncludeJsr303Annotations()) {
      if (generationConfig.isUseJakartaValidation()) {
        annotations.add(jakarta.validation.constraints.NotNull.class);
      } else {
        annotations.add(javax.validation.constraints.NotNull.class);
      }
    }

    if (generationConfig.isIncludeJsr305Annotations()) {
      if (generationConfig.isUseJakartaValidation()) {
        annotations.add(jakarta.annotation.Nonnull.class);
      } else {
        annotations.add(javax.annotation.Nonnull.class);
      }
    }

    annotations.add(NonNull.class);

    return Collections.unmodifiableList(annotations);
  }

  /**
   * Gets the nullable annotations based on generation configuration.
   *
   * @param generationConfig The generation configuration.
   * @return The nullable annotation classes.
   */
  public static List<Class<? extends Annotation>> getNullableAnnotations(final GenerationConfig generationConfig) {
    final var annotations = new ArrayList<Class<? extends Annotation>>();
    if (generationConfig.isIncludeJsr305Annotations()) {
      if (generationConfig.isUseJakartaValidation()) {
        annotations.add(jakarta.annotation.Nullable.class);
      } else {
        annotations.add(javax.annotation.Nullable.class);
      }
    }

    annotations.add(Nullable.class);

    return Collections.unmodifiableList(annotations);
  }
}
