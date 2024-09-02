package io.github.lengors.js2pets.annotators;

import com.sun.codemodel.JMethod;

/**
 * Extension for jsonschema2pojo's annotator that allows to listen to
 * constructor generation and annotate it.
 *
 * @author lengors
 */
public interface Js2petsAnnotator extends org.jsonschema2pojo.Annotator {

  /**
   * Listener to constructor generation for annotations.
   *
   * @param constructor The generated constructor.
   */
  void constructor(JMethod constructor);
}
