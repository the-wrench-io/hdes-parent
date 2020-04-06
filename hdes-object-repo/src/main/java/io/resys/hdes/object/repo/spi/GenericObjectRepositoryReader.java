package io.resys.hdes.object.repo.spi;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableContent;
import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ImmutableTag;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ImmutableTreeEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.ActionType;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Content;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;

public class GenericObjectRepositoryReader implements ObjectRepositoryReader {

  @Override
  public Head visitHead(byte[] content) {
    String value = new String(content, StandardCharsets.UTF_8);
    return ImmutableHead.builder()
        .commitId(value)
        .build();
  }

  @Override
  public Commit visitCommit(String id, byte[] content) {
    String[] value = new String(content, StandardCharsets.UTF_8).split("/n");
    String treeId = value[0].split(" ")[1];
    LocalDateTime dateTime = LocalDateTime.parse(value[1].split(" ")[1]);
    String author = value[2].split(" ")[1];
    return ImmutableCommit.builder()
        .id(id)
        .treeId(treeId)
        .dateTime(dateTime)
        .author(author)
        .build();
  }

  @Override
  public Content visitBlob(String id, byte[] content) {
    return ImmutableContent.builder().id(id).bytes(content).build();
  }

  @Override
  public Tree visitTree(String id, byte[] content) {
    String[] lines = new String(content, StandardCharsets.UTF_8).split("/n");
    List<TreeEntry> values = new ArrayList<>();
    for(String line : lines) {
      String[] columns = line.split(" ");
      values.add(ImmutableTreeEntry.builder()
          .action(ActionType.valueOf(columns[0]))
          .name(columns[1])
          .contentId(columns[2])
          .build());
    }
    return ImmutableTree.builder().id(id).values(values).build();
  }

  @Override
  public Tag visitTag(String id, byte[] content) {
    String[] lines = new String(content, StandardCharsets.UTF_8).split("/n");
    
    return ImmutableTag.builder()
        .id(id)
        .name(lines[0].split(" ")[1])
        .commitId(lines[1].split(" ")[1])
        .build();
  }
}
