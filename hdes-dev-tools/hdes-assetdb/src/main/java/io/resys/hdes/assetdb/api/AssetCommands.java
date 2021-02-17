package io.resys.hdes.assetdb.api;

import java.util.List;

import io.resys.hdes.assetdb.api.AssetClient.Commit;
import io.resys.hdes.assetdb.api.AssetClient.Objects;
import io.resys.hdes.assetdb.api.AssetClient.RefStatus;
import io.resys.hdes.assetdb.api.AssetClient.Snapshot;
import io.resys.hdes.assetdb.api.AssetClient.Tag;

public interface AssetCommands {
  StatusBuilder status();
  CommitBuilder commit();
  HistoryBuilder history();
  SnapshotBuilder snapshot();
  CheckoutBuilder checkout();
  PullCommand pull();
  MergeBuilder merge();
  TagBuilder tag();
  
  interface PullCommand {
    Object build();
  }
  
  interface RebaseBuilder {
    RebaseBuilder author(String author);
    RebaseBuilder ref(String refName);
    Objects build();
  }
  
  interface MergeBuilder {
    // ref name from what to merge to "master"
    MergeBuilder ref(String name);
    MergeBuilder author(String author);
    Objects build();
  }
  
  interface StatusBuilder {   
    // Build overview of other refs related to 'master' ref
    List<RefStatus> find();
    
    // Build overview of other ref(name) related to 'master' ref
    RefStatus get(String name);
  }
  
  interface TagBuilder {
    // Name of the tag
    TagBuilder name(String name);
    // optional commit for what to add tag
    TagBuilder commit(String commit);
    // tags can be created only from master
    Tag build();
  }
  
  interface HistoryBuilder {
    List<Commit> build();
  }
  
  interface CheckoutBuilder {
    // tag/commit/ref
    CheckoutBuilder from(String name);
    Objects build();
  }
  
  interface SnapshotBuilder {
    // tag/commit/ref
    SnapshotBuilder from(String name);
    Snapshot build();
  }
  
  interface CommitBuilder {
    CommitBuilder add(String name, String content);
    CommitBuilder delete(String name);
    CommitBuilder change(String name, String content);
    CommitBuilder conflict(String name, String oldValue, String newValue);
    
    CommitBuilder ref(String name);
    CommitBuilder parent(String commitId);
    CommitBuilder author(String author);
    CommitBuilder comment(String message);
    CommitBuilder merge(String commitId);
    Commit build();
  }
}
