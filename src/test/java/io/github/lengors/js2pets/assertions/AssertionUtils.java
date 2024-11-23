package io.github.lengors.js2pets.assertions;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;

/**
 * Utility class for assertions with support for Checkerframework.
 *
 * @author lengors
 */
public final class AssertionUtils {
  private AssertionUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Asserts that the given value is not null.
   *
   * @param object The value to assert.
   */
  @EnsuresNonNull("#1")
  public static void assertNotNull(final @Nullable Object object) {
    Assertions.assertNotNull(object);
  }
}
