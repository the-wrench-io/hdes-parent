package io.resys.hdes.flow.spi.model.beans;

/*-
 * #%L
 * hdes-flow
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

public class KeywordAndValueBean {
  
  private final String keyword;
  private final String value;
  private final int indent;
  
  public KeywordAndValueBean(String keyword, String value, int indent) {
    super();
    this.keyword = keyword;
    this.value = value;
    this.indent = indent;
  }
  
  public String getKeyword() {
    return keyword;
  }
  public String getValue() {
    return value;
  }
  public int getIndent() {
    return indent;
  }
  
  public KeywordAndValueBean withValue(String value) {
    return new KeywordAndValueBean(keyword, value, indent);
  }
}
