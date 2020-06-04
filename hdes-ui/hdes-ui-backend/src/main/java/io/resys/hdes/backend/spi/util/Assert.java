package io.resys.hdes.backend.spi.util;

import java.util.function.Supplier;



public class Assert {
  
  public static void notNull(Object value, Supplier<String> msg) {
    if(value == null) {
      throw new HdesUIBackendApiExeption(msg.get());
    }
  }
  
  public static void notEmpty(String value, Supplier<String> msg) {
    if(value == null || value.isBlank()) {
      throw new HdesUIBackendApiExeption(msg.get());
    }
  }
  
  public static class HdesUIBackendApiExeption extends RuntimeException {
    private static final long serialVersionUID = 3801387126866326646L;
    
    public HdesUIBackendApiExeption(String msg) {
      super(msg);
    }
  }
}
