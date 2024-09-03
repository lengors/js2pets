/**
 * The {@code io.github.lengors.js2pets.factories} package contains factory classes responsible for creating and
 * configuring rules and objects used in the JSON Schema to POJO conversion process. These factories extend and
 * customize the behavior of the base jsonschema2pojo library, offering enhanced functionality such as controlling the
 * inclusion of no-argument constructors in generated POJOs.
 * <br />
 * <br />
 * The package is designed to be used in conjunction with the jsonschema2pojo tool, allowing developers to generate Java
 * classes from JSON schemas with additional customizations.
 * <br />
 * <br />
 * Key classes include:
 * <ul>
 * <li>{@link EnhancedRuleFactory}: A custom {@link org.jsonschema2pojo.rules.RuleFactory} that provides additional
 * options for generating constructors.</li>
 * <li>{@link EnhancedRuleFactory.ExcludeNoArgsConstructor}: A factory configuration that excludes no-argument
 * constructors.</li>
 * <li>{@link EnhancedRuleFactory.IncludeNoArgsConstructor}: A factory configuration that includes no-argument
 * constructors.</li>
 * </ul>
 * This package is part of the js2pets project, which aims to extend the functionality of jsonschema2pojo for specific
 * use cases.
 *
 * @author lengors
 */
package io.github.lengors.js2pets.factories;
