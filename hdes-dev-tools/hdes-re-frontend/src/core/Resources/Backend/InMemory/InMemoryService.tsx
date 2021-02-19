import Backend from './../Backend';
import createDemoData from './DemoData';



class InMemoryService implements Backend.Service {
  private _snapshots: Backend.SnapshotService;
  private _commits: Backend.CommitService;
  private _projects: Backend.ProjectService;
  private _listeners: Backend.ServiceListeners;
  
  constructor() {
    console.log('creating demo service');
    const onSave = (saved: Backend.Commit) => {
      this.listeners.onSave(saved);
    }
    const onDelete = (deleted: Backend.Commit) => {
      this.listeners.onDelete(deleted);
    }
    const demo = createDemoData();
    this._snapshots = {};
    this._projects = {};
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

export { InMemoryService };
