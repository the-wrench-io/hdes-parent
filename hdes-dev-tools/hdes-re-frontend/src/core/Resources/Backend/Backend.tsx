
declare namespace Backend {
  
  interface ServiceCallback<T> {
    onSuccess: (handle: (resource: T) => void) => void; 
  }
  
  interface ServiceListeners {
    onSave: (saved: Commit) => void;
    onDelete: (deleted: Commit) => void;
    onError: (error: ServerError) => void;
  }
  
  interface Service {
    projects: ProjectService;
    commits: CommitService;
    snapshots: SnapshotService;
    heads: HeadService;
    listeners: ServiceListeners;
    withListeners: (listeners: ServiceListeners) => Service;
  }
  
  interface ProjectService {
    query: () => ProjectQuery;    
  }
  
  interface ProjectQuery extends ServiceCallback<ProjectResource[]>{}
  
  interface HeadService {
    query: () => ProjectQuery;    
  }
  
  interface HeadQuery extends ServiceCallback<HeadResource[]>{}
  
  
  
  
  interface CommitService {
    
  }
  
  interface SnapshotService {
    
  }

  
  interface ProjectResource {
    project: Project;
    heads: Record<string, Head>;
  }
  
  interface HeadResource {
    head: Head;
    state: { commits: number, type: "ahead" | "behind" }
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