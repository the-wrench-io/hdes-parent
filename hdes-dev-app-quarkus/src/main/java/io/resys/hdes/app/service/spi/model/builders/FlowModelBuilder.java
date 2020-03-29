package io.resys.hdes.app.service.spi.model.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.resys.hdes.app.service.api.ApplicationService.FlViewModel;
import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.SearchModel;
import io.resys.hdes.app.service.api.ImmutableFlViewModel;
import io.resys.hdes.app.service.api.ImmutableModel;
import io.resys.hdes.app.service.api.ImmutableSearchModel;
import io.resys.hdes.app.service.spi.model.ModelFactory.ModelBuilder;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowModel.Input;
import io.resys.hdes.flow.api.FlowModel.Task;
import io.resys.hdes.storage.api.Changes;

public class FlowModelBuilder implements ModelBuilder {
  private final ModelServices services;
  private Changes changes;

  public FlowModelBuilder(ModelServices services) {
    super();
    this.services = services;
  }

  @Override
  public ModelBuilder from(Changes changes) {
    this.changes = changes;
    return this;
  }

  @Override
  public Model build() {
    Assert.notNull(changes, () -> "changes must be defined!");
    FlowModel.Root flow = services.getFlowService().model().src(changes.getValues()).build();
    Collection<SearchModel> searchModel = createSearchModel(flow);
    FlViewModel viewModel = ImmutableFlViewModel.builder().type(changes.getLabel()).value(flow).build();
    return ImmutableModel.builder()
        .id(changes.getId())
        .type(changes.getLabel())
        .name(flow.getId().getValue())
        .search(searchModel)
        .view(viewModel)
        .build();
  }

  private Collection<SearchModel> createSearchModel(FlowModel.Root flow) {
    Collection<SearchModel> result = new ArrayList<>();
   
    if(flow.getId() != null) {
      result.add(ImmutableSearchModel.builder()
          .type("line: " + flow.getId().getSource().getLine())
          .value(flow.getId().getValue())
          .build());
    }
    
    if(flow.getInputs() != null) {
      for(Map.Entry<String, Input> input : flow.getInputs().entrySet()) {
        result.add(ImmutableSearchModel.builder()
            .type("line: " + input.getValue().getSource().getLine() + ", input")
            .value(input.getKey())
            .build());
      }
    }
    
    if(flow.getTasks() != null) {
      for(Map.Entry<String, Task> entry : flow.getTasks().entrySet()) {
        result.add(ImmutableSearchModel.builder()
            .type("line: " + entry.getValue().getSource().getLine() + ", task")
            .value(entry.getKey())
            .build());
        
        Task task = entry.getValue();

        if(task.getRef() != null) {
          String refValue = task.getRef().getRef().getValue();
          result.add(ImmutableSearchModel.builder()
              .type("line: " + entry.getValue().getSource().getLine() + ", task ref")
              .value(refValue)
              .build());
          
          for(Map.Entry<String, FlowModel> inputEntry : task.getRef().getInputs().entrySet()) {
            result.add(ImmutableSearchModel.builder()
                .type("line: " + inputEntry.getValue().getSource().getLine() + ", task inputs")
                .value(inputEntry.getValue().getValue())
                .build()); 
          }
        }
      }
    }

    return result;
  }
}
