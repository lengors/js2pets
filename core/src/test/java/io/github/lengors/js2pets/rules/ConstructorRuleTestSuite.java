package io.github.lengors.js2pets.rules;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;

interface ConstructorRuleTestSuite {
  Schema getCurrentSchema();

  RuleFactory getRuleFactory();

  JsonNode getNode();

  Rule<JDefinedClass, JDefinedClass> getSuperConstructorRule();
}
