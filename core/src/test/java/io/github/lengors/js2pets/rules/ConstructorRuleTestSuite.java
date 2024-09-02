package io.github.lengors.js2pets.rules;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;

/**
 * A test suite interface for providing necessary components to test the {@link ConstructorRule}. Implementations of
 * this interface should supply mock or real instances of the required schema, rule factory, JSON nodes, and super
 * constructor rules. These components are used by the {@link ConstructorRuleTestRunner} to perform unit tests.
 *
 * @author lengors
 */
public interface ConstructorRuleTestSuite {
  /**
   * Retrieves the current schema being tested.
   *
   * @return The schema instance used in the current test context.
   */
  Schema getCurrentSchema();

  /**
   * Retrieves the rule factory used to create rules for schema to Java conversion.
   *
   * @return The rule factory instance used in the test.
   */
  RuleFactory getRuleFactory();

  /**
   * Retrieves the JSON node representing the part of the schema being tested.
   *
   * @return The JSON node being tested.
   */
  JsonNode getNode();

  /**
   * Retrieves the rule for generating the super constructor in a defined class.
   *
   * @return The rule used to generate a super constructor in the test.
   */
  Rule<JDefinedClass, JDefinedClass> getSuperConstructorRule();
}
