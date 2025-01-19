package io.github.lengors.js2pets.annotators;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.NoopAnnotator;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.codemodel.CodeModelUtils;

/**
 * An annotator that makes the generated classes compatible with the Checkerframework.
 *
 * @author lengors
 * @deprecated Use {@link NullabilityAnnotator} instead with an instance of
 *             {@link io.github.lengors.js2pets.factories.EnhancedRuleFactory}.
 */
@Deprecated(since = "1.2.0", forRemoval = true)
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
        .filter(field -> CodeModelUtils.containsAnnotation(field, Nullable.class))
        .map(JFieldVar::name)
        .collect(Collectors.toUnmodifiableSet());

    final var classStructure = CodeModelUtils.listClassStructure(clazz);
    CodeModelUtils.annotateInvokablesParameters(
        CodeModelUtils.streamInvokables(classStructure),
        nullableFields,
        AnnotationUtils.CHECKERFRAMEWORK_NULLABLE_ANNOTATION,
        false);
    CodeModelUtils.annotateEqualsMethod(clazz);
  }
}
