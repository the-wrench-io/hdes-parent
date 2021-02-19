import Backend from './../Backend';
import createDemoData from './DemoData';



class InMemoryService implements Backend.Service {
  private _snapshots: Backend.SnapshotService;
  private _commits: Backend.CommitService;
  private _projects: Backend.ProjectService;
  private _listeners: Backend.ServiceListeners;
  private _store: Store;
  
  constructor() {
    console.log('creating demo service');
    const onSave = (saved: Backend.Commit) => {
      this.listeners.onSave(saved);
    }
    const onDelete = (deleted: Backend.Commit) => {
      this.listeners.onDelete(deleted);
    }
    
    const demoData = createDemoData();
    this._store = new InMemoryStore(demoData.projects);
    this._snapshots = {};
    this._projects = new InMemoryProjectService(this._store);
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


interface Store {
  projects: Backend.ProjectResource[];
}

class InMemoryStore implements Store {
  private _projects: Backend.ProjectResource[];
  
  constructor(projects: Backend.ProjectResource[]) {
    this._projects = projects;
  }
  
  get projects() {
    return this._projects;
  }
}


export { InMemoryService };
