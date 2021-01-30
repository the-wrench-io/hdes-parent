import Backend from './../Backend';
import { Store, ServerStore } from './ServerStore';
import { InMemoryProjectService } from './InMemoryProjectService';
import { InMemoryGroupService } from './InMemoryGroupService';
import { ServerUserService } from './ServerUserService';



class ServerService implements Backend.Service {
  users: Backend.UserService;
  projects: Backend.ProjectService;
  groups: Backend.GroupService;
  config: Backend.ServerConfig;
  listener?: (newService: Backend.Service) => void;
  
  constructor(config: Backend.ServerConfig) {
    this.config = config;
    console.log('creating server service');
    const updateChanges = () => {
      if(this.listener) {
        this.listener(this);
      }
    }
    
    const store = new ServerStore(updateChanges, config);

    this.users = new ServerUserService(store);
    this.projects = new InMemoryProjectService(store);
    this.groups = new InMemoryGroupService(store);
  }

  onUpdate = (listener: (newService: Backend.Service) => void) => {
    this.listener = listener;
  }
}

export { ServerService };
