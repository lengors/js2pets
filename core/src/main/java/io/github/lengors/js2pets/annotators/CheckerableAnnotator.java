package io.github.lengors.js2pets.annotators;

import java.util.Optional;
import java.util.SequencedCollection;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsonschema2pojo.NoopAnnotator;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * An annotator that makes the generated classes compatible with the Checkerframework.
 *
 * @author lengors
 */
public class CheckerableAnnotator extends NoopAnnotator implements EnhancedAnnotator {

  /**
   * Annotator callback that annotates equals methods' parameter with Checkerframework's {@link Nullable}.
   *
   * @param type The generated class to apply the annotation to.
   */
  @Override
  public void type(final JType type) {
    if (!(type instanceof JDefinedClass clazz)) {
      return;
    }

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
}
