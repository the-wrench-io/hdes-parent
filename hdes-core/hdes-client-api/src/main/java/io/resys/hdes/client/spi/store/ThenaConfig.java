package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.thena.docdb.api.DocDB;
import io.resys.thena.docdb.api.actions.ObjectsActions.BlobObject;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.thena.docdb.api.models.Objects.Blob;
import io.smallrye.mutiny.Uni;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.List;

@Value.Immutable
public interface ThenaConfig {
  DocDB getClient();
  String getRepoName();
  String getHeadName();
  AuthorProvider getAuthorProvider();
  
  @FunctionalInterface
  interface GidProvider {
    String getNextId(AstBodyType entity);
  }
  
  GidProvider getGidProvider();
  
  @FunctionalInterface
  interface Serializer {
    String toString(StoreEntity entity);
  }
  
  interface Deserializer {
    StoreEntity fromString(Blob value);
  }
  Serializer getSerializer();
  Deserializer getDeserializer();
  
  @FunctionalInterface
  interface AuthorProvider {
    String getAuthor();
  }
  
  @Value.Immutable
  interface EntityState {
    ObjectsResult<BlobObject> getSrc();
    StoreEntity getEntity();
  }
  
  interface Commands {
    Uni<List<StoreEntity>> delete(StoreEntity toBeDeleted);
    Uni<StoreState> get();
    Uni<EntityState> getEntityState(String id);
    Uni<StoreEntity> save(StoreEntity toBeSaved);
    Uni<Collection<StoreEntity>> save(Collection<StoreEntity> toBeSaved);
  }  
}