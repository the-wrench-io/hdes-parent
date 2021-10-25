package io.resys.hdes.client.api.exceptions;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.StoreExceptionMsg;

public class StoreException extends RuntimeException {

  private static final long serialVersionUID = 7058468238867536222L;

  private final String code;
  private final Optional<StoreEntity> target;
  private final List<StoreExceptionMsg> messages = new ArrayList<>();
  
  public StoreException(Exception e, String code, StoreEntity target) {
    super(e.getMessage(), e);
    this.code = code;
    this.target = Optional.ofNullable(target);
  }

  public StoreException(Exception e, String code, StoreEntity target, StoreExceptionMsg ... msg) {
    super(e.getMessage(), e);
    this.code = code;
    this.target = Optional.ofNullable(target);
    this.messages.addAll(Arrays.asList(msg));
  }
    
  public StoreException(String code, StoreEntity target) {
    super();
    this.code = code;
    this.target = Optional.ofNullable(target);
  }

  public StoreException(String code, StoreEntity target, StoreExceptionMsg ... msg) {
    super();
    this.code = code;
    this.target = Optional.ofNullable(target);
    this.messages.addAll(Arrays.asList(msg));
  }
  
  
  public String getCode() {
    return code;
  }
  public Optional<StoreEntity> getTarget() {
    return target;
  }
  public List<StoreExceptionMsg> getMessages() {
    return messages;
  }
}
