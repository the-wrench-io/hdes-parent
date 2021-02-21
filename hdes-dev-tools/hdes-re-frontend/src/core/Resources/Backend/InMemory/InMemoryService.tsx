import Backend from './../Backend';
import createDemoData from './DemoData';



class InMemoryService implements Backend.Service {
  private _snapshots: Backend.SnapshotService;
  private _commits: Backend.CommitService;
  private _projects: Backend.ProjectService;
  private _heads: Backend.HeadService;
  private _listeners: Backend.ServiceListeners;
  private _store: Store;
  
  constructor() {
    console.log('creating demo service');
    const onSave = (saved: Backend.AnyResource) => {
      this.listeners.onSave(saved);
    }
    const onDelete = (deleted: Backend.AnyResource) => {
      this.listeners.onDelete(deleted);
    }
    
    const demoData = createDemoData();
    this._store = new InMemoryStore(onSave, onDelete, demoData.projects, demoData.heads);
    this._snapshots = {};
    this._projects = new InMemoryProjectService(this._store);
    this._heads = new InMemoryHeadService(this._store);
    this._commits = {};
    this._listeners = { 
      onSave: (resource) => console.log("saved resources", resource),
      onDelete: (resource) => console.log("deleted resources", resource),
      onError: (error) => console.error("error", error), 
    };
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
  get snapshots() {
    return this._snapshots;
  }
  get listeners() {
    return this._listeners;
  }
  withListeners(listeners: Backend.ServiceListeners): Backend.Service {
    this._listeners = listeners;
    return this;
  }
}

class InMemoryProjectService implements Backend.ProjectService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    return new InMemoryProjectQuery(this.store);
  }
}

class InMemoryProjectQuery implements Backend.ProjectQuery {
  store: Store;
  
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (projects: Backend.ProjectResource[]) => void) {
    const { store } = this;
    const projects = [...store.projects];
    handle(projects);
  }
}


class InMemoryHeadService implements Backend.HeadService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    return new InMemoryHeadQuery(this.store);
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

class InMemoryHeadQuery implements Backend.HeadQuery {
  store: Store;
  
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (projects: Backend.HeadResource[]) => void) {
    const { store } = this;
    const heads = [...store.heads];
    handle(heads);
  }
}

interface Store {
  projects: Backend.ProjectResource[];
  heads: Backend.HeadResource[];
  deleteHead(head: Backend.Head): Backend.Head;
}

class InMemoryStore implements Store {
  private _projects: Backend.ProjectResource[];
  private _heads: Backend.HeadResource[];
  private _onSave: (resource: Backend.AnyResource) => void;
  private _onDelete: (resource: Backend.AnyResource) => void;
  
  constructor(
    onSave: (resource: Backend.AnyResource) => void,
    onDelete: (resource: Backend.AnyResource) => void, 
    projects: Backend.ProjectResource[],
    heads: Backend.HeadResource[]) {
    
    this._onSave = onSave;
    this._onDelete = onDelete;
    this._projects = projects;
    this._heads = heads;
  }
  
  get projects() {
    return this._projects;
  }
  get heads() {
    return this._heads;
  }
  deleteHead(target: Backend.Head) {
    const newProjects: Backend.ProjectResource[] = []
    for(const project of this._projects) {
      const newHeads: Record<string, Backend.Head> = {};
      const newStates: Record<string, Backend.ProjectHeadState> = {};
      
      for(const head of Object.values(project.heads)){
        if(head.id !== target.id) {
          newHeads[head.name] = head;
        }
      }
      for(const state of Object.values(project.states)){
        if(state.head !== target.id) {
          newStates[state.head] = state;
        }
      }      
      const newProject: Backend.ProjectResource = { 
        project: project.project,
        heads: newHeads,
        states: newStates,
      }
      newProjects.push(newProject);
    }
    
    this._heads = [...this._heads.filter(h => h.head.id != target.id)];
    this._projects = newProjects;
    
    this._onDelete(target);
    return target;
  }
}


export { InMemoryService };
