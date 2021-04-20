package io.resys.hdes.docdb.spi.tag;

import java.util.Optional;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.actions.TagActions.TagQuery;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.spi.codec.TagCodec;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class AnyTagQuery implements TagQuery {
  private final DocDBClientState state;
  
  private String repoId;
  private String tagName;
  
  public AnyTagQuery(DocDBClientState state) {
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
  
    return state.getRepo(repoId).onItem()
      .transformToMulti(repo -> {
        if(repo == null) {
          return Multi.createFrom().empty();
        }
        final var ctx = this.state.getContext().toRepo(repo);
        return findTags(ctx, tagName);
      });
  }
  @Override
  public Uni<Optional<Tag>> get() {
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");
    RepoAssert.notEmpty(tagName, () -> "tagName can't be empty!");
    
    return state.getRepo(repoId).onItem()
      .transformToUni(repo -> {
        if(repo == null) {
          return Uni.createFrom().item(Optional.empty());
        }
        final var ctx = this.state.getContext().toRepo(repo);
        return findTags(ctx, tagName).collectItems()
            .first().onItem().transform(tag -> Optional.ofNullable(tag));
      });
  }
  @Override
  public Uni<Optional<Tag>> delete() {
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");
    RepoAssert.notEmpty(tagName, () -> "tagName can't be empty!");
    
    return state.getRepo(repoId).onItem()
      .transformToUni(repo -> {
        if(repo == null) {
          return Uni.createFrom().item(Optional.empty());
        }
        final var ctx = this.state.getContext().toRepo(repo);
        return findTags(ctx, tagName).collectItems()
          .first().onItem().transformToUni(tag -> {
            if(tag == null) {
              return Uni.createFrom().item(Optional.empty());
            }
            
            return this.state.getClient()
              .getDatabase(ctx.getDb())
              .getCollection(ctx.getRefs(), Tag.class)
              .deleteOne(Filters.eq(TagCodec.ID, tagName))
              .onItem()
              .transform(result -> {
                if(result.getDeletedCount() > 0) {
                  return Optional.of(tag);
                }
                return Optional.empty();
              });
          });
    });
  }
  
  private Multi<Tag> findTags(DocDBContext ctx, String tagName) {
    return this.state.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Tag.class)
        .find(Filters.eq(TagCodec.ID, tagName));
  }
}
