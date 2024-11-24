package io.github.lengors.js2pets.codemodel;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Stream;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
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
    return annotatable
        .annotations()
        .stream()
        .map(JAnnotationUse::getAnnotationClass)
        .map(JClass::fullName)
        .anyMatch(annotation.getName()::equals);
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
