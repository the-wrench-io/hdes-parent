package io.resys.hdes.docdb.spi.support;

import java.util.function.Supplier;

import io.resys.hdes.docdb.api.exceptions.RepoException;

public class RepoAssert {
  private static final String NAME_PATTER = "^([a-zA-Z0-9 +_/-]|\\\\\\\\)+";
  
  public static void isName(String value, Supplier<String> message) {
    RepoAssert.isTrue(value.matches(NAME_PATTER), () -> message.get() + " => Valid name pattern: '" + NAME_PATTER + "'!");
  }
  
  public static void notEmpty(String object, Supplier<String> message) {
    if (object == null || object.isBlank()) {
      throw new RepoException(getMessage(message));
    }
  }
  public static void isEmpty(String object, Supplier<String> message) {
    if (object == null || object.isBlank()) {
      return;
    }
    throw new RepoException(getMessage(message));
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
