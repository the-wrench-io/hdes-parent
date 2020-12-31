import Backend from './Backend';
import { GenericUserBuilder } from './ResourceBuilders';
import cteateDemoData from './DemoData';

interface Store {
  users: Backend.User[], 
  projects: Backend.Project[],
  access: Backend.Access[],
  groups: Backend.Group[],
  groupUsers: Backend.GroupUser[],
  
  getAccess: (params: {projectId?: string, userId?: string}) => Record<string, Backend.Access>,
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

  constructor(users: Backend.User[], projects: Backend.Project[], access: Backend.Access[], groups: Backend.Group[], groupUsers: Backend.GroupUser[]) {
    this.users = users;
    this.projects = projects;
    this.access = access;
    this.groupUsers = groupUsers;
    this.groups = groups;
  }

  getAccess = (params: {projectId?: string, userId?: string}): Record<string, Backend.Access> => {
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
  builder() {
    return new GenericUserBuilder();
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


class InMemoryService implements Backend.Service {
  users: Backend.UserService;
  projects: Backend.ProjectService;
  store: Store;
  
  constructor() {
    const demo = cteateDemoData();
    this.store = new DefaultStore(demo.users, demo.projects, demo.access, demo.groups, demo.groupUsers);
    this.users = new InMemoryUserService(this.store);
    this.projects = new InMemoryProjectService(this.store);
  }
}

export default InMemoryService;
