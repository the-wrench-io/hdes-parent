package io.resys.hdes.object.repo.spi.mapper;

import java.util.List;

import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;

public interface ObjectRepositoryMapper<T> {

  Serializer serializer();
  Deserializer deserializer();
  Writer<T> writer(Objects from);
  Delete<T> delete(Objects from);
  IdSupplier id();

  interface Delete<T> {
    Ref visitRef(T to, Ref ref);
    Objects build(RefStatus refStatus);
  }
  
  interface Writer<T> {
    Ref visitRef(T to, Ref ref);
    Tag visitTag(T to, Tag tag);
    Commit visitCommit(T to, Commit commit);
    Blob visitBlob(T to, Blob blob);
    Tree visitTree(T to, Tree tree);
    Objects build(List<Object> objects);
  }
  
  interface Deserializer {
    Ref visitRef(String id, byte[] content);
    Tag visitTag(String id, byte[] content);
    IsObject visitObject(String id, byte[] content);
  }
  interface Serializer {
    byte[] visitRef(Ref ref);
    byte[] visitTag(Tag tag);
    byte[] visitObject(IsObject object);
  }
  
  interface IdSupplier {
    Blob id(Blob blob);
    Tree id(Tree tree);
    Commit id(Commit commit);
  }
}
