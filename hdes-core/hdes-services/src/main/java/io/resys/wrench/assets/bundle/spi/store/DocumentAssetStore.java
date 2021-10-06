package io.resys.wrench.assets.bundle.spi.store;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.thena.docdb.api.actions.TagActions.TagStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableService;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;
import io.resys.wrench.assets.bundle.spi.exceptions.DeleteException;
import io.resys.wrench.assets.bundle.spi.exceptions.QueryException;
import io.resys.wrench.assets.bundle.spi.exceptions.SaveException;
import io.resys.wrench.assets.bundle.spi.exceptions.TagSaveException;
import io.resys.wrench.assets.bundle.spi.store.document.PersistenceConfig;

public class DocumentAssetStore implements ServiceStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentAssetStore.class);
  private final Map<String, AssetService> cachedAssets = new ConcurrentHashMap<>();
  private final PersistenceConfig config;
  
  public DocumentAssetStore(PersistenceConfig config) {
    this.config = config;
  }
  
  @Override
  public AssetService get(AssetService service, String rev) {
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(rev)
        .blobName(service.getName())
        .get().onItem()
        .transform(state -> {
          if(state.getStatus() != ObjectsStatus.OK) {
            throw new QueryException(service.getId(), state);  
          }
          return ImmutableServiceBuilder.from(service).setSrc(state.getObjects().getBlob().getValue()).build();
        }).await().atMost(Duration.ofSeconds(3));
  }
  
  @Override
  public AssetService load(AssetService service) {
    checkDuplicateAsset(service);
    
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(config.getHeadName())
        .blobName(service.getName())
        .get().onItem()
        .transform(state -> {
          if(state.getStatus() != ObjectsStatus.OK) {
            throw new QueryException(service.getId(), state);  
          }
          return ImmutableServiceBuilder.from(service).setSrc(state.getObjects().getBlob().getValue()).build();
        }).await().atMost(Duration.ofSeconds(3));
  }

  @Override
  public Collection<AssetService> list() {
    return Collections.unmodifiableCollection(cachedAssets.values());
  }

  @Override
  public AssetService get(String id) {
    Assert.isTrue(cachedAssets.containsKey(id), "No asset with id: " + id + "!");
    return cachedAssets.get(id);
  }

  @Override
  public boolean contains(String id) {
    return cachedAssets.containsKey(id);
  }

  @Override
  public AssetService save(AssetService service) {
    checkDuplicateAsset(service);

    LOGGER.debug("Saving assets: {} of type: {}", service.getName(), service.getType());

    if(service.getType() == ServiceType.TAG) {
      this.config.getClient().tag().create().tagName(service.getName()).message("release").build()
      .onItem().transform(commit -> {
        if(commit.getStatus() != TagStatus.OK) {
          throw new TagSaveException(service, commit);
        }
        return service;
      });
    } else {
       config.getClient().commit().head()
          .head(config.getRepoName(), config.getHeadName())
          .message("update type: '" + service.getType() + "', with id: '" + service.getId() + "'")
          .parentIsLatest()
          .author(config.getAuthorProvider().get().getUser())
          .append(service.getId(), service.getSrc())
          .build().onItem().transform(commit -> {
            if(commit.getStatus() == CommitStatus.OK) {
              return service;
            }
            throw new SaveException(service, commit);
          });    
    }
    
    final AssetService oldService = cachedAssets.get(service.getId());
    final AssetService result;
    if(oldService == null) {
      result = ImmutableService.of(service);
    } else {
      result = ImmutableService.of(service, oldService.getDataModel().getCreated(), new Timestamp(System.currentTimeMillis()));
    }
    
    cachedAssets.put(service.getId(), result);
    return result;
  }

  private void checkDuplicateAsset(AssetService service) {
    Optional<AssetService> duplicate = cachedAssets.values().stream()
        .filter(a -> !a.getId().equals(service.getId()))
        .filter(a -> a.getType() == service.getType())
        .filter(a -> a.getName().equalsIgnoreCase(service.getName()))
        .findFirst();
    if(duplicate.isPresent()) {
      throw AssetErrorCodes.SERVICE_NAME_NOT_UNIQUE.newException(
          service.getId() + " / " + duplicate.get().getId(), 
          duplicate.get().getName());
    }
  }



  @Override
  public List<String> getTags() {
    return config.getClient().tag()
    .query()
    .repo(config.getRepoName())
    .find()
    .onItem().transform(tag -> {
      return tag.getName();
    }).subscribe().asStream().collect(Collectors.toList());   
  }

  @Override
  public void remove(String id) {
    config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message("delete id: '" + id + "'")
        .parentIsLatest()
        .author(config.getAuthorProvider().get().getUser())
        .remove(id)
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return id;
          }
          throw new DeleteException(id, commit);
        });
  }

}
