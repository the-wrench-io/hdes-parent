package io.resys.hdes.docdb.spi;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.DocDB;
import io.resys.hdes.docdb.api.actions.CheckoutActions;
import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.TagActions;
import io.resys.hdes.docdb.spi.checkout.CheckoutActionsDefault;
import io.resys.hdes.docdb.spi.commit.CommitActionsDefault;
import io.resys.hdes.docdb.spi.history.HistoryActionsDefault;
import io.resys.hdes.docdb.spi.objects.ObjectsActionsDefault;
import io.resys.hdes.docdb.spi.repo.RepoActionsDefault;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.state.ImmutableDocDBClientState;
import io.resys.hdes.docdb.spi.tag.TagActionsDefault;

public class DocDBMongoClient implements DocDB {
  private final DocDBClientState state;
  private RepoActions repoActions;
  private CommitActions commitActions;
  private TagActions tagActions;
  private CheckoutActions checkoutActions;
  private HistoryActions historyActions;
  private ObjectsActions objectsActions;
  
  public DocDBMongoClient(ReactiveMongoClient client, DocDBContext ctx) {
    super();
    this.state = ImmutableDocDBClientState.builder().context(ctx).client(client).build();
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
}
