package io.resys.hdes.object.repo.spi;

import java.util.List;

import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.HeadStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;

public interface ObjectRepositoryMapper<T> {

  Serializer serializer();
  Deserializer deserializer();
  Writer<T> writer(Objects from);
  Delete<T> delete(Objects from);
  IdSupplier id();

  interface Delete<T> {
    Head visitHead(T to, Head head);
    Objects build(HeadStatus headStatus);
  }
  
  interface Writer<T> {
    Head visitHead(T to, Head head);
    Tag visitTag(T to, Tag tag);
    Commit visitCommit(T to, Commit commit);
    Blob visitBlob(T to, Blob blob);
    Tree visitTree(T to, Tree tree);
    Objects build(List<Object> objects);
  }
  
  interface Deserializer {
    Head visitHead(String id, byte[] content);
    Tag visitTag(String id, byte[] content);
    IsObject visitObject(String id, byte[] content);
  }
  interface Serializer {
    byte[] visitHead(Head head);
    byte[] visitTag(Tag tag);
    byte[] visitObject(IsObject object);
  }
  
  interface IdSupplier {
    Blob id(Blob blob);
    Tree id(Tree tree);
    Commit id(Commit commit);
  }
}
