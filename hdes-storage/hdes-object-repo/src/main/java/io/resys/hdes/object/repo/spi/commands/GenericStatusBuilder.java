package io.resys.hdes.object.repo.spi.commands;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import io.resys.hdes.object.repo.api.ImmutableChanges;
import io.resys.hdes.object.repo.api.ImmutableRefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.StatusBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.RefException;
import io.resys.hdes.object.repo.spi.RepoAssert;

public class GenericStatusBuilder implements StatusBuilder {

  @Value.Immutable
  interface RefCommits {
    Ref getRef();
    List<String> getValues();
  }

  
  private final Objects objects;

  public GenericStatusBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public RefStatus get(String refFilter) {
    RepoAssert.notNull(refFilter, () -> "refFilter can't be null!");
    if (!objects.getRefs().containsKey(refFilter)) {
      throw new RefException(RefException.builder().refUnknown(refFilter));
    }
    RefCommits master = buildRefCommits(objects, objects.getRefs().get(ObjectRepository.MASTER));
    return buildRefStatus(objects.getRefs().get(refFilter), master);
  }

  @Override
  public List<RefStatus> find() {
    if (objects.getRefs().isEmpty()) {
      return Collections.emptyList();
    }
    
    RefCommits master = buildRefCommits(objects, objects.getRefs().get(ObjectRepository.MASTER));
    return objects.getRefs().values().stream()
        .filter(ref -> !ref.getName().equals(ObjectRepository.MASTER))
        .map(ref -> buildRefStatus(ref, master))
        .collect(Collectors.toList());
  }

  private RefStatus buildRefStatus(Ref ref, RefCommits master) {
    // No diffs
    RefCommits refCommits = buildDiff(objects, ref, master);
    if (refCommits.getValues().isEmpty()) {
      return ImmutableRefStatus.builder().name(ref.getName()).build();
    }
    
    Tree tree = getTree(objects, ref);
    Tree masterNow = getTree(objects, master.getRef());
    Tree masterThen = (Tree) objects.getValues().get(getParentCommit(objects, refCommits).getTree());
    
    List<Changes> changes = new ArrayList<>();
    for (TreeEntry mergable : tree.getValues().values()) {
      TreeEntry now = masterNow.getValues().get(mergable.getName());
      TreeEntry then = masterThen.getValues().get(mergable.getName());
      
      // no changes in this ref
      if (now != null && now.getBlob().equals(mergable.getBlob())) {
        continue;
      }
      
      // new entry
      if (now == null) {
        Blob blob = (Blob) objects.getValues().get(mergable.getBlob());
        changes.add(ImmutableChanges.builder()
            .action(ChangeAction.CREATED)
            .name(mergable.getName())
            .newValue(blob.getValue()).build());
        continue;
      }
      
      boolean noChangesInMaster = now.getBlob().equals(then.getBlob());
      
      // master modified but no changes in ref
      if (!noChangesInMaster && then.getBlob().equals(mergable.getBlob())) {
        continue;
      }
      
      Blob latestMasterValue = (Blob) objects.getValues().get(now.getBlob());
      Blob refValue = (Blob) objects.getValues().get(mergable.getBlob());
      changes.add(ImmutableChanges.builder()
          .action(noChangesInMaster ? ChangeAction.MODIFIED : ChangeAction.CONFLICT)
          .name(mergable.getName())
          .newValue(refValue.getValue())
          .oldValue(latestMasterValue.getValue()).build());
    }
    // deletes
    for (TreeEntry entry : masterNow.getValues().values()) {
      if (tree.getValues().containsKey(entry.getName())) {
        Blob blob = (Blob) objects.getValues().get(entry.getBlob());
        changes.add(ImmutableChanges.builder()
            .action(ChangeAction.DELETED)
            .name(entry.getName())
            .oldValue(blob.getValue()).build());
      }
    }
    return ImmutableRefStatus.builder().commits(refCommits.getValues()).name(ref.getName()).changes(changes).build();
  }

  private static Tree getTree(Objects objects, Ref ref) {
    Commit commit = (Commit) objects.getValues().get(ref.getCommit());
    return (Tree) objects.getValues().get(commit.getTree());
  }

  private static RefCommits buildRefCommits(Objects objects, Ref start) {
    List<String> values = new ArrayList<>();
    String parent = start.getCommit();
    do {
      Commit commit = (Commit) objects.getValues().get(parent);
      values.add(commit.getId());
      parent = commit.getParent().orElse(null);
    } while(parent != null);
    
    return ImmutableRefCommits.builder().ref(start).values(values).build();
  }

  private static RefCommits buildDiff(Objects objects, Ref start, RefCommits master) {
    List<String> values = new ArrayList<>();
    String parent = start.getCommit();
    while(!master.getValues().contains(parent)) {
      Commit commit = (Commit) objects.getValues().get(parent);
      values.add(commit.getId());
      parent = commit.getParent().orElse(null);
    }
    
    return ImmutableRefCommits.builder().ref(start).values(values).build();
  }
  
  private static Commit getParentCommit(Objects objects, RefCommits commits) {
    String id = commits.getValues().get(commits.getValues().size() - 1);
    Commit commit = (Commit) objects.getValues().get(id);
    return (Commit) objects.getValues().get(commit.getParent().get());
  }
}
