package io.github.lengors.js2pets.codemodel;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import io.github.lengors.js2pets.annotators.AnnotationUtils;
import io.github.lengors.js2pets.streams.StreamUtils;

/**
 * Utilities for code model related classes.
 *
 * @author lengors
 */
public final class CodeModelUtils {
  private CodeModelUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Safely annotates equals method for the given class.
   *
   * @param clazz The given class.
   */
  public static void annotateEqualsMethod(final JDefinedClass clazz) {
    final var objectRef = clazz
        .owner()
        ._ref(Object.class);

    Optional
        .ofNullable(clazz.getMethod("equals", new JType[] {
            objectRef
        }))
        .map(JMethod::params)
        .map(SequencedCollection::getFirst)
        .ifPresent(parameter -> safeAnnotate(parameter, AnnotationUtils.CHECKERFRAMEWORK_NULLABLE_ANNOTATION));
  }

  /**
   * Safely annotates the parameters for all the invokables in the given stream with the given list of annotations for
   * the matching fields.
   *
   * @param invokables  The invokables to annotate.
   * @param fieldNames  The set of field names to match the parameters with.
   * @param annotations The list of annotations to annotate with.
   */
  public static void annotateInvokablesParameters(
      final Stream<JMethod> invokables,
      final Set<String> fieldNames,
      final Collection<Class<? extends Annotation>> annotations) {
    invokables
        .map(JMethod::params)
        .flatMap(List::stream)
        .filter(param -> fieldNames.contains(param.name()))
        .forEach(param -> safeAnnotate(param, annotations));
  }

  /**
   * Determines if the given annotatable contains the given annotation.
   *
   * @param annotatable The annotatable to check for annotation.
   * @param annotation  The annotation type to check for.
   * @return True if the annotatable contains the given annotation. False, otherwise.
   */
  public static boolean containsAnnotation(
      final JAnnotatable annotatable,
      final Class<? extends Annotation> annotation) {
    return getAnnotationUsages(annotatable, annotation)
        .findAny()
        .isPresent();
  }

  /**
   * Get all annotation usages for the given annotation type on the given annotatable.
   *
   * @param annotatable The given annotatable.
   * @param annotation  The given annotation type.
   * @return All annotation usages.
   */
  public static Stream<JAnnotationUse> getAnnotationUsages(
      final JAnnotatable annotatable,
      final Class<? extends Annotation> annotation) {
    final var annotationName = annotation.getName();
    return annotatable
        .annotations()
        .stream()
        .filter(annotationUse -> annotationUse
            .getAnnotationClass()
            .fullName()
            .equals(annotationName));
  }

  /**
   * Retrieves {@link JsonProperty} value associated with given field.
   *
   * @param field The given field.
   * @return The json property value.
   */
  public static @Nullable String getJsonPropertyValue(final JFieldVar field) {
    return getAnnotationUsages(field, JsonProperty.class)
        .map(annotation -> {
          final var annotationValue = annotation
              .getAnnotationMembers()
              .get("value");

          if (annotationValue == null) {
            return null;
          }

          try (var writer = new StringWriter()) {
            final var formatter = new JFormatter(writer);
            annotationValue.generate(formatter);
            final var stringifiedPropertyValue = writer.toString();
            return stringifiedPropertyValue.substring(1, stringifiedPropertyValue.length() - 1);
          } catch (final IOException exception) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  /**
   * Retries property name associated with field.
   *
   * @param field The field from which to retrieve the property name.
   * @return The property name.
   */
  public static @Nullable String getPropertyName(final JFieldVar field) {
    final var jsonProperty = getJsonPropertyValue(field);
    if (jsonProperty == null) {
      return null;
    }
    final var property = field.name();
    return JJavaName.isJavaIdentifier(jsonProperty) ? property : property.substring(1);
  }

  /**
   * Creates {@link List} with class structure of given outer class.
   *
   * @param outerClass The outer class.
   * @return The class structure as a {@link List} of classes.
   */
  public static List<JDefinedClass> listClassStructure(final JDefinedClass outerClass) {
    return streamClassStructure(outerClass).toList();
  }

  /**
   * Creates {@link List} with all invokables in the given class structure.
   *
   * @param classStructure The class structure.
   * @return The invokables as a {@link List}.
   */
  public static List<JMethod> listInvokables(final List<JDefinedClass> classStructure) {
    return streamInvokables(classStructure).toList();
  }

  /**
   * Creates {@link Stream} with class structure of given outer class.
   *
   * @param outerClass The outer class.
   * @return The class structure as a {@link Stream} of classes.
   */
  public static Stream<JDefinedClass> streamClassStructure(final JDefinedClass outerClass) {
    return Stream.concat(
        Stream.of(outerClass),
        StreamUtils
            .stream(outerClass.classes())
            .flatMap(CodeModelUtils::streamClassStructure));
  }

  /**
   * Creates {@link Stream} with all invokables in the given class structure.
   *
   * @param classStructure The class structure.
   * @return The invokables as a {@link Stream}.
   */
  public static Stream<JMethod> streamInvokables(final List<JDefinedClass> classStructure) {
    return Stream.concat(
        classStructure
            .stream()
            .flatMap(clazz -> clazz
                .methods()
                .stream()),
        classStructure
            .stream()
            .flatMap(clazz -> StreamUtils.stream(clazz.constructors())));
  }

  /**
   * Safely annotates the given annotatable with the given list of annotations.
   *
   * @param annotatable The annotatable to annotate.
   * @param annotations The list of annotations to annotate with.
   */
  public static void safeAnnotate(
      final JAnnotatable annotatable,
      final Collection<Class<? extends Annotation>> annotations) {
    final var annotatableType = switch (annotatable) {
      case JMethod method -> method.type();
      case JVar jVar -> jVar.type();
      default -> null;
    };
    final var type = Optional
        .ofNullable(annotatableType)
        .filter(JDefinedClass.class::isInstance)
        .map(JDefinedClass.class::cast)
        .orElse(null);
    final var annotationStream = annotations
        .stream()
        .filter(annotation -> !CodeModelUtils.containsAnnotation(annotatable, annotation));
    final var postProcessedAnnotationStream = type != null
        ? annotationStream.filter(annotation -> !type
            .parentContainer()
            .isClass() || !AnnotationUtils.CHECKERFRAMEWORK_NULLABILITY_ANNOTATIONS.contains(annotation.getName()))
        : annotationStream;
    postProcessedAnnotationStream.forEach(annotatable::annotate);
  }
}
