package io.resys.hdes.storage.mongodb.tests;

/*-
 * #%L
 * hdes-storage-mongodb
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestAssert {
  
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  
  public static void assertEquals(String objectInTestPath, Object actual) {
    try {
      String expected = IOUtils.toString(TestAssert.class.getClassLoader().getResourceAsStream(objectInTestPath), StandardCharsets.UTF_8);
      String value = OBJECT_MAPPER.writeValueAsString(actual);
      System.out.println(value);
      JSONAssert.assertEquals(expected, value, false);
    } catch (IOException | JSONException e) {
      throw new RuntimeException(e.getMessage(), e);
    }    
  }
}
