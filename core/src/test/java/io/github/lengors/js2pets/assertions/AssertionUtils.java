package io.github.lengors.js2pets.assertions;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;

import lombok.experimental.UtilityClass;

/**
 * Utility class for assertions with support for checkerframework.
 *
 * @author lengors
 */
@UtilityClass
public class AssertionUtils {

  /**
   * Asserts that the given value is not null.
   *
   * @param object The value to assert.
   */
  @EnsuresNonNull("#1")
  public void assertNotNull(final @Nullable Object object) {
    Assertions.assertNotNull(object);
  }
}
