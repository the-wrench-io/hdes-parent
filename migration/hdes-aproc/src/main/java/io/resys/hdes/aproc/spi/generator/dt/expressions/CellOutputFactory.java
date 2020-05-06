package io.resys.hdes.aproc.spi.generator.dt.expressions;

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

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.exceptions.DataTypeExpressionException;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;

public interface CellOutputFactory {

  public static String create(Header header, Cell cell) {
    ValueType valueType = ValueType.valueOf(header.getValue());
    String src = cell.getValue();
    switch (valueType) {
    case STRING:
      return "\"" + cell.getValue() + "\"";
    case BOOLEAN:
      return cell.getValue();
    case INTEGER:
      return cell.getValue();
    case LONG:
      return cell.getValue() + "l";
    case DECIMAL:
      return "new BigDecimal(\"" + cell.getValue() + "\")";
    case DATE:
      return CellExpressionDate.parseLocalDate(cell.getValue()).toString();
    case DATE_TIME:
      return CellExpressionDate.parseLocalDateTime(cell.getValue()).toString();
    default:
      throw DataTypeExpressionException.builder().valueType(valueType).src(src).msg("Unknown value type").build();
    }
  }
}
