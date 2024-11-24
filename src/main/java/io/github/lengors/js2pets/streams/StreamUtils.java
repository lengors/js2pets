package io.github.lengors.js2pets.streams;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IteratorUtils;

/**
 * Utilities for {@link Stream}.
 *
 * @author lengors
 */
public final class StreamUtils {
  private StreamUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates {@link Stream} for the given {@link Iterator}.
   *
   * @param <T>      The generic type of the {@link Iterator}.
   * @param iterator The {@link Iterator}.
   * @return The {@link Stream}.
   */
  public static <T> Stream<T> stream(final Iterator<T> iterator) {
    return fromIterable(IteratorUtils.asIterable(iterator));
  }

  /**
   * Creates {@link Stream} for the given {@link Iterable}.
   *
   * @param <T>      The generic type of the {@link Iterable}.
   * @param iterable The {@link Iterable}.
   * @return The {@link Stream}.
   */
  public static <T> Stream<T> fromIterable(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }
}
