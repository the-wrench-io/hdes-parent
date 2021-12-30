package io.resys.wrench.assets.bundle.spi.exceptions;

/*-
 * #%L
 * wrench-component-messages
 * %%
 * Copyright (C) 2016 Copyright 2016 ReSys OÜ
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessageList implements Serializable {

  private static final long serialVersionUID = -3743717516899480503L;

  private int status;
  private final List<Message> messages = new ArrayList<>();

  public int getStatus() {
    return status;
  }
  public MessageList setStatus(int status) {
    if(this.status != 0) {
      return this;
    }
    this.status = status;
    return this;
  }

  public MessageList add(Message message) {
    this.messages.add(message);
    return this;
  }
  public MessageList addAll(Collection<Message> messages) {
    this.messages.addAll(messages);
    return this;
  }
  public MessageList merge(MessageList source) {
    this.messages.addAll(source.get());

    if(source.getStatus() != 0) {
      this.status = source.getStatus();
    }
    return this;
  }


  public List<Message> get() {
    return Collections.unmodifiableList(messages);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("status=" + status + System.lineSeparator());

    for(Message message : this.messages) {
      result.append(message).append(System.lineSeparator());
    }

    return result.toString();
  }
}
