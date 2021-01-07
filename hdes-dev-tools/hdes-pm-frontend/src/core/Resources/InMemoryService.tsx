import Backend from './Backend';
import { GenericUserBuilder } from './ResourceBuilders';
import createDemoData from './DemoData';


const uuid = ():string => {
  return "inmemory-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (char) => {
    let random = Math.random() * 16 | 0;
    let value = char === "x" ? random : (random % 4 + 8);
    return value.toString(16)
  });
}

interface Store {
  users: Backend.User[], 
  projects: Backend.Project[],
  access: Backend.Access[],
  groups: Backend.Group[],
  groupUsers: Backend.GroupUser[],
  
  setUpdates: () => void;
  getAccess: (params: {projectId?: string, userId?: string, groupId?: string}) => Record<string, Backend.Access>,
  getGroups: (access: Record<string, Backend.Access>) => Record<string, Backend.Group>
  getGroupUsers: (groups: Record<string, Backend.Group>) => Record<string, Backend.GroupUser>
  getUsers: (access: Record<string, Backend.Access>) => Record<string, Backend.User>
  getProjects: (access: Record<string, Backend.Access>) => Record<string, Backend.Project>
}

class DefaultStore implements Store {
  users: Backend.User[]; 
  projects: Backend.Project[];
  access: Backend.Access[];
  groups: Backend.Group[];
  groupUsers: Backend.GroupUser[];
  setUpdates: () => void;

  constructor(setUpdates: () => void, users: Backend.User[], projects: Backend.Project[], access: Backend.Access[], groups: Backend.Group[], groupUsers: Backend.GroupUser[]) {
    this.users = users;
    this.projects = projects;
    this.access = access;
    this.groupUsers = groupUsers;
    this.groups = groups;
    this.setUpdates = setUpdates;
  }

  getAccess = (params: {projectId?: string, userId?: string, groupId?: string}): Record<string, Backend.Access> => {
    const result:Record<string, Backend.Access> = {}
    if(params.projectId) {
      this.access
        .filter(access => access.projectId === params.projectId)
        .forEach(access => result[access.id] = access)
    }
    if(params.userId) {
      this.access
        .filter(access => access.userId === params.userId)
        .forEach(access => result[access.id] = access)
        
      const groupsIds = this.groupUsers
        .filter(g => g.userId === params.userId)
        .map(g => g.groupId)
      this.access
        .filter(access => access.groupId && groupsIds.includes(access.groupId))
        .forEach(access => result[access.id] = access)    
    }
    if(params.groupId) {
      this.access
        .filter(access => access.groupId === params.groupId)
        .forEach(access => result[access.id] = access)
    }
    return result;
  }
  
  getGroups = (access: Record<string, Backend.Access>): Record<string, Backend.Group> => {
    const ids = Object.values(access)
      .filter(a => a.groupId).map(groupAccess => groupAccess.groupId);
  
    const result:Record<string, Backend.Group> = {}
    this.groups
      .filter(g => ids.includes(g.id))
      .map(g => result[g.id] = g);
    return result;
  }
  
  getGroupUsers = (groups: Record<string, Backend.Group>): Record<string, Backend.GroupUser> => {
    const ids = Object.keys(groups);
  
    const result:Record<string, Backend.GroupUser> = {}
    this.groupUsers
      .filter(g => ids.includes(g.id))
      .map(g => result[g.id] = g);
    return result;
  }
  
  getUsers = (access: Record<string, Backend.Access>): Record<string, Backend.User> => {
    const ids: string[] = [];
    Object.values(access)
      .forEach(a => a.userId ? ids.push(a.userId) : "");
      
    const groupIds = Object.keys(this.getGroups(access));
    this.groupUsers.filter(a => groupIds.includes(a.groupId))
      .forEach(a => ids.push(a.userId));  

    const result:Record<string, Backend.User> = {}
    this.users
      .filter(g => ids.includes(g.id))
      .map(g => result[g.id] = g);
    return result;
  }
  
