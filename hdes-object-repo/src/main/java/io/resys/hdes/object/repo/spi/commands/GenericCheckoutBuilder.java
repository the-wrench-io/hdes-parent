package io.resys.hdes.object.repo.spi.commands;

import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.object.repo.api.ImmutableSnapshot;
import io.resys.hdes.object.repo.api.ImmutableSnapshotEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.CheckoutBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Snapshot;
import io.resys.hdes.object.repo.api.ObjectRepository.SnapshotEntry;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.CheckoutException;
import io.resys.hdes.object.repo.spi.RepoAssert;

public class GenericCheckoutBuilder implements CheckoutBuilder {

  private final Objects objects;
  private String name;

  public GenericCheckoutBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public CheckoutBuilder from(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Snapshot build() {
    RepoAssert.notNull(name, () -> "name can't be null");
    
    String id;
    if(objects.getTags().containsKey(name)) {
      id = objects.getTags().get(name).getCommit();
    } else if(objects.getRefs().containsKey(name)) {
      id = objects.getRefs().get(name).getCommit();
    } else if(objects.getValues().containsKey(name)) {
      id = name;
    } else {
      throw new CheckoutException(CheckoutException.builder().notFound(name));
    }
    
    Commit commit = CommitQuery.builder(objects).commit(id).get();
    Tree tree = (Tree) objects.getValues().get(commit.getTree());
    
    List<SnapshotEntry> values = new ArrayList<>();
    for(TreeEntry entry : tree.getValues().values()) {
      Blob blob = (Blob) objects.getValues().get(entry.getBlob());
      values.add(ImmutableSnapshotEntry.builder()
          .name(entry.getName())
          .blob(blob.getValue())
          .build());
    }
    
    return ImmutableSnapshot.builder()
        .id(name)
        .tree(tree)
        .values(values)
        .build();
  }
}
