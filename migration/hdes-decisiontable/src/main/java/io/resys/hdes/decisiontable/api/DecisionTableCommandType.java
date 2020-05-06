package io.resys.hdes.decisiontable.api;

/*-
 * #%L
 * hdes-decisiontable
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

public enum DecisionTableCommandType  {

    @Deprecated
    SET_HEADER_NAME,
    @Deprecated
    SET_HEADER_SCRIPT,
    
    SET_NAME,
    SET_DESCRIPTION,
    IMPORT_CSV,
    MOVE_ROW,
    MOVE_HEADER,
    SET_HEADER_TYPE,
    SET_HEADER_REF,
    SET_HEADER_EXTERNAL_REF,
    SET_HEADER_DIRECTION,
    SET_HEADER_EXPRESSION,
    SET_HIT_POLICY,
    SET_CELL_VALUE,
    DELETE_CELL,
    DELETE_HEADER,
    DELETE_ROW,
    DELETE_HEADER_CONSTRAINT,
    ADD_LOG,
    ADD_HEADER_IN,
    ADD_HEADER_OUT,
    ADD_HEADER_CONSTRAINT,
    ADD_ROW;
}
