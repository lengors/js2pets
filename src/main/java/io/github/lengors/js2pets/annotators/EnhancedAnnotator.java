package io.github.lengors.js2pets.annotators;

import org.jsonschema2pojo.Annotator;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

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
  default void constructor(JMethod constructor) {

  }

  /**
   * Listener to type generation finished for annotations.
   *
   * @param type The generated type.
   */
  default void type(JType type) {

  }
}
