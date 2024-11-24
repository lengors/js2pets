package io.github.lengors.js2pets.annotators;

import java.util.Comparator;

import org.jsonschema2pojo.Annotator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

import io.github.lengors.js2pets.codemodel.CodeModelUtils;
import io.github.lengors.js2pets.streams.StreamUtils;

/**
 * Enhanced annotator that extends the base functionality of jsonschema2pojo's allowing respective implementations to
 * listen to constructor generation or type generation and annotate them.
 *
 * @author lengors
 */
public interface EnhancedAnnotator extends Annotator {

  /**
   * Listener to constructor generation for annotations.
   *
   * @param constructor The generated constructor.
   */
  default void constructor(final JMethod constructor) {

  }

  /**
   * Listener to type generation finished for annotations.
   *
   * @param type The generated type.
   */
  default void type(final JType type) {
    if (!(type instanceof JDefinedClass clazz)) {
      return;
    }

    StreamUtils
        .stream(clazz.constructors())
        .max(Comparator.comparing(constructor -> constructor
            .params()
            .size()))
        .ifPresent(constructor -> {
          constructor.annotate(JsonCreator.class);
          for (final var parameter : constructor.params()) {

            final var field = clazz
                .fields()
                .get(parameter.name());
            if (field == null) {
              continue;
            }

            final var propertyName = CodeModelUtils.getJsonPropertyValue(field);
            if (propertyName != null) {
              parameter
                  .annotate(JsonProperty.class)
                  .param("value", propertyName);
            }
          }
        });
  }
}
