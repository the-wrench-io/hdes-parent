package io.resys.hdes.docdb.spi.tags;

import java.util.Optional;

import io.resys.hdes.docdb.api.actions.TagActions.TagQuery;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.spi.ClientState;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class AnyTagQuery implements TagQuery {
  private final ClientState state;
  
  private String repoId;
  private String tagName;
  
  public AnyTagQuery(ClientState state) {
    super();
    this.state = state;
  }
  @Override
  public TagQuery tagName(String tagName) {
    this.tagName = tagName;
    return this;
  }
  @Override
  public TagQuery repo(String repoId) {
    this.repoId = repoId;
    return this;
  }
  
  @Override
  public Multi<Tag> find() {
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");

    return state.query(repoId)
        .onItem().transformToMulti(f -> f.tags().name(tagName).find());
  }
  @Override
  public Uni<Optional<Tag>> get() {
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");
    RepoAssert.notEmpty(tagName, () -> "tagName can't be empty!");
    
    return state.query(repoId)
        .onItem().transformToUni(f -> f.tags().name(tagName).get())
        .onItem().transform(tag -> Optional.ofNullable(tag));
  }
  @Override
  public Uni<Optional<Tag>> delete() {
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");
    RepoAssert.notEmpty(tagName, () -> "tagName can't be empty!");
    
    return state.query(repoId)
    .onItem().transformToUni(query -> 
      query.tags().name(tagName).get().onItem().transformToUni(tag -> {
        if(tag == null) {
          return Uni.createFrom().item(Optional.empty());
        }
        return query.tags().delete().onItem().transform(delete -> {
          if(delete.getDeletedCount() > 0) {
            return Optional.of(tag);
          }
          return Optional.empty();
        });
      })
      
    );
  }
}
