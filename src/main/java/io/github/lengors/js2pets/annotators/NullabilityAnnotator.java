package io.github.lengors.js2pets.annotators;

import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.GenerationConfig;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.codemodel.CodeModelUtils;

/**
 * An annotator that makes the generated classes emitted with proper nullable and non-nullable annotations.
 *
 * @author lengors
 */
public class NullabilityAnnotator extends AbstractAnnotator implements EnhancedAnnotator {
  /**
   * Getter method prefix.
   */
  private static final String GETTER_PREFIX = "get";

  /**
   * Instantiates annotator with generation configuration.
   *
   * @param generationConfig The injected generation configuration.
   */
  public NullabilityAnnotator(final GenerationConfig generationConfig) {
    super(generationConfig);
  }

  /**
   * Annotator callback that annotates equals methods' parameter with proper nullable types as well as any properties,
   * return types and parameters for constructors and setters for the respective fields.
   *
   * @param type The generated class to apply the annotation to.
   */
  @Override
  public void type(final JType type) {
    EnhancedAnnotator.super.type(type);

    if (!(type instanceof JDefinedClass clazz)) {
      return;
    }

    final var nullableAnnotations = AnnotationUtils.getNullableAnnotations(getGenerationConfig());
    final var nonNullableAnnotations = AnnotationUtils.getNonNullableAnnotations(getGenerationConfig());

    final var classStructure = CodeModelUtils.listClassStructure(clazz);
    final var invokables = CodeModelUtils.listInvokables(classStructure);

    final var fieldsByNullabilityType = clazz
        .fields()
        .values()
        .stream()
        .collect(Collectors.groupingBy(field -> AnnotationUtils.NON_NULLABLE_ANNOTATIONS
            .stream()
            .anyMatch(annotation -> CodeModelUtils.containsAnnotation(field, annotation))
                ? NullabilityType.NON_NULLABLE
                : NullabilityType.NULLABLE));

    for (final var nullabilityType : NullabilityType.values()) {
      final var annotations = switch (nullabilityType) {
        case NullabilityType.NON_NULLABLE -> nonNullableAnnotations;
        case NullabilityType.NULLABLE -> nullableAnnotations;
      };
      final var fields = fieldsByNullabilityType.getOrDefault(nullabilityType, Collections.emptyList());
      final var fieldNames = fields
          .stream()
          .map(field -> {
            CodeModelUtils.safeAnnotate(field, annotations);
            return field;
          })
          .map(JFieldVar::name)
          .collect(Collectors.toUnmodifiableSet());

      CodeModelUtils.annotateInvokablesParameters(invokables.stream(), fieldNames, annotations);

      final var capitalizedFieldNames = fieldNames
          .stream()
          .map(StringUtils::capitalize)
          .collect(Collectors.toUnmodifiableSet());
      invokables
          .stream()
          .filter(method -> {
            if (!method.params().isEmpty()) {
              return false;
            }

            final var methodName = method.name();
            if (!methodName.startsWith(GETTER_PREFIX)) {
              return false;
            }

            return capitalizedFieldNames.contains(StringUtils.capitalize(methodName.replaceFirst(GETTER_PREFIX, "")));
          })
          .forEach(method -> CodeModelUtils.safeAnnotate(method, annotations));
    }

    CodeModelUtils.annotateEqualsMethod(clazz);
  }
}
