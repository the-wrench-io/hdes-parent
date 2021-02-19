
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
    listeners: ServiceListeners;
    withListeners: (listeners: ServiceListeners) => Service;
  }
  
  interface ProjectService {
    
  }
  
  interface CommitService {
    
  }
  
  interface SnapshotService {
    
  }
  
  interface Project {
    
  }
  
  interface Snapshot {
    
  }
  
  interface Commit {
    
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