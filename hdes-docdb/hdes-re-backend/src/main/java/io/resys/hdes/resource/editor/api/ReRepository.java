package io.resys.hdes.resource.editor.api;

import java.util.List;

import io.resys.hdes.resource.editor.api.ReResource.BlobUpdate;
import io.resys.hdes.resource.editor.api.ReResource.ProjectResource;
import io.resys.hdes.resource.editor.api.ReResource.SnapshotResource;


public interface ReRepository {
  ReUpdate update();
  ReQuery query();

  interface ReUpdate {
    SnapshotResource blob(BlobUpdate update) throws ReException;
  }
    
  interface ReQuery {
    ProjectQuery projects();
    SnapshotQuery snapshots();
  }
  
  interface SnapshotQuery {
    ProjectResource get(String idOrName)  throws ReException;
  }
  
  interface ProjectQuery {
    ProjectResource get(String idOrName);
    List<ProjectResource> find();
  } 
}
