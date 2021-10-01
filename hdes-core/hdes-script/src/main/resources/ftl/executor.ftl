<#--
 #%L
 wrench-component-bind
 %%
 Copyright (C) 2016 Copyright 2016 ReSys OÜ
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

<#foreach import in imports>
import ${import};
</#foreach>

class ${name} extends ScriptTemplate {

  private final ${beanSimpleName} bean;

  public ${name}(${beanSimpleName} bean, ServiceAstType model) {
    super(model);
    this.bean = bean;
  }

  @Override  
  public Object execute(List<Object> facts) {
    ScriptMethodModel method = getMethod(facts);
    if(method == null) {
      return null;
    }
<#foreach executorArg in inputs>
    ${executorArg.getType().getBeanType().getSimpleName()} ${executorArg.getType().getName()} = (${executorArg.getType().getBeanType().getSimpleName()}) getArgument(${executorArg.getType().getBeanType().getCanonicalName()}.class, facts);
</#foreach>
<#if executorMethod.isReturnType()>
    Object result =
<#else>
    Object result = null;
</#if>bean.${executorMethod.getName()}(<#list inputs as executorArg>${executorArg.getType().getName()}<#sep>, </#list>);
    
    return result;     
  }
}
