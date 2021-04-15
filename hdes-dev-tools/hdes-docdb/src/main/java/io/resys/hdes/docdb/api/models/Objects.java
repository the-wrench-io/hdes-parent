package io.resys.hdes.docdb.api.models;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface Objects {
  Map<String, Ref> getRefs();
  Map<String, Tag> getTags();
  Map<String, IsObject> getValues();
  Optional<Head> getHead();
  
  public static final String MAIN = "main";
  interface IsObject { String getId(); }
  interface IsName { String getName(); }
  
  @Value.Immutable
  interface Head extends IsName {
    String getCommit();
  }
  
  // branch with a name
  @Value.Immutable
  interface Ref extends IsName {
    // last commit in the branch
    String getCommit();
  }

  @Value.Immutable
  interface Tag extends IsName {
    // id of a commit
    String getCommit();
  }
  
  // World state 
  @Value.Immutable
  interface Tree extends IsObject {
    // resource name - blob id
    Map<String, TreeValue> getValues();
  }
  
  // Resource name - blob id(content in blob)
  @Value.Immutable
  interface TreeValue {
    // Name of the resource
    String getName();
    // Id of the blob that holds content
    String getBlob();
  }
  
  @Value.Immutable
  interface Commit extends IsObject {
    String getAuthor();
    LocalDateTime getDateTime();
    String getMessage();
    
    // Parent commit id
    Optional<String> getParent();
    
    // This commit is merge commit, that points to a commit in different branch
    Optional<String> getMerge();
    
    // Tree id that describes list of (resource name - content) entries
    String getTree();
  }
  
  @Value.Immutable
  interface Blob extends IsObject {
    String getValue();
  }
}
