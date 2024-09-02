package io.github.lengors.js2pets.annotators;

import org.jsonschema2pojo.Annotator;

import com.sun.codemodel.JMethod;

/**
 * Enhanced annotator that extends the base functionality of jsonschema2pojo's allowing respective implementations to
 * listen to constructor generation and annotate it.
 *
 * @author lengors
 */
public interface EnhancedAnnotator extends Annotator {

  /**
   * Listener to constructor generation for annotations.
   *
   * @param constructor The generated constructor.
   */
  void constructor(JMethod constructor);
}
