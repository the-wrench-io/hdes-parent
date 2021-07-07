package io.resys.wrench.assets.bundle.spi.exceptions;

/*-
 * #%L
 * wrench-assets-services
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

public enum AssetErrorCodes implements MessageCode {
  TASK_ALREADY_COMPLETED(500, "application.assets.taskAlreadyCompleted", "Task already completed, process id: {0}, task name: {2}, task form id: {3}!"),
  FLOW_START_ERROR(500, "application.assets.flowCanNotBeStarted", "Flow can not be started id: {0}, message: {1}!"),
  SERVICE_NAME_NOT_UNIQUE(422, "application.assets.nameNotUnique", "Service name must be unique, comparison is case insensitive, service pointer with same name: {0}, {1}!"),
  GIT_CONFLICT(409, "application.assets.gitConflict", "{0}"),
  FLOW_PROPERTY_REQUIRED(422, "application.assets.flow.propertyRequired", "Flow requires property: {0}!"),
  FLOW_PROPERTY_INVALID(422, "application.assets.flow.propertyInvalid", "Flow requires property: {0}!"),
  FLOW_EXEC_ERROR(422, "application.assets.flow.executionError", "{0}"),
  FLOW_TASK_ERROR(422, "application.assets.flow.taskError", "{0}"),
  FLOW_TASK_NAME_INVALID(422, "application.assets.flow.taskNameInvalid", "{0}");

  private final int status;
  private final String code;
  private final String value;

  private AssetErrorCodes(int status, String code, String value) {
    this.status = status;
    this.code = code;
    this.value = value;
  }

  @Override
  public Message newMessage(Object ... args) {
    return new Message(getCode(), getValue(), args);
  }

  @Override
  public DataException newException(Object ... args) {
    return new DataException(getStatus(), newMessage(args));
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getValue() {
    return value;
  }
}
