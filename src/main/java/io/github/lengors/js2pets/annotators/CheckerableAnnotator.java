package io.github.lengors.js2pets.annotators;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IteratorUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.NoopAnnotator;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;

/**
 * An annotator that makes the generated classes compatible with the Checkerframework.
 *
 * @author lengors
 */
public class CheckerableAnnotator extends NoopAnnotator implements EnhancedAnnotator {

  /**
   * Annotator callback that annotates equals methods' parameter with Checkerframework's {@link Nullable} as well as any
   * parameters for the respective field already annotated with {@link Nullable}.
   *
   * @param type The generated class to apply the annotation to.
   */
  @Override
  public void type(final JType type) {
    if (!(type instanceof JDefinedClass clazz)) {
      return;
    }

    final var nullableFields = clazz
        .fields()
        .values()
        .stream()
        .filter(field -> field
            .annotations()
            .stream()
            .map(JAnnotationUse::getAnnotationClass)
            .map(JClass::fullName)
            .anyMatch(Nullable.class.getName()::equals))
        .map(JFieldVar::name)
        .collect(Collectors.toUnmodifiableSet());

    final var classList = classes(clazz)
        .toList();

    Stream
        .concat(classList
            .stream()
            .flatMap(classFromSet -> classFromSet
                .methods()
                .stream()),
            classList
                .stream()
                .flatMap(classFromSet -> stream(classFromSet.constructors())))
        .map(JMethod::params)
        .flatMap(List::stream)
        .filter(param -> nullableFields.contains(param.name()))
        .forEach(param -> param.annotate(Nullable.class));

    final var objectRef = clazz
        .owner()
        ._ref(Object.class);

    Optional
        .ofNullable(clazz.getMethod("equals", new JType[] {
            objectRef
        }))
        .map(JMethod::params)
        .map(SequencedCollection::getFirst)
        .ifPresent(parameter -> parameter.annotate(Nullable.class));
  }

  private static <T> Stream<T> stream(final Iterator<T> iterator) {
    return stream(IteratorUtils.asIterable(iterator));
  }

  private static <T> Stream<T> stream(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  private static Stream<JDefinedClass> classes(final JDefinedClass definedClass) {
    return Stream.concat(
        Stream.of(definedClass),
        stream(definedClass.classes())
            .flatMap(CheckerableAnnotator::classes));
  }
}
