package io.resys.hdes.aproc.spi.tag;

/*-
 * #%L
 * hdes-aproc
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.storage.api.Changes;

public class TagFactory {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private final Map<String, TaggedChanges> result = new HashMap<>();

    public void add(Changes changes) {
      this.add("master", changes);
    }
    
    public void add(String tag, Changes changes) {
      final TaggedChanges changesByTag;
      if(result.containsKey(tag)) {
        changesByTag = result.get(tag);
      } else {
        changesByTag = new TaggedChanges("tag" + (result.size() + 1), tag);
        result.put(tag, changesByTag);
      }
      
      changesByTag.values.add(changes);
    }
    
    public List<TaggedChanges> build() {
      return Collections.unmodifiableList(new ArrayList<>(result.values()));
    }
  }
  
  public static class TaggedChanges {
    private final String tagId;
    private final String tagName;
    private final List<Changes> values = new ArrayList<>();
    
    public TaggedChanges(String tagId, String tagName) {
      super();
      this.tagName = tagName;
      this.tagId = tagId;
    }
    public String getTagId() {
      return tagId;
    }
    public List<Changes> getValues() {
      return values;
    }
    public String getTagName() {
      return tagName;
    }
  }
}
