package io.resys.hdes.backend.spi.util;

/*-
 * #%L
 * hdes-ui-backend
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
