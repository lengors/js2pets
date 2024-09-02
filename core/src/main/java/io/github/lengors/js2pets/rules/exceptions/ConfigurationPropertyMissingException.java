package io.github.lengors.js2pets.rules.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a required configuration property is missing and its value cannot be inferred.
 *
 * This exception is a specific type of {@link NullPointerException} that includes the name of the missing configuration
 * property for easier debugging and context understanding.
 *
 * @author lengors
 */
public class ConfigurationPropertyMissingException extends NullPointerException {
  /**
   * The name of the configuration property that is missing.
   */
  @Getter
  private final String configurationPropertyName;

  /**
   * Constructs a new {@code ConfigurationPropertyMissingException} with the specified property name.
   *
   * @param configurationPropertyName The name of the missing configuration property.
   */
  public ConfigurationPropertyMissingException(final String configurationPropertyName) {
    super(String.format(
        "Configuration property {name=%s} missing and value could not be inferred from context",
        configurationPropertyName));
    this.configurationPropertyName = configurationPropertyName;
  }
}
