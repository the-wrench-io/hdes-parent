//package io.resys.wrench.assets.bundle.spi.flow;
//
//import java.sql.Timestamp;
//
///*-
// * #%L
// * wrench-component-assets
// * %%
// * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import io.resys.hdes.client.api.ast.AstFlow;
//import io.resys.hdes.client.api.ast.TypeDef;
//import io.resys.hdes.client.api.ast.TypeDef.Direction;
//import io.resys.hdes.client.api.ast.TypeDef.ValueType;
//import io.resys.hdes.client.api.programs.FlowProgram;
//import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStep;
//import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStepRefType;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociationType;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
//import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
//import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceAssociation;
//import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceDataModel;
//import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceError;
//import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
//
//public class FlowServiceDataModelBuilder {
//
//  private final ServiceStore serviceStore;
//  private final List<ServiceError> errors = new ArrayList<>();
//
//  public FlowServiceDataModelBuilder(ServiceStore serviceStore) {
//    this.serviceStore = serviceStore;
//  }
//
//  public ServiceDataModel build(String id, AstFlow flowModel, Timestamp modified) {
//    List<TypeDef> params = new ArrayList<>(flowModel.getAcceptDefs());
//    Map<String, TypeDef> allParams = createModelParameterMap(flowModel, params);
//
//    List<ServiceError> errors = new ArrayList<>();
//    List<ServiceAssociation> assocs = new ArrayList<>();
//
//    for(FlowProgramStep taskModel : flowModel.getSteps().values()) {
//
//      ServiceType serviceType = getServiceType(taskModel);
//      if(serviceType != null) {
//        String taskServiceName = getTaskServiceName(taskModel);
//        if(StringUtils.isEmpty(taskServiceName)) {
//          errors.add(new ImmutableServiceError("flowTaskRefMissing", "Task: " + taskModel.getId() + ", is missing 'ref' value!"));
//          continue;
//        }
//
//        AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
//        if(service == null) {
//          errors.add(new ImmutableServiceError("flowTaskRefMissing", "Task: " + taskModel.getId() + ", refers to non existing " + serviceType + ": " + taskServiceName + "!"));
//          continue;
//        }
//
//        Map<String, TypeDef> taskInputs = getTaskServiceInput(taskModel, allParams, service);
//        for(TypeDef input : service.getDataModel().getParams()) {
//          if(input.getDirection() == Direction.OUT) {
//            continue;
//          }
//          if(taskInputs.containsKey(input.getName())) {
//            ValueType ref = taskInputs.get(input.getName()).getValueType();
//            if(input.getValueType() != ref) {
//              errors.add(new ImmutableServiceError("flowTaskParamMissing", "Task: " + taskModel.getId() + ", input: '" + input.getName() + "', type has wrong type, expecting:'" + input.getValueType() + "' but was: '" + ref + "'!"));
//            }
//            taskInputs.remove(input.getName());
//          } else {
//            errors.add(new ImmutableServiceError("flowTaskParamMissing", "Task: " + taskModel.getId() + ", is missing input: '" + input.getName() + "'!"));
//          }
//        }
//
//        for(TypeDef input : taskInputs.values()) {
//          errors.add(new ImmutableServiceError("flowTaskParamUnused", "Task: " + taskModel.getId() + ", has unused input: '" + input.getName() + "'!"));
//        }
//
//        if(service.getDataModel().getStatus() == ServiceStatus.ERROR) {
//          errors.add(new ImmutableServiceError("flowTaskInError", "Task: " + taskModel.getId() + ", refers to " + serviceType + ", that is in error state!"));
//        } else {
//          assocs.add(new ImmutableServiceAssociation(taskModel.getId(), taskServiceName, serviceType, isTaskServiceCollection(taskModel) ? ServiceAssociationType.ONE_TO_MANY : ServiceAssociationType.ONE_TO_ONE, Direction.OUT));
//        }
//      } else if(taskModel.getBody() != null && taskModel.getBody().getRefType() == FlowProgramStepRefType.DT) {
//        for(Map.Entry<String, String> input : taskModel.getBody().getInputMapping().entrySet()) {
//          if(!allParams.containsKey(input.getKey())) {
//            errors.add(new ImmutableServiceError("flowTaskExpressionParamMissing", "Task: " + taskModel.getId() + ", is missing input: '" + input.getKey() + "'!"));
//          }
//        }
//      }
//    }
//
//    return new ImmutableServiceDataModel(
//        id, ast.getName(), ast.getDescription(),
//        ServiceType.FLOW,
//        flowModel.getClass(),
//        errors.isEmpty() ? ServiceStatus.OK : ServiceStatus.ERROR,
//            Collections.unmodifiableList(errors),
//            Collections.unmodifiableList(params),
//            Collections.unmodifiableList(assocs));
//  }
//
//
//
//  protected Map<String, TypeDef> getTaskServiceInput(
//      FlowProgramStep taskModel, Map<String, TypeDef> allParams,
//      AssetService refService) {
//
//    Map<String, TypeDef> serviceTypes = refService.getDataModel().getParams().stream()
//    .filter(p -> p.getDirection() == Direction.IN)
//    .collect(Collectors.toMap(p -> p.getName(), p -> p));
//
//    Map<String, TypeDef> result = new HashMap<>();
//    for(Map.Entry<String, String> entry : taskModel.getBody().getInputMapping().entrySet()) {
//      if(!serviceTypes.containsKey(entry.getKey())) {
//        errors.add(new ImmutableServiceError("flowTaskParamUnknown", "Task: " + taskModel.getId() + ", has unknown input: '" + entry.getKey() + "'!"));
//      } else if(allParams.containsKey(entry.getValue())) {
//        result.put(entry.getKey(), allParams.get(entry.getValue()));
//      }
//    }
//    return result;
//  }
//
//
//  protected Map<String, TypeDef> createModelParameterMap(FlowProgram flowModel, List<TypeDef> params) {
//    Map<String, TypeDef> result = new HashMap<>();
//    params.forEach(p -> result.put(p.getName(), p));
//
//    for(FlowProgramStep taskModel : flowModel.getSteps().values()) {
//      String taskServiceName = getTaskServiceName(taskModel);
//      ServiceType serviceType = getServiceType(taskModel);
//      if(serviceType == null) {
//        continue;
//      }
//      if(StringUtils.isEmpty(taskServiceName)) {
//        errors.add(new ImmutableServiceError("flowTaskRefUndefined", "Task: " + taskModel.getId() + ", has no ref!"));
//        continue;
//      }
//      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
//      if(service == null) {
//        errors.add(new ImmutableServiceError("flowTaskRefUndefined", "Task: " + taskModel.getId() + ", ref: '" + taskServiceName + "' does not exist!"));
//        continue;
//      }
//      for(TypeDef param : service.getDataModel().getParams()) {
//        if(param.getDirection() == Direction.OUT) {
//          String name = taskModel.getId() + "." + param.getName();
//          Assert.isTrue(!result.containsKey(name), "Can't have duplicate param: " + name + "!");
//          result.put(name, param);
//        }
//      }
//    }
//
//    return Collections.unmodifiableMap(result);
//  }
//
//  protected ServiceQuery createQuery() {
//    return new GenericServiceQuery(serviceStore);
//  }
//
//  protected boolean isTaskServiceCollection(FlowProgramStep taskModel) {
//    return taskModel.getBody() != null ? taskModel.getBody().getCollection() : false;
//  }
//  protected String getTaskServiceName(FlowProgramStep taskModel) {
//    return taskModel.getBody() != null ? taskModel.getBody().getRef() : null;
//  }
//  protected ServiceType getServiceType(FlowProgramStep taskModel) {
//    if(taskModel.getBody() == null) {
//      return null;
//    }
//    switch(taskModel.getBody().getRefType()) {
//    case DT: return ServiceType.DT;
//    case SERVICE: return ServiceType.FLOW_TASK;
//    default: return null;
//    }
//  }
//
//}
