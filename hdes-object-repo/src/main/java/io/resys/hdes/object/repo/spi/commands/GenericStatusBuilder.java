package io.resys.hdes.object.repo.spi.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.object.repo.api.ImmutableChanges;
import io.resys.hdes.object.repo.api.ImmutableRefStatus;
import io.resys.hdes.object.repo.api.ImmutableStatus;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Blob;
import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Status;
import io.resys.hdes.object.repo.api.ObjectRepository.StatusBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Tree;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.RefException;

public class GenericStatusBuilder implements StatusBuilder {

  private final Objects objects;
  private String refFilter;
  
  public GenericStatusBuilder(Objects objects) {
    super();
    this.objects = objects;
  }

  @Override
  public StatusBuilder ref(String refName) {
    this.refFilter = refName;
    return this;
  }
  @Override
  public Status build() {
    if(objects.getRefs().isEmpty()) {
      return ImmutableStatus.builder().build();
    }
    Ref master = objects.getRefs().get(ObjectRepository.MASTER);
    Map<String, Commit> masterCommits = buildRef(objects, master.getCommit(), new HashMap<>());
    
    Tree masterTree = getTree(objects, master);
    
    final List<RefStatus> entries = new ArrayList<>();
    final Collection<Ref> refs;
    if(this.refFilter != null) {
      if(!objects.getRefs().containsKey(refFilter)) {
        throw new RefException(RefException.builder().refUnknown(refFilter));
      }
      refs = Arrays.asList(objects.getRefs().get(refFilter));
    } else {
      refs = objects.getRefs().values();
    }
    
    for(Ref ref : refs) {
      if(ref.getName().equals(ObjectRepository.MASTER)) {
        continue;
      }
      
      // No diffs
      List<Commit> refCommits = buildCommits(objects, ref.getCommit(), masterCommits, new ArrayList<>());
      if(refCommits.isEmpty()) {
        entries.add(ImmutableRefStatus.builder().name(ref.getName()).build());
        continue;
      }
      
      Tree tree = getTree(objects, ref);
      
      Commit lastMasterCommit = (Commit) objects.getValues().get(refCommits.get(0).getParent().get());
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
        
        // master modified but no changes in ref
        if(!noChangesInMaster && originalMasterTreeEntry.getBlob().equals(entry.getBlob())) {
          continue;
        }
        
        Blob latestMasterValue = (Blob) objects.getValues().get(latestMasterTreeEntry.getBlob());  
        Blob refValue = (Blob) objects.getValues().get(entry.getBlob()); 
        changes.add(ImmutableChanges.builder()
            .action(noChangesInMaster ? ChangeAction.MODIFIED : ChangeAction.CONFLICT)
            .name(entry.getName())
            .newValue(refValue.getValue())
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
      
      entries.add(ImmutableRefStatus.builder().commits(refCommits).name(ref.getName()).changes(changes).build());
    }
    
    
    return ImmutableStatus.builder().entries(entries).build();
  }
  
  
  private static Tree getTree(Objects objects, Ref ref) {
    Commit commit = (Commit) objects.getValues().get(ref.getCommit());
    return (Tree) objects.getValues().get(commit.getTree());
  }
  
  private static Map<String, Commit> buildRef(Objects objects, String start, Map<String, Commit> result) {
    Commit commit = (Commit) objects.getValues().get(start);
    result.put(commit.getId(), commit);
  
    if(commit.getParent().isPresent()) {
      return buildRef(objects, commit.getParent().get(), result);
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
