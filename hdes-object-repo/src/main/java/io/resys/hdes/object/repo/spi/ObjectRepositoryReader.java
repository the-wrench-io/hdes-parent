package io.resys.hdes.object.repo.spi;

import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Content;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;

public interface ObjectRepositoryReader {
  Head visitHead(byte[] content);
  Commit visitCommit(String id, byte[] content);
  Content visitBlob(String id, byte[] content);
  Tree visitTree(String id, byte[] content);
  Tag visitTag(String id, byte[] content);
}