  getProjects = (access: Record<string, Backend.Access>): Record<string, Backend.Project> => {
    const ids = Object.values(access).map(a => a.projectId);

    const result:Record<string, Backend.Project> = {}
    this.projects
      .filter(g => ids.includes(g.id))
      .map(g => result[g.id] = g);
    return result;
  }
}

class InMemoryUserQuery implements Backend.UserQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.UserResource[]) => void) {
    const store = this.store;
    const result: Backend.UserResource[] = this.store.users.map(user => {    
      const access = store.getAccess({userId: user.id});
      const groups = store.getGroups(access);
      const groupUsers = store.getGroupUsers(groups);
      const projects = store.getProjects(access);
      return { user, access, groups, groupUsers, projects };
    });
    handle(result);
  }
}

class InMemoryProjectQuery implements Backend.ProjectQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.ProjectResource[]) => void) {
    const store = this.store;
    const result: Backend.ProjectResource[] = store.projects.map(project => {
      const access = store.getAccess({projectId: project.id});
      const groups = store.getGroups(access);
      const groupUsers = store.getGroupUsers(groups);
      const users = store.getUsers(access);
      return { project, access, groups, groupUsers, users};
      
    });
    handle(result);
  }
}

class InMemoryUserService implements Backend.UserService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  query() {
    return new InMemoryUserQuery(this.store);
  }
  builder(from?: Backend.UserResource) {
    const result = new GenericUserBuilder();
    if(from) {
      result.withResource(from);
    }
    return result;
  }
  save(builder: Backend.UserBuilder) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.UserResource) => void) => {
        
        // user entry
        const newUser: Backend.User = {
          id: uuid(),
          rev: uuid(), 
          name: builder.name ? builder.name : "",
          externalId: builder.externalId, 
          created: new Date()
        };
        
        // direct access to projects
        const newAccess: Backend.Access[] = [];
        if(builder.projects) {
          for(let projectId of builder.projects) {
            newAccess.push({
              id: uuid(), 
              rev: uuid(), 
              name: "inmemory", 
              projectId: projectId, 
              created: new Date()
            });
          }
        }
        
        // access to groups
        const newGroupUsers: Backend.GroupUser[] = [];
        if(builder.groups) {
          for(let groupId of builder.groups) {
            newGroupUsers.push({
              id: uuid(), 
              rev: uuid(), 
              groupId: groupId,
              userId: newUser.id,
              created: new Date()
            });
          }
        }

        store.groupUsers.push(...newGroupUsers);
        store.access.push(...newAccess);
        store.users.push(newUser);
        store.setUpdates();
        
        const access = store.getAccess({userId: newUser.id});
        const groups = store.getGroups(access);
        const groupUsers = store.getGroupUsers(groups);
        const projects = store.getProjects(access);
        callback({ user: newUser, access, groups, groupUsers, projects }) 
      }
    }
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


class InMemoryGroupQuery implements Backend.GroupQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.GroupResource[]) => void) {
    const store = this.store;
    const result: Backend.GroupResource[] = this.store.groups.map(group => {
    
      const groups:Record<string, Backend.Group> = {};
      groups[group.id] = group;
    
      const access = store.getAccess({groupId: group.id});
      const users = store.getUsers(access);
      const groupUsers = store.getGroupUsers(groups);
      const projects = store.getProjects(access);
      return { group, users, access, groupUsers, projects };

    }); 
    handle(result);
  }
}

class InMemoryGroupService implements Backend.GroupService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    return new InMemoryGroupQuery(this.store);
  }
}



class DemoService implements Backend.Service {
  users: Backend.UserService;
  projects: Backend.ProjectService;
  groups: Backend.GroupService;
  store: Store;
  listener?: (newService: Backend.Service) => void;
  
  constructor() {
    const updateChanges = () => {
      if(this.listener) {
        this.listener(new InMemoryService(new Date(), this.store, this.listener));
      }
    }
    const demo = createDemoData();
    this.store = new DefaultStore(updateChanges, demo.users, demo.projects, demo.access, demo.groups, demo.groupUsers);
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
