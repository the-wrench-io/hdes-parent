package io.resys.wrench.assets.bundle.spi.exceptions;

import java.util.Optional;

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


public class DataException extends RuntimeException {

  private static final long serialVersionUID = -69566802600380919L;

  private final MessageList messagesList;

  public DataException(int status, Exception e) {
    super(e.getMessage(), e);
    this.messagesList = new MessageList().setStatus(status);
  }
  public DataException(Exception e) {
    super(e.getMessage(), e);
    this.messagesList = new MessageList();
  }

  public DataException(int status, Message message) {
    super(message.toString());
    this.messagesList = new MessageList().setStatus(status).add(message);
  }

  public DataException(MessageList messagesList) {
    super(messagesList.toString());
    this.messagesList = messagesList;
  }

  public MessageList getMessagesList() {
    return messagesList;
  }
  
  public Optional<Message> getError(String code) {
    return this.getMessagesList().get().stream().filter(e -> e.getCode().equals(code)).findFirst();
  }
}
