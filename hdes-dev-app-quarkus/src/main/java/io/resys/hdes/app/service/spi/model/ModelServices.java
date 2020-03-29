package io.resys.hdes.app.service.spi.model;

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.storage.api.StorageService;

public class ModelServices {
  
  private final DataTypeService dataTypeService;
  private final DecisionTableService decisionTableService;
  private final FlowService flowService;
  private final ServiceTaskService serviceTaskService;
  private final StorageService storageService;
  
  public ModelServices(
      DataTypeService dataTypeService, 
      DecisionTableService decisionTableService, 
      FlowService flowService, 
      ServiceTaskService serviceTaskService, 
      StorageService storageService) {
    super();
    this.dataTypeService = dataTypeService;
    this.decisionTableService = decisionTableService;
    this.flowService = flowService;
    this.serviceTaskService = serviceTaskService;
    this.storageService = storageService;
  }
  
  public DataTypeService getDataTypeService() {
    return dataTypeService;
  }
  public DecisionTableService getDecisionTableService() {
    return decisionTableService;
  }
  public FlowService getFlowService() {
    return flowService;
  }
  public ServiceTaskService getServiceTaskService() {
    return serviceTaskService;
  }
  public StorageService getStorageService() {
    return storageService;
  }
}
