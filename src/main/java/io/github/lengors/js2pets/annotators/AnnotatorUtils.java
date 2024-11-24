package io.github.lengors.js2pets.annotators;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.CompositeAnnotator;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * Annotator utility class to be used as an extension method by client classes.
 *
 * @author lengors
 */
public final class AnnotatorUtils {
  private AnnotatorUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Invokes the constructor callback if applicable to the annotator.
   *
   * @param annotator   The annotator to notify.
   * @param constructor The generated constructor to notify of.
   */
  public static void constructor(final Annotator annotator, final JMethod constructor) {
    dispatch(annotator, constructor, EnhancedAnnotator::constructor);
  }

  private static <T> void dispatch(
      final Annotator annotator,
      final T value,
      final BiConsumer<EnhancedAnnotator, T> action) {
    streamAnnotators(annotator)
        .filter(EnhancedAnnotator.class::isInstance)
        .forEach(leafAnnotator -> action.accept((EnhancedAnnotator) leafAnnotator, value));
  }

  /**
   * Returns a flat stream of annotators by traversing the given annotator if it's a composite annotator.
   *
   * @param annotator The source annotator.
   * @return The stream of annotators obtained.
   */
  public static Stream<Annotator> streamAnnotators(final Annotator annotator) {
    if (annotator instanceof CompositeAnnotator compositeAnnotator) {
      final var annotatorsField = FieldUtils.getField(annotator.getClass(), "annotators", true);
      final Annotator[] annotators;
      try {
        annotators = (Annotator[]) annotatorsField.get(compositeAnnotator);
      } catch (final IllegalAccessException exception) {
        return Stream.empty();
      }
      if (annotators == null) {
        return Stream.empty();
      }
      return Stream
          .of(annotators)
          .flatMap(AnnotatorUtils::streamAnnotators);
    } else {
      return Stream.of(annotator);
    }
  }

  /**
   * Invokes the type callback if applicable to the annotator.
   *
   * @param annotator The annotator to notify.
   * @param type      The generated type to notify of.
   */
  public static void type(final Annotator annotator, final JType type) {
    dispatch(annotator, type, EnhancedAnnotator::type);
  }
}
