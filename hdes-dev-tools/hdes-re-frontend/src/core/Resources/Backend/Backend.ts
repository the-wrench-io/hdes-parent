import Ast from './Ast';


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
  interface HeadService {
    query: () => HeadQuery;
    delete: (value: Head) => ServiceCallback<Head>;    
  }
  interface MergeService {
    save: (value: Head) => ServiceCallback<Head>;
  }
  interface SnapshotService {
    query: (args: {head: Head}) => SnapshotQuery;
  }
  interface CommitService {
    save: (changes: AssetBlob[]) => ServiceCallback<SnapshotResource>;
  }

  interface ProjectQuery extends ServiceCallback<ProjectResource[]>{}
  interface HeadQuery extends ServiceCallback<HeadResource[]>{}  
  interface SnapshotQuery extends ServiceCallback<SnapshotResource>{}

  interface SnapshotResource {
    head: Head;
    project: Project;
    blobs: Record<string, AssetBlob>;       // name - asset
    ast: Record<string, Ast.BodyNode>;      // asset name - ast
    errors: Record<string, Ast.ErrorNode>;  // name name - error
  }
  
  interface ProjectResource {
    project: Project;
    heads: Record<string, Head>;
    
    // head name
    states: Record<string, HeadState>;
  }
  interface HeadResource {
    head: Head;
  }
  
  interface AssetBlob {
    id: string;
    name: string;
    src: string; 
    ast: string[]
  }

  interface HeadState {
    id: string;
    head: string; //head name
    commits: number; 
    type: "ahead" | "behind" | "same";
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
    headers: {};
  }

  interface ServerError {
    id: string,
    messages: { code: string, value: string}[]
  }
}
export default Backend;