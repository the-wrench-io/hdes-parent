package io.resys.hdes.app.service.spi.model.builders;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.app.service.api.ApplicationService.DtViewModel;
import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.SearchModel;
import io.resys.hdes.app.service.api.ImmutableDtViewModel;
import io.resys.hdes.app.service.api.ImmutableModel;
import io.resys.hdes.app.service.api.ImmutableSearchModel;
import io.resys.hdes.app.service.spi.model.ModelFactory.ModelBuilder;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.storage.api.Changes;

public class DecisionTableModelBuilder implements ModelBuilder {
  private final ModelServices services;
  private Changes changes;

  public DecisionTableModelBuilder(ModelServices services) {
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
    DecisionTableModel dt = services.getDecisionTableService().model().src(changes.getValues()).build();
    Collection<SearchModel> searchModel = createSearchModel(dt);
    DtViewModel viewModel = ImmutableDtViewModel.builder().type(changes.getLabel()).value(dt).build();
    
    return ImmutableModel.builder()
        .id(changes.getId())
        .type(changes.getLabel())
        .name(dt.getName())
        .search(searchModel)
        .view(viewModel)
        .build();
  }
  
  private Collection<SearchModel> createSearchModel(DecisionTableModel dt) {
    Collection<SearchModel> result = new ArrayList<>();

    result.add(ImmutableSearchModel.builder()
        .type("name")
        .value(dt.getName())
        .build());
    
    int index = 0;
    for(Header header : dt.getHeaders()) {
      result.add(ImmutableSearchModel.builder()
          .type("header: " + index)
          .value(header.getName() + " : " + header.getValue())
          .build());
      index++;
    }
    
    int rowIndex = 0;
    for(Row row : dt.getRows()) {
      
      int cellIndex = 0;
      for(Cell cell : row.getCells()) {
        if(StringUtils.isEmpty(cell.getValue())) {
          continue;
        }
        Header header  = dt.getHeaders().get(cellIndex);
        result.add(ImmutableSearchModel.builder()
            .type("row: " + rowIndex + " cell: " + cellIndex + " header: " + header.getName())
            .value(cell.getValue())
            .build());
        cellIndex++;
      }
      rowIndex++;
    }
    
    return result;
  }
}
