package io.github.lengors.js2pets.assertions;

import java.util.List;

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

  /**
   * Asserts if the two lists match each other (contains the exact same elements).
   *
   * @param expected Expected list.
   * @param actual   Actual list.
   */
  public static void assertListMatches(final List<?> expected, final List<?> actual) {
    Assertions.assertEquals(expected.size(), actual.size());
    actual.forEach(actualElement -> {
      AssertionUtils.assertNotNull(actualElement);
      Assertions.assertTrue(expected.contains(actualElement));
    });
  }
}
