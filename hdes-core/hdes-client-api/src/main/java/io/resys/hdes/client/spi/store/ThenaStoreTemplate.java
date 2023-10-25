package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ImmutableStoreExceptionMsg;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.exceptions.StoreException;
import io.resys.hdes.client.spi.store.ThenaConfig.EntityState;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.thena.docdb.api.actions.RepoActions.RepoStatus;
import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ThenaStoreTemplate extends PersistenceCommands implements HdesStore {
  public ThenaStoreTemplate(ThenaConfig config) {
    super(config);
  }
  
  protected abstract HdesStore createWithNewConfig(ThenaConfig config);
  
  @Override
  public String getRepoName() {
    return config.getRepoName();
  }
  @Override
  public String getHeadName() {
    return config.getHeadName();
  }
  @Override
  public StoreRepoBuilder repo() {
    return new StoreRepoBuilder() {
      private String repoName;
      private String headName;
      @Override
      public StoreRepoBuilder repoName(String repoName) {
        this.repoName = repoName;
        return this;
      }
      @Override
      public StoreRepoBuilder headName(String headName) {
        this.headName = headName;
        return this;
      }
      @Override
      public Uni<HdesStore> create() {
        HdesAssert.notNull(repoName, () -> "repoName must be defined!");
        final var client = config.getClient();
        final var newRepo = client.repo().create().name(repoName).build();
        return newRepo.onItem().transform((repoResult) -> {
          if(repoResult.getStatus() != RepoStatus.OK) {
            throw new StoreException("REPO_CREATE_FAIL", null, 
                ImmutableStoreExceptionMsg.builder()
                .id(repoResult.getStatus().toString())
                .value(repoName)
                .addAllArgs(repoResult.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
                .build()); 
          }
          
          return build();
        });
      }
      @Override
      public HdesStore build() {
        HdesAssert.notNull(repoName, () -> "repoName must be defined!");
        return createWithNewConfig(ImmutableThenaConfig.builder()
            .from(config)
            .repoName(repoName)
            .headName(headName == null ? config.getHeadName() : headName)
            .build());
      }
      @Override
      public Uni<Boolean> createIfNot() {
        final var client = config.getClient();
        
        return client.repo().query().id(config.getRepoName()).get().onItem().transformToUni(repo -> {
          if(repo == null) {
            return client.repo().create().name(config.getRepoName()).build().onItem().transform(newRepo -> true); 
          }
          return Uni.createFrom().item(false);
        });
      }
    };
  }
  
  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }
  @Override
  public Uni<StoreEntity> create(CreateStoreEntity newType) {
    final var gid = newType.getId() == null ? gid(newType.getBodyType()) : newType.getId();
    
    final var entity = (StoreEntity) ImmutableStoreEntity.builder()
        .id(gid)
        .hash("")
        .body(newType.getBody())
        .bodyType(newType.getBodyType())
        .build();
    
    if(newType.getId() == null) {
      return super.save(entity);  
    }
    
    return get().onItem().transformToUni(currentState -> {
      cantHaveEntityWithId(newType.getId(), currentState);  
      return super.save(entity);  
    });
  }

  @Override
  public Uni<StoreEntity> update(UpdateStoreEntity updateType) {
    final Uni<EntityState> query = getEntityState(updateType.getId());
    return query.onItem().transformToUni(state -> {
      final StoreEntity entity = ImmutableStoreEntity.builder()
          .from(state.getEntity())
          .id(updateType.getId())
          .bodyType(state.getEntity().getBodyType())
          .body(updateType.getBody())
          .build();
      return super.save(entity);
    });
  }
  @Override
  public Uni<List<StoreEntity>> batch(ImportStoreEntity batchType) {
    return get().onItem().transformToUni(currentState -> {
      final var commitBuilder = config.getClient().commit().head()
          .head(config.getRepoName(), config.getHeadName())
          .message("Save batch with new: " + batchType.getCreate().size() + " and updated: " + batchType.getUpdate().size() + " entries")
          .parentIsLatest()
          .author(config.getAuthorProvider().getAuthor());
      
      final List<String> ids = new ArrayList<>();
      for(final var toBeSaved : batchType.getCreate()) {
        final var id = toBeSaved.getId();
        if(id != null) {
          cantHaveEntityWithId(id, currentState);
        }
        
        final var gid = toBeSaved.getId() == null ? gid(toBeSaved.getBodyType()) : toBeSaved.getId();
        final var entity = (StoreEntity) ImmutableStoreEntity.builder()
            .id(gid)
            .hash("")
            .body(toBeSaved.getBody())
            .bodyType(toBeSaved.getBodyType())
            .build();
        commitBuilder.append(entity.getId(), config.getSerializer().toString(entity));
        ids.add(gid);
      }
      for(final var toBeSaved : batchType.getUpdate()) {
        final var id = toBeSaved.getId();
        HdesAssert.isTrue(
            currentState.getDecisions().containsKey(id) ||
            currentState.getFlows().containsKey(id) ||
            currentState.getServices().containsKey(id) ||
            currentState.getTags().containsKey(id), 
            () -> "Entity not found with id: '" + id + "'!");
        
        final var entity = (StoreEntity) ImmutableStoreEntity.builder()
            .id(id)
            .hash("")
            .body(toBeSaved.getBody())
            .bodyType(toBeSaved.getBodyType())
            .build();
        commitBuilder.append(entity.getId(), config.getSerializer().toString(entity));
        ids.add(entity.getId());
      }    
      
      return commitBuilder.build().onItem().transformToUni(commit -> {
            if(commit.getStatus() == CommitStatus.OK) {
              return config.getClient()
                  .objects().blobState()
                  .repo(config.getRepoName())
                  .anyId(config.getHeadName())
                  .blobNames(ids)
                  .list().onItem()
                  .transform(states -> {
                    if(states.getStatus() != ObjectsStatus.OK) {
                      // TODO
                      throw new StoreException("LIST_FAIL", null, convertMessages2(states));
                    }
                    List<StoreEntity> entities = new ArrayList<>(); 
                    for(final var state : states.getObjects().getBlob()) {
                      StoreEntity start = (StoreEntity) config.getDeserializer().fromString(state);
                      entities.add(start);
                    }                  
                    return entities;
                  });
            }
            // TODO
            throw new StoreException("SAVE_FAIL", null, convertMessages(commit));
          });
      
    });
    
  }
  @Override
  public Uni<List<StoreEntity>> delete(DeleteAstType deleteType) {
    final Uni<EntityState> query = getEntityState(deleteType.getId());
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  private String gid(AstBodyType type) {
    return config.getGidProvider().getNextId(type);
  }

  @Override
  public HistoryQuery history() {
    // TODO Auto-generated method stub
    return null;
  }
  
  private void cantHaveEntityWithId(String id, StoreState currentState) {
    HdesAssert.isTrue(!currentState.getDecisions().containsKey(id), () -> "Entity of type 'decision' already exists with id: '" + id + "'!");
    HdesAssert.isTrue(!currentState.getFlows().containsKey(id), () -> "Entity of type 'flow' already exists with id: '" + id + "'!");
    HdesAssert.isTrue(!currentState.getServices().containsKey(id), () -> "Entity of type 'service' already exists with id: '" + id + "'!");
    HdesAssert.isTrue(!currentState.getTags().containsKey(id), () -> "Entity of type 'tag' already exists with id: '" + id + "'!");
  }
}
