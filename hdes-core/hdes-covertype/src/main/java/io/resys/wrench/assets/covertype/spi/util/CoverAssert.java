package io.resys.wrench.assets.covertype.spi.util;

import java.util.function.Supplier;

public class CoverAssert {
  
  public static void notNull(Object object, Supplier<String> message) {
    if (object == null) {
      throw new IllegalArgumentException(getMessage(message));
    }
  }
  public static void isTrue(boolean expression, Supplier<String> message) {
    if (!expression) {
      throw new IllegalArgumentException(getMessage(message));
    }
  }
  private static String getMessage(Supplier<String> supplier) {
    return (supplier != null ? supplier.get() : null);
  }

}
