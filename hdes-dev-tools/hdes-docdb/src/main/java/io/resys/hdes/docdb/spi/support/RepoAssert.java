package io.resys.hdes.docdb.spi.support;

import java.util.function.Supplier;

import io.resys.hdes.docdb.api.exceptions.RepoException;

public class RepoAssert {
  public static void notEmpty(String object, Supplier<String> message) {
    if (object == null || object.isBlank()) {
      throw new RepoException(getMessage(message));
    }
  }
  public static void notNull(Object object, Supplier<String> message) {
    if (object == null) {
      throw new RepoException(getMessage(message));
    }
  }
  public static void isTrue(boolean expression, Supplier<String> message) {
    if (!expression) {
      throw new RepoException(getMessage(message));
    }
  }
  private static String getMessage(Supplier<String> supplier) {
    return (supplier != null ? supplier.get() : null);
  }

}
