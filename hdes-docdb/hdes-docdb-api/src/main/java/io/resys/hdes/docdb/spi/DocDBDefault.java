package io.resys.hdes.docdb.spi;

import io.resys.hdes.docdb.api.DocDB;
import io.resys.hdes.docdb.api.actions.CheckoutActions;
import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.api.actions.DiffActions;
import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.TagActions;
import io.resys.hdes.docdb.spi.checkout.CheckoutActionsDefault;
import io.resys.hdes.docdb.spi.commits.CommitActionsDefault;
import io.resys.hdes.docdb.spi.diff.DiffActionsDefault;
import io.resys.hdes.docdb.spi.history.HistoryActionsDefault;
import io.resys.hdes.docdb.spi.objects.ObjectsActionsDefault;
import io.resys.hdes.docdb.spi.repo.RepoActionsDefault;
import io.resys.hdes.docdb.spi.tags.TagActionsDefault;

public class DocDBDefault implements DocDB {
  private final ClientState state;
  private RepoActions repoActions;
  private CommitActions commitActions;
  private TagActions tagActions;
  private CheckoutActions checkoutActions;
  private HistoryActions historyActions;
  private ObjectsActions objectsActions;
  private DiffActions diffActions;
  
  public DocDBDefault(ClientState state) {
    super();
    this.state = state;
  }
  
  @Override
  public RepoActions repo() {
    if(repoActions == null) {
      repoActions = new RepoActionsDefault(state); 
    }
    return repoActions;
  }
  @Override
  public CommitActions commit() {
    if(commitActions == null) {
      commitActions = new CommitActionsDefault(state, objects()); 
    }
    return commitActions;
  }
  @Override
  public TagActions tag() {
    if(tagActions == null) {
      tagActions = new TagActionsDefault(state); 
    }
    return tagActions;
  }
  @Override
  public CheckoutActions checkout() {
    if(checkoutActions == null) {
      checkoutActions = new CheckoutActionsDefault(state); 
    }
    return checkoutActions;
  }
  @Override
  public HistoryActions history() {
    if(historyActions == null) {
      historyActions = new HistoryActionsDefault(state); 
    }
    return historyActions;
  }

  @Override
  public ObjectsActions objects() {
    if(objectsActions == null) {
      objectsActions = new ObjectsActionsDefault(state); 
    }
    return objectsActions;
  }

  @Override
  public DiffActions diff() {
    if(diffActions == null) {
      diffActions = new DiffActionsDefault(state, objects()); 
    }
    return diffActions;
  }
}
