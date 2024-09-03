package io.github.lengors.js2pets.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.MockedConstruction;
import org.mockito.Mockito;

final class EnhancedMockedConstruction<T> implements MockedConstruction<T> {

  /**
   * Mocked construction for delegation.
   */
  private final MockedConstruction<T> delegatedMockedConstruction;

  /**
   * Arguments per mocked object instantiated with mocked construction.
   */
  private final Map<T, List<?>> arguments = new HashMap<>();

  EnhancedMockedConstruction(final Class<T> classToMock) {
    delegatedMockedConstruction = Mockito.mockConstruction(
        classToMock,
        (mock, context) -> arguments.put(mock, context.arguments()));
  }

  @Override
  public void close() {
    delegatedMockedConstruction.close();
  }

  @Override
  public void closeOnDemand() {
    delegatedMockedConstruction.closeOnDemand();
  }

  @Override
  public List<T> constructed() {
    return delegatedMockedConstruction.constructed();
  }

  List<?> getArguments(final Object mock) {
    return arguments.getOrDefault(mock, Collections.emptyList());
  }

  @Override
  public boolean isClosed() {
    return delegatedMockedConstruction.isClosed();
  }
}
