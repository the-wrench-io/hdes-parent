package io.resys.hdes.decisiontable.spi.exporters;

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

import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableService;

public class DecisionTableExporterException  extends HdesException {
  private static final long serialVersionUID = -9002485278903967975L;

  public DecisionTableExporterException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private DecisionTableService.ExportType type;
    private DecisionTableAst src;
    private Exception original;

    public Builder type(DecisionTableService.ExportType type) {
      this.type = type;
      return this;
    }
    public Builder dt(DecisionTableAst src) {
      this.src = src;
      return this;
    }
    public DecisionTableExporterException build() {
      String message = String.format("Exception in exporting dt: %s into format: %s ", src.getId(), type);

      if(original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }

      return new DecisionTableExporterException(message, original);
    }
  }
}
