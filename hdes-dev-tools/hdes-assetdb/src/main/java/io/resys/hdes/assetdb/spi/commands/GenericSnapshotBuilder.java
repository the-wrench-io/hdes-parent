package io.resys.hdes.assetdb.spi.commands;

/*-
 * #%L
 * hdes-object-repo
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.assetdb.api.AssetClient.Blob;
import io.resys.hdes.assetdb.api.AssetClient.Commit;
import io.resys.hdes.assetdb.api.AssetClient.Objects;
import io.resys.hdes.assetdb.api.AssetClient.Snapshot;
import io.resys.hdes.assetdb.api.AssetClient.SnapshotBuilder;
import io.resys.hdes.assetdb.api.AssetClient.SnapshotEntry;
import io.resys.hdes.assetdb.api.AssetClient.Tree;
import io.resys.hdes.assetdb.api.AssetClient.TreeEntry;
import io.resys.hdes.assetdb.api.ImmutableSnapshot;
import io.resys.hdes.assetdb.api.ImmutableSnapshotEntry;
import io.resys.hdes.assetdb.api.exceptions.CommitException;
import io.resys.hdes.assetdb.spi.RepoAssert;

public class GenericSnapshotBuilder implements SnapshotBuilder {

  private final Objects objects;
  private String name;

  public GenericSnapshotBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public SnapshotBuilder from(String name) {
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
      throw new CommitException(CommitException.builder().notFound(name));
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
