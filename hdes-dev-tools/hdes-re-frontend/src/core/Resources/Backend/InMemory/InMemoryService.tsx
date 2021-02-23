import Backend from './../Backend';
import createDemoData, {DemoHead} from './DemoData';



class InMemoryService implements Backend.Service {
  private _snapshots: Backend.SnapshotService;
  private _commits: Backend.CommitService;
  private _projects: Backend.ProjectService;
  private _heads: Backend.HeadService;
  private _merge: Backend.MergeService;
  private _listeners: Backend.ServiceListeners;
  private _delegate: Backend.ServiceListeners;
  private _store: Store;
  
  constructor() {
    console.log('creating demo service');
    
    const getId = () => {
      return this._delegate.id;
    }
    
    this._listeners = {
      get id() {
        return getId();
      },
      onSave: (saved: Backend.AnyResource, type: Backend.ResourceType) => this._delegate.onSave(saved, type),
      onDelete: (deleted: Backend.AnyResource, type: Backend.ResourceType) => this._delegate.onDelete(deleted, type),
      onError: (error: Backend.ServerError, type: Backend.ResourceType) => this._delegate.onError(error, type),
    }
    this._delegate = { 
      id: 'log-listeners',
      onSave: (resource) => console.log("saved resources", resource),
      onDelete: (resource) => console.log("deleted resources", resource),
      onError: (error) => console.error("error", error), 
    };
    
    const demoData = createDemoData();
    this._store = new InMemoryStore(this._listeners, demoData.projects, demoData.heads);
    this._snapshots = {};
    this._projects = new InMemoryProjectService(this._store);
    this._heads = new InMemoryHeadService(this._store);
    this._merge = new InMemoryMergeService(this._store);
    this._commits = {};
  }

  get projects() {
    return this._projects;
  }
  get heads() {
    return this._heads;
  }
  get commits() {
    return this._commits;
  }
  get merge() {
    return this._merge;
  }
  get snapshots() {
    return this._snapshots;
  }
  get listeners() {
    return this._listeners;
  }
  withListeners(listeners: Backend.ServiceListeners): Backend.Service {
    this._delegate = listeners;
    return this;
  }
}

class InMemoryMergeService implements Backend.MergeService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  save(head: Backend.Head) {
    const { store } = this;
    return {
      onSuccess(handle: (head: Backend.Head) => void) {
        handle(store.mergeHead(head));
      }
    };
  }
}

class InMemoryProjectService implements Backend.ProjectService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    const { store } = this;
    return {
      onSuccess(handle: (projects: Backend.ProjectResource[]) => void) {
        const projects = [...store.projects];
        handle(projects);
      }
    };
  }
}


class InMemoryHeadService implements Backend.HeadService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    const { store } = this;
    return {      
      onSuccess(handle: (projects: Backend.HeadResource[]) => void) {
        const heads = [...store.heads];
        handle(heads);
      }
    }
  }
  delete(head: Backend.Head) {
    const { store } = this;
    return {  
      onSuccess(handle: (head: Backend.Head) => void) {
        handle(store.deleteHead(head));
      }
    }
  } 
}

interface Store {
  projects: Backend.ProjectResource[];
  heads: Backend.HeadResource[];
  deleteHead(head: Backend.Head): Backend.Head;
  mergeHead(head: Backend.Head): Backend.Head;
}

class InMemoryStore implements Store {
  private _projects: Backend.ProjectResource[];
  private _heads: Backend.HeadResource[];
  private _listeners: Backend.ServiceListeners;
  
  constructor(
    listeners: Backend.ServiceListeners, 
    projects: Backend.ProjectResource[],
    heads: Backend.HeadResource[]) {
    
    this._listeners = listeners;
    this._projects = projects.map(p => {
      p.states = createProjectHeadState(p.heads);
      return p;
    });
    this._heads = heads;
  }
  
  get projects() {
    return this._projects;
  }
  get heads() {
    return this._heads;
  }
  mergeHead(target: Backend.Head) {
    const project = this._projects
      .filter(project =>  Object.values(project.heads).filter(h => h.id === target.id).length > 0)[0];
    const newProjects: Backend.ProjectResource[] = this._projects.filter(p => p.project.id !== project.project.id);
    
    const demoTargetHead = target as DemoHead;    
    const demoHead = project.heads['main'] as DemoHead;
    const toMerge = demoTargetHead.commits.slice(demoHead.commits.length);
    
    demoHead.commits = [...demoHead.commits, ...toMerge];
    project.states = createProjectHeadState(project.heads);
        
    this._projects = [...newProjects, project];
    this._listeners.onSave(target, "head");
    return target;  
  }
  deleteHead(target: Backend.Head) {
    const newProjects: Backend.ProjectResource[] = []
    for(const project of this._projects) {
      const newHeads: Record<string, Backend.Head> = {};
      for(const head of Object.values(project.heads)){
        if(head.id !== target.id) {
          newHeads[head.name] = head;
        }
      }
      
      const newProject: Backend.ProjectResource = { 
        project: project.project,
        heads: newHeads,
        states: createProjectHeadState(newHeads),
      }
      newProjects.push(newProject);
    }
    
    this._heads = [...this._heads.filter(h => h.head.id !== target.id)];
    this._projects = newProjects;
    
    this._listeners.onDelete(target, "head");
    return target;
  }
}

const createProjectHeadState = (heads: Record<string, Backend.Head>): Record<string, Backend.ProjectHeadState> => {
  const result: Record<string, Backend.ProjectHeadState> = {};
  const main = heads['main'];
  const mainCommits = (main as DemoHead).commits;
  for(const head of Object.values(heads)) {
    const headCommits = (head as DemoHead).commits;
    const diff = headCommits.length - mainCommits.length;
    const commits = Math.abs(diff);
    const type = diff === 0 ? 'same' : (diff > 0 ? 'ahead' : 'behind');
    result[head.name] = { head: head.name, id: head.id, commits, type };
  }
  
  return result;
}

export { InMemoryService };
