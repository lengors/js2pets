package io.github.lengors.js2pets.annotators;

import org.jsonschema2pojo.Annotator;

import com.sun.codemodel.JMethod;

/**
 * Annotator for the js2pets plugin.
 */
public interface Js2petsAnnotator extends Annotator {

  /**
   * Annotate a constructor method.
   *
   * @param constructor the constructor method to annotate
   */
  void constructor(JMethod constructor);
}
