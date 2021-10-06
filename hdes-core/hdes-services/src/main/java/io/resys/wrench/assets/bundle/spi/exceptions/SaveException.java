package io.resys.wrench.assets.bundle.spi.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;

public class SaveException extends RuntimeException {
  private static final long serialVersionUID = 4107376210233372261L;

  private final List<AssetService> services = new ArrayList<>();
  private final CommitResult commit;
  
  public SaveException(AssetService entity, CommitResult commit) {
    super(msg(Arrays.asList(entity), commit));
    this.services.add(entity);
    this.commit = commit;
  }
  
  public List<AssetService> getEntity() {
    return services;
  }
  public CommitResult getCommit() {
    return commit;
  }
  
  private static String msg(List<AssetService> services, CommitResult commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit.getMessages()) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    return new StringBuilder("Can't save services: ")
        .append(services.get(0).getType())
        .append(", because of: ").append(messages)
        .toString();
  }
}
