package io.resys.hdes.app;

import java.io.File;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.DefaultBean;
import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.spi.GenericApplicationService;
import io.resys.hdes.app.service.spi.model.GenericModelFactory;
import io.resys.hdes.app.service.spi.model.ModelCache;
import io.resys.hdes.app.service.spi.model.ModelFactory;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.app.service.spi.state.ApplicationState;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.GenericDataTypeService;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.spi.GenericDecisionTableService;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.spi.GenericFlowService;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.spi.GenericServiceTaskService;
import io.resys.hdes.storage.spi.folder.StorageServiceFolder;

@Dependent
public class ApplicationConfiguration {
  
  @Inject
  @ConfigProperty(name = "storage-service.folder.path")
  String source;
  
  @Produces
  @Singleton
  @DefaultBean
  public ApplicationService applicationService() {
    
    
    DataTypeService dataTypeService = GenericDataTypeService.config().build();
    DecisionTableService decisionTableRepository = GenericDecisionTableService.config().dataType(dataTypeService).build();
    FlowService flowService = GenericFlowService.config().dataType(dataTypeService).build();
    ServiceTaskService serviceTaskService = GenericServiceTaskService.config().dataType(dataTypeService).build();
    
    StorageServiceFolder storage = StorageServiceFolder.config().source(new File(source)).build();
    
    ModelServices services = new ModelServices(dataTypeService, decisionTableRepository, flowService, serviceTaskService, storage);
    ModelFactory modelFactory = new GenericModelFactory(services);
    ModelCache cache = new ModelCache();
    ApplicationState state = new ApplicationState(services, cache, modelFactory).refresh();
    
    return new GenericApplicationService(state);
  }
}
