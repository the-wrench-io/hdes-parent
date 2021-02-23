
declare namespace Backend {
  
  type AnyResource = Commit | Head | Project;
  type ResourceType = "commit" | "head" | "project";
  
  interface ServiceCallback<T> {
    onSuccess: (handle: (resource: T) => void) => void; 
  }
  
  interface ServiceListeners {
    id: string,
    onSave: (saved: AnyResource, type: ResourceType) => void;
    onDelete: (deleted: AnyResource, type: ResourceType) => void;
    onError: (error: ServerError, type: ResourceType) => void;
  }
  
  interface Service {
    projects: ProjectService;
    commits: CommitService;
    snapshots: SnapshotService;
    heads: HeadService;
    merge: MergeService;
    
    listeners: ServiceListeners;
    withListeners: (listeners: ServiceListeners) => Service;
  }
  
  interface ProjectService {
    query: () => ProjectQuery;    
  }
  
  interface ProjectQuery extends ServiceCallback<ProjectResource[]>{}
  
  interface HeadService {
    query: () => HeadQuery;
    delete: (value: Head) => ServiceCallback<Head>;    
  }

  interface MergeService {
    save: (value: Head) => ServiceCallback<Head>;    
  }
  
  interface HeadQuery extends ServiceCallback<HeadResource[]>{}
  
  
  
  interface CommitService {
    
  }
  
  interface SnapshotService {
    
  }

  
  interface ProjectResource {
    project: Project;
    heads: Record<string, Head>;
    
    // head name
    states: Record<string, ProjectHeadState>;
  }
  
  interface ProjectHeadState {
    id: string;
    head: string; //head name
    commits: number; 
    type: "ahead" | "behind" | "same";
  }
  
  interface HeadResource {
    head: Head;
  }
  
  interface Head {
    id: string;
    name: string;
    commit: Commit;
  }
  
  interface Project {
    id: string;
    name: string;
  }
  
  
  interface Snapshot {
    
  }
  
  interface Commit {
    id: string;
    author: string;
    dateTime: Date | number[];
  }
  
  interface ServerConfig {
    ctx: string;    
    projects: string;
    commits: string;
    snapshots: string;
    
    headers: {}
  }
  
  
  interface ServerError {
    id: string,
    messages: { code: string, value: string}[]
  }
}
export default Backend;