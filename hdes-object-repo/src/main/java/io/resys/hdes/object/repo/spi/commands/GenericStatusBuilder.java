package io.resys.hdes.object.repo.spi.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.object.repo.api.ImmutableChanges;
import io.resys.hdes.object.repo.api.ImmutableHeadStatus;
import io.resys.hdes.object.repo.api.ImmutableStatus;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.HeadStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Status;
import io.resys.hdes.object.repo.api.ObjectRepository.StatusBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.HeadException;

public class GenericStatusBuilder implements StatusBuilder {

  private final Objects objects;
  private String headFilter;
  
  public GenericStatusBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public StatusBuilder head(String head) {
    this.headFilter = head;
    return this;
  }
  @Override
  public Status build() {
    if(objects.getHeads().isEmpty()) {
      return ImmutableStatus.builder().build();
    }
    Head master = objects.getHeads().get(ObjectRepository.MASTER);
    Map<String, Commit> masterCommits = buildHead(objects, master.getCommit(), new HashMap<>());
    
    Tree masterTree = getTree(objects, master);
    
    final List<HeadStatus> entries = new ArrayList<>();
    final Collection<Head> heads;
    if(this.headFilter != null) {
      if(!objects.getHeads().containsKey(headFilter)) {
        throw new HeadException(HeadException.builder().headUnknown(headFilter));
      }
      heads = Arrays.asList(objects.getHeads().get(headFilter));
    } else {
      heads = objects.getHeads().values();
    }
    
    for(Head head : heads) {
      if(head.getName().equals(ObjectRepository.MASTER)) {
        continue;
      }
      
      // No diffs
      List<Commit> headCommits = buildCommits(objects, head.getCommit(), masterCommits, new ArrayList<>());
      if(headCommits.isEmpty()) {
        entries.add(ImmutableHeadStatus.builder().head(head.getName()).build());
        continue;
      }
      
      Tree tree = getTree(objects, head);
      
      Commit lastMasterCommit = (Commit) objects.getValues().get(headCommits.get(0).getParent().get());
      Tree lastMasterCommitTree = (Tree) objects.getValues().get(lastMasterCommit.getTree());
      
      List<Changes> changes = new ArrayList<>();
      for(TreeEntry entry : tree.getValues().values()) {
        TreeEntry latestMasterTreeEntry = masterTree.getValues().get(entry.getName());
        TreeEntry originalMasterTreeEntry = lastMasterCommitTree.getValues().get(entry.getName());
        
        // no changes
        if(latestMasterTreeEntry != null && latestMasterTreeEntry.getBlob().equals(entry.getBlob())) {
          continue;
        }
        
        // created
        if(latestMasterTreeEntry == null) {
          Blob blob = (Blob) objects.getValues().get(entry.getBlob());
          changes.add(ImmutableChanges.builder()
              .action(ChangeAction.CREATED)
              .name(entry.getName())
              .newValue(blob.getValue()).build());
          continue;
        }
        
        // update / conflict
        boolean noChangesInMaster = latestMasterTreeEntry.getBlob().equals(originalMasterTreeEntry.getBlob());
        
        // master modified but no changes in head
        if(!noChangesInMaster && originalMasterTreeEntry.getBlob().equals(entry.getBlob())) {
          continue;
        }
        
        Blob latestMasterValue = (Blob) objects.getValues().get(latestMasterTreeEntry.getBlob());  
        Blob headValue = (Blob) objects.getValues().get(entry.getBlob()); 
        changes.add(ImmutableChanges.builder()
            .action(noChangesInMaster ? ChangeAction.MODIFIED : ChangeAction.CONFLICT)
            .name(entry.getName())
            .newValue(headValue.getValue())
            .oldValue(latestMasterValue.getValue()).build());
      }
      
      // deletes
      for(TreeEntry entry : masterTree.getValues().values()) {
        if(tree.getValues().containsKey(entry.getName())) {
          Blob blob = (Blob) objects.getValues().get(entry.getBlob());
          changes.add(ImmutableChanges.builder()
              .action(ChangeAction.DELETED)
              .name(entry.getName())
              .oldValue(blob.getValue()).build());
        }
      }
      
      entries.add(ImmutableHeadStatus.builder().commits(headCommits).head(head.getName()).changes(changes).build());
    }
    
    
    return ImmutableStatus.builder().entries(entries).build();
  }
  
  
  private static Tree getTree(Objects objects, Head head) {
    Commit commit = (Commit) objects.getValues().get(head.getCommit());
    return (Tree) objects.getValues().get(commit.getTree());
  }
  
  private static Map<String, Commit> buildHead(Objects objects, String start, Map<String, Commit> result) {
    Commit commit = (Commit) objects.getValues().get(start);
    result.put(commit.getId(), commit);
  
    if(commit.getParent().isPresent()) {
      return buildHead(objects, commit.getParent().get(), result);
    }
    return result;
  }
  
  private static List<Commit> buildCommits(Objects objects, String start, Map<String, Commit> masterCommits, List<Commit> result) {
    Commit commit = (Commit) objects.getValues().get(start);
    if(masterCommits.containsKey(commit.getId())) {
      return result;
    }
    result.add(commit);
  
    if(commit.getParent().isPresent()) {
      return buildCommits(objects, commit.getParent().get(), masterCommits, result);
    }
    return result;
  }
}
