import Backend from './../Backend';
import { ServerStore } from './ServerStore';
import { ServerProjectService } from './ServerProjectService';
import { ServerGroupService } from './ServerGroupService';
import { ServerUserService } from './ServerUserService';


class ServerService implements Backend.Service {
  private _users: Backend.UserService;
  private _projects: Backend.ProjectService;
  private _groups: Backend.GroupService;
  private _listeners: Backend.ServiceListeners;
    
  constructor(config: Backend.ServerConfig) {
    console.log('creating server service', config);
    const onSave = (resource: Backend.AnyResource) => {
      this.listeners.onSave(resource);
    }
    const onError = (error: Backend.ServerError) => {
      this.listeners.onError(error);
    }
    const store = new ServerStore(onSave, onError, config);
    this._users = new ServerUserService(store);
    this._projects = new ServerProjectService(store);
    this._groups = new ServerGroupService(store);
    this._listeners = { 
      onSave: (resource) => console.log("saved resources", resource),
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

export { ServerService };
