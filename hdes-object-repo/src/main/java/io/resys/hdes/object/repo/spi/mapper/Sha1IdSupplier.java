package io.resys.hdes.object.repo.spi.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.resys.hdes.object.repo.api.ImmutableBlob;
import io.resys.hdes.object.repo.api.ImmutableCommit;
import io.resys.hdes.object.repo.api.ImmutableTree;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.IdSupplier;

@SuppressWarnings("deprecation")
public class Sha1IdSupplier implements IdSupplier {
  private static final TreeEntryComparator comparator = new TreeEntryComparator();

  @Override
  public Blob id(Blob blob) {
    String id = Hashing.sha1().hashString(blob.getValue(), Charsets.UTF_8).toString();
    return ImmutableBlob.builder().from(blob).id(id).build();
  }

  @Override
  public Tree id(Tree tree) {
    List<TreeEntry> source = new ArrayList<>(tree.getValues().values());
    Collections.sort(source, comparator);
    String id = Hashing.sha1().hashString(source.toString(), Charsets.UTF_8).toString();
    return ImmutableTree.builder().from(tree).id(id).build();
  }

  @Override
  public Commit id(Commit commit) {
    String id = Hashing.sha1().hashString(commit.toString(), Charsets.UTF_8).toString();
    return ImmutableCommit.builder()
        .from(commit)
        .id(id)
        .build();
  }

  static class TreeEntryComparator implements Comparator<TreeEntry> {
    @Override
    public int compare(TreeEntry o1, TreeEntry o2) {
      return o1.getName().compareTo(o2.getName());
    }
  }
}