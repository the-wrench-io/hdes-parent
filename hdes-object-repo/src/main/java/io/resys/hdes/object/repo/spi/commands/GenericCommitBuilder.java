package io.resys.hdes.object.repo.spi.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.resys.hdes.object.repo.api.ImmutableBlob;
import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableHead;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ImmutableTreeEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.CommitException;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.file.RepoAssert;

public abstract class GenericCommitBuilder implements CommitBuilder {
  private final static String FAKE_ID = "!-unknown-atm-!";
  
  private final List<TreeEntry> toAdd = new ArrayList<>();
  private final List<Object> newObjects = new ArrayList<>();
  private final List<String> toDelete = new ArrayList<>();
  
  private final Objects objects;
  private final ObjectRepositoryMapper mapper;
  
  private String newHead;
  private String parentId;
  private String author;
  private String comment;
  

  public GenericCommitBuilder(Objects objects, ObjectRepositoryMapper mapper) {
    super();
    this.objects = objects;
    this.mapper = mapper;
  }

  public Blob blob(String content) {
    return add(mapper.id().id(ImmutableBlob.builder().id(FAKE_ID).value(content).build()));
  }

  @Override
  public CommitBuilder add(String name, String content) {
    Blob blob = blob(content);
    toAdd.add(ImmutableTreeEntry.builder().blob(blob.getId()).name(name).build());
    return this;
  }
  
  @Override
  public CommitBuilder change(String name, String content) {
    Blob blob = blob(content);
    toAdd.add(ImmutableTreeEntry.builder().blob(blob.getId()).name(name).build());
    return this;
  }
  
  @Override
  public CommitBuilder delete(String name) {
    toDelete.add(name);
    return this;
  }

  @Override
  public CommitBuilder parent(String commitId) {
    this.parentId = commitId;
    return this;
  }

  @Override
  public CommitBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public CommitBuilder head(String head) {
    this.newHead = head;
    return this;
  }
  @Override
  public CommitBuilder comment(String comment) {
    this.comment = comment;
    return this;
  }
  @Override
  public Objects build() {
    RepoAssert.notNull(author, () -> "author must be defined!");
    RepoAssert.notNull(comment, () -> "comment must be defined!");
    
    // nothing to commit
    if (toAdd.isEmpty()) {
      throw new CommitException(CommitException.builder().emptyCommit(parentId, author));
    }
    
    // First commit
    Optional<Commit> parent;
    Optional<Head> head;
    Map<String, TreeEntry> oldTree;
    if(objects.getHeads().isEmpty()) {
      oldTree = new HashMap<>();
      head = Optional.of(ImmutableHead.builder()
          .name(newHead == null || newHead.trim().isEmpty() ? "master" : newHead.trim())
          .commit(FAKE_ID)
          .build());
      parent = Optional.empty();
    } else {
      RepoAssert.notNull(parentId, () -> "parent must be defined!");
      parent = Optional.ofNullable((Commit) objects.getValues().get(parentId));
      if (!parent.isPresent()) {
        throw new CommitException(CommitException.builder().unknownParent(parentId, author));
      }
      head = objects.getHeads().values().stream().filter(h -> h.getCommit().equals(parentId)).findFirst();
      if (!head.isPresent()) {
        throw new CommitException(CommitException.builder().headDoesNotMatch(parentId, author, objects.getHeads().values()));
      }
      oldTree = ((Tree) objects.getValues().get(parent.get().getTree())).getValues();
    }
    
    // create tree
    
    Map<String, TreeEntry> newTree = new HashMap<>(oldTree);
    
    
    // add
    for (TreeEntry entry : this.toAdd) {
      newTree.put(entry.getName(), entry);
    }
    
    // delete
    for (String entry : this.toDelete) {
      newTree.remove(entry);
    }
    
    
    Tree tree = add(mapper.id().id(ImmutableTree.builder().id(FAKE_ID).values(newTree).build()));
    
    
    Commit commit = add(mapper.id().id(ImmutableCommit.builder()
        .id(FAKE_ID)
        .tree(tree.getId())
        .author(author)
        .dateTime(LocalDateTime.now())
        .message(comment)
        .parent(parent.map(e -> e.getId()))
        .build()));
    
    if(objects.getValues().containsKey(commit.getId())) {
      throw new CommitException(CommitException.builder().lastCommitIsIdentical(parentId, author));
    }
    
    add(ImmutableHead.builder()
        .from(head.get())
        .commit(commit.getId())
        .build());
    
    return save(newObjects);
  }
  
  protected abstract Objects save(List<Object> newObjects);
  
  private <T> T add(T object) {
    newObjects.add(object);
    return object;
  } 
}
