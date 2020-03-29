<#--
 #%L
 hdes-servicetask
 %%
 Copyright (C) 2020 Copyright 2020 ReSys OÜ
 %%
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 #L%
-->
package ${packageName};

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.ImmutableDataType;
import io.resys.hdes.servicetask.api.ServiceTask;
import io.resys.hdes.servicetask.api.ServiceTask.Input;
import io.resys.hdes.servicetask.api.ServiceTask.Output;
<#foreach import in imports>
import ${import};
</#foreach>

${src.getImports()}


class ${name} implements ServiceTask<InputBean, OutputBean, ${contextType}> {

  private final static Class<InputBean> _inputType = InputBean.class;
  private final static Class<OutputBean> _outputType = OutputBean.class;

  static {
${src.getStaticBody()}
  }

  @Override
  public Class<InputBean> getInputType() {
    return _inputType;
  }
  
  @Override
  public Class<OutputBean> getOutputType() {
    return _outputType;
  }
  
  @Override
  public OutputBean execute(InputBean input, ${contextType} context) {
    OutputBean output = new OutputBean();
${src.getExecuteBody()}
    return output;     
  }

  public static class InputBean implements Input {
${src.getInputBody()}
  }
  
  public static class OutputBean implements Output {
${src.getOutputBody()}
  }
}
