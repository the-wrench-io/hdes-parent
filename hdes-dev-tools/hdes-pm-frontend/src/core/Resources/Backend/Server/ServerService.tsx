import Backend from './../Backend';
import { Store, ServerStore } from './ServerStore';
import { InMemoryProjectService } from './InMemoryProjectService';
import { InMemoryGroupService } from './InMemoryGroupService';
import { ServerUserService } from './ServerUserService';



class ServerService implements Backend.Service {
  private _users: Backend.UserService;
  private _projects: Backend.ProjectService;
  private _groups: Backend.GroupService;
  private _config: Backend.ServerConfig;
  private _listeners: Backend.ServiceListeners;
    
  constructor(config: Backend.ServerConfig) {
    console.log('creating server service', config);
    const updateChanges = () => {
      this.listeners.onSave();
    }
    this._config = config;    
    const store = new ServerStore(updateChanges, config);
    this._users = new ServerUserService(store);
    this._projects = new InMemoryProjectService(store);
    this._groups = new InMemoryGroupService(store);
    this._listeners = { onSave: () => console.log("saved resources") };
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

export { ServerService };
