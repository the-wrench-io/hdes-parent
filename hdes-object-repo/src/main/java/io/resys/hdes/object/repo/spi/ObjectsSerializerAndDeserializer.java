package io.resys.hdes.object.repo.spi;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.object.repo.api.ImmutableBlob;
import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ImmutableTag;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ImmutableTreeEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.RepoException;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper.Deserializer;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper.Serializer;

public class ObjectsSerializerAndDeserializer implements Serializer, Deserializer {
  public final static ObjectsSerializerAndDeserializer INSTANCE = new ObjectsSerializerAndDeserializer();
  
  public static final String TYPE_BLOB = "blob ";
  public static final String TYPE_TREE = "tree ";
  public static final String TYPE_COMMIT = "commit "; 
  
  private ObjectsSerializerAndDeserializer() {
    super();
  }

  private Commit visitCommit(String id, String content) {
    String[] value = content.split("/n");

    String treeId = value[0].split(" ")[1];
    String[] parent = value[1].split(" ");
    LocalDateTime dateTime = LocalDateTime.parse(value[2].split(" ")[1]);
    String author = value[3].split(" ")[1];
    return ImmutableCommit.builder()
        .id(id)
        .parent(parent.length > 1 ? parent[1] : null)
        .tree(treeId)
        .dateTime(dateTime)
        .author(author)
        .build();
  }
  
  private byte[] visitCommit(Commit commit) {
    return new StringBuilder()
    .append(TYPE_COMMIT).append(commit.getTree()).append("/n")
    .append("parent ").append(commit.getParent().orElse("")).append("/n")
    .append("localDateTime ").append(commit.getDateTime()).append("/n")
    .append("author ").append(commit.getAuthor()).append("/n")
    .toString().getBytes(StandardCharsets.UTF_8);
  }

  private Blob visitBlob(String id, String content) {
    return ImmutableBlob.builder().id(id).value(content.substring(TYPE_BLOB.length())).build();
  }

  private byte[] visitBlob(Blob blob) {
    return new StringBuilder()
        .append(TYPE_BLOB).append(blob.getValue())
        .toString().getBytes(StandardCharsets.UTF_8);
  }
  
  private Tree visitTree(String id, String content) {
    String[] lines = content.split("/n");
    Map<String, TreeEntry> values = new HashMap<>();
    for(int index = 1; index < lines.length; index++) {
      String line = lines[index];
      int sep = line.lastIndexOf(" ");
      
      String name = line.substring(0, sep);
      String value = line.substring(sep);
      
      values.put(name, ImmutableTreeEntry.builder()
          .name(name)
          .blob(value)
          .build());
    }
    return ImmutableTree.builder().id(id).values(values).build();
  }
  
  private byte[] visitTree(Tree tree) {
    StringBuilder result = new StringBuilder()
        .append(TYPE_TREE).append("/n");
    for(TreeEntry entry : tree.getValues().values()) {
      result
      .append(entry.getName()).append(" ")
      .append(entry.getBlob()).append(" ")
      .append("/n");
    }
    return result.toString()
        .toString().getBytes(StandardCharsets.UTF_8);
  }
  
  @Override
  public IsObject visitObject(String id, byte[] content) {
    String value = new String(content, StandardCharsets.UTF_8);
    if(value.startsWith(TYPE_BLOB)) {
      return visitBlob(id, value);
    } else if(value.startsWith(TYPE_TREE)) {
      return visitTree(id, value);
    } else if(value.startsWith(TYPE_COMMIT)) {
      return visitCommit(id, value);
    }
    throw new RepoException("Unknown object: " + id + System.lineSeparator() + value);
  }
  
  @Override
  public Head visitHead(String id, byte[] content) {
    String commit = new String(content, StandardCharsets.UTF_8);
    return ImmutableHead.builder()
        .name(id)
        .commit(commit)
        .build();
  }

  @Override
  public Tag visitTag(String id, byte[] content) {
    String[] lines = new String(content, StandardCharsets.UTF_8).split("/n");
    
    return ImmutableTag.builder()
        .name(id)
        .commit(lines[1])
        .build();
  }

  @Override
  public byte[] visitHead(Head head) {
    return head.getCommit().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] visitTag(Tag tag) {
    return tag.getCommit().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] visitObject(IsObject object) {
    
    if(object instanceof Blob) {
      return visitBlob((Blob) object);
    
    } else if(object instanceof Tree) {
      return visitTree((Tree) object);
    
    } else if(object instanceof Commit) {
      return visitCommit((Commit) object);
    }
    throw new RepoException("Unknown object: " + object);
  }
  
  
}