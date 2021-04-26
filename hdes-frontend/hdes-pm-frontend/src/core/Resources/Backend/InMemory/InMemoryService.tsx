import Backend from './../Backend';
import { Store, InMemoryStore } from './InMemoryStore';
import { InMemoryProjectService } from './InMemoryProjectService';
import { InMemoryGroupService } from './InMemoryGroupService';
import { InMemoryUserService } from './InMemoryUserService';
import createDemoData from './DemoData';



class DemoService implements Backend.Service {
  private _users: Backend.UserService;
  private _projects: Backend.ProjectService;
  private _groups: Backend.GroupService;
  private _store: Store;
  private _listeners: Backend.ServiceListeners;
  
  constructor() {
    console.log('creating demo service');
    const onSave = (saved: Backend.AnyResource) => {
      this.listeners.onSave(saved);
    }
    const onDelete = (deleted: Backend.AnyResource) => {
      this.listeners.onDelete(deleted);
    }
    const demo = createDemoData();
    this._store = new InMemoryStore(onSave, onDelete, demo.users, demo.projects, demo.access, demo.groups, demo.groupUsers);
    this._users = new InMemoryUserService(this._store);
    this._projects = new InMemoryProjectService(this._store);
    this._groups = new InMemoryGroupService(this._store);
    this._listeners = { 
      onSave: (resource) => console.log("saved resources", resource),
      onDelete: (resource) => console.log("deleted resources", resource),
      onError: (error) => console.error("error", error), 
    };
  }

  get users() {
    return this._users;
  }
  get projects() {
    return this._projects;
  }
  get groups() {
    return this._groups;
  }
  get listeners() {
    return this._listeners;
  }
  withListeners(listeners: Backend.ServiceListeners) {
    this._listeners = listeners;
    return this;
  }
}

export { DemoService };
