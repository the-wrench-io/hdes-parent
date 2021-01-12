import Backend from './../Backend';
import { Store, InMemoryStore } from './InMemoryStore';
import { InMemoryProjectService } from './InMemoryProjectService';
import { InMemoryGroupService } from './InMemoryGroupService';
import { InMemoryUserService } from './InMemoryUserService';
import createDemoData from './DemoData';



class DemoService implements Backend.Service {
  users: Backend.UserService;
  projects: Backend.ProjectService;
  groups: Backend.GroupService;
  store: Store;
  listener?: (newService: Backend.Service) => void;
  
  constructor() {
    console.log('creating demo service');
    const updateChanges = () => {
      if(this.listener) {
        this.listener(new InMemoryService(new Date(), this.store, this.listener));
      }
    }
    const demo = createDemoData();
    this.store = new InMemoryStore(updateChanges, demo.users, demo.projects, demo.access, demo.groups, demo.groupUsers);
    this.users = new InMemoryUserService(this.store);
    this.projects = new InMemoryProjectService(this.store);
    this.groups = new InMemoryGroupService(this.store);
  }

  onUpdate = (listener: (newService: Backend.Service) => void) => {
    this.listener = listener;
  }
}

class InMemoryService implements Backend.Service {
  users: Backend.UserService;
  projects: Backend.ProjectService;
  groups: Backend.GroupService;
  store: Store;
  listener: (newService: Backend.Service) => void;
  updatedAt: Date;
  
  constructor(updatedAt: Date, store: Store, listener: (newService: Backend.Service) => void) {
    console.log('creating in-memory service');
    this.listener = listener;
    this.store = store;
    this.users = new InMemoryUserService(this.store);
    this.projects = new InMemoryProjectService(this.store);
    this.groups = new InMemoryGroupService(this.store);
    this.updatedAt = updatedAt;
  }

  onUpdate = (listener: (newService: Backend.Service) => void) => {
    this.listener = listener;
  }
}

export { DemoService, InMemoryService };
