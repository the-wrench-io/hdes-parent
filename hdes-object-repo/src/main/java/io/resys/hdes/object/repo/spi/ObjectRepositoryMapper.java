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

public interface ObjectRepositoryMapper {

  Serializer serializer();
  Deserializer deserializer();
  Writer writer(Objects from);
  Delete delete(Objects from);
  IdSupplier id();

  interface Delete {
    Head visitHead(Head head);
    Objects build(HeadStatus headStatus);
  }
  
  interface Writer {
    Head visitHead(Head head);
    Tag visitTag(Tag tag);
    Commit visitCommit(Commit commit);
    Blob visitBlob(Blob blob);
    Tree visitTree(Tree tree);
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
