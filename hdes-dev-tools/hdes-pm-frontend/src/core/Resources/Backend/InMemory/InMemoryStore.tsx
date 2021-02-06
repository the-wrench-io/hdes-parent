import Backend from './../Backend'; 


interface Store {
  users: Backend.User[], 
  projects: Backend.Project[],
  access: Backend.Access[],
  groups: Backend.Group[],
  groupUsers: Backend.GroupUser[],
  
  onSave: (resource: Backend.AnyResource) => void;
  
  getAccess: (params: {projectId?: string, userId?: string, groupId?: string}) => Record<string, Backend.Access>;
  getGroupUsers: (groups: Record<string, Backend.Group>) => Record<string, Backend.GroupUser>;
  
  getGroups: (access: Record<string, Backend.Access>) => Record<string, Backend.Group>;
  setGroup: (group: Backend.GroupBuilder) => Backend.GroupResource;
  getGroup: (groupId: string) => Backend.GroupResource;
  
  getUsers: (access: Record<string, Backend.Access>) => Record<string, Backend.User>;
  setUser: (user: Backend.UserBuilder) => Backend.UserResource;
  getUser: (userId: string) => Backend.UserResource;
  
  getProjects: (access: Record<string, Backend.Access>) => Record<string, Backend.Project>;
  setProject: (project: Backend.ProjectBuilder) => Backend.ProjectResource;
  getProject: (projectId: string) => Backend.ProjectResource;
  
  toProjectResource: (project: Backend.Project) => Backend.ProjectResource;
  toUserResource: (user: Backend.User) => Backend.UserResource;
  toGroupResource: (group: Backend.Group) => Backend.GroupResource;
  uuid: () => string;
}

class InMemoryStore implements Store {
  users: Backend.User[]; 
  projects: Backend.Project[];
  access: Backend.Access[];
  groups: Backend.Group[];
  groupUsers: Backend.GroupUser[];
  onSave: (resource: Backend.AnyResource) => void;

  constructor(
    onSave: (resource: Backend.AnyResource) => void, 
    users: Backend.User[], 
    projects: Backend.Project[], 
    access: Backend.Access[], 
    groups: Backend.Group[], 
    groupUsers: Backend.GroupUser[]) {
    
    this.users = users;
    this.projects = projects;
    this.access = access;
    this.groupUsers = groupUsers;
    this.groups = groups;
    this.onSave = onSave;
  }
  
  toProjectResource = (project: Backend.Project): Backend.ProjectResource => {
    const access = this.getAccess({projectId: project.id});
    const groups = this.getGroups(access);
    const groupUsers = this.getGroupUsers(groups);
    const users = this.getUsers(access);
    return { project, access, groups, groupUsers, users }
  } 
  toUserResource = (user: Backend.User): Backend.UserResource => {
    const access = this.getAccess({userId: user.id});
    const groups = this.getGroups(access);
    const groupUsers = this.getGroupUsers(groups);
    const projects = this.getProjects(access);
    return { user, access, groups, groupUsers, projects }
  } 
  toGroupResource = (group: Backend.Group): Backend.GroupResource => {
    const access = this.getAccess({groupId: group.id});
    const users = this.getUsers(access);
    const groups: Record<string, Backend.Group> = {};
    groups[group.id] = group;
    const groupUsers = this.getGroupUsers(groups);
    const projects = this.getProjects(access);
    return { group, access, groupUsers, projects, users }
  }
  getProject = (projectId: string): Backend.ProjectResource => {
    const project = this.projects.filter(p => p.id === projectId)[0];
    return this.toProjectResource(project);
  }
  getUser = (userId: string): Backend.UserResource => {
    const user = this.users.filter(p => p.id === userId)[0];
    return this.toUserResource(user);
  }
  getGroup = (groupId: string): Backend.GroupResource => {
    const group = this.groups.filter(p => p.id === groupId)[0];
    return this.toGroupResource(group);
  }
  setGroup = (builder: Backend.GroupBuilder): Backend.GroupResource => {
    const id = builder.id;
    if(!id) {
      throw new Error('User id must be defined');
    }
    
    const groupId: string = id;
    const prevResource = this.getGroup(groupId);
    const prevState = prevResource.group;
    const newState: Backend.Group = {
      id: groupId, rev: this.uuid(), 
      created: prevState.created,
      name: builder.name ? builder.name : prevState.name,
      matcher: builder.matcher,
      type: builder.type ? builder.type : prevState.type,
    };
    const index = this.groups.indexOf(prevState, 0);
    this.groups.splice(index, 1);
    this.groups.push(newState);
    
    const prevProjects = Object.keys(prevResource.projects);
    const prevUsers = Object.keys(prevResource.users);

    // remove access
    const accessToRemove: Backend.Access[] = [];
    const groupUsersToRemove: Backend.GroupUser[] = []; 
    
    // from users
    prevProjects
      .filter(p => !builder.projects.includes(p))
      .map(projectId => this.access
        .filter(a => a.groupId === groupId)
        .filter(a => a.projectId === projectId)
        .forEach(a => accessToRemove.push(a)));

    // from groups
    prevUsers
      .filter(p => !builder.users.includes(p))
      .map(userId => this.groupUsers
        .filter(a => a.groupId === groupId)
        .filter(a => a.userId === userId)
        .forEach(a => groupUsersToRemove.push(a)));

    accessToRemove.forEach(a => {
      const index = this.access.indexOf(a, 0);
      this.access.splice(index, 1);
    });
    groupUsersToRemove.forEach(a => {
      const index = this.groupUsers.indexOf(a, 0);
      this.groupUsers.splice(index, 1);
    });
    
    
    // add access
    builder.projects
      .filter(p => !prevProjects.includes(p))
      .forEach(projectId => this.access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        groupId, projectId}));

    builder.users
      .filter(p => !prevUsers.includes(p))
      .forEach(userId => this.groupUsers.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(),
        groupId, userId}));
        
    return this.toGroupResource(newState); 
  }
  
  setUser = (builder: Backend.UserBuilder): Backend.UserResource => {
            console.log("time to save", builder);
    const id = builder.id;
    if(!id) {
      throw new Error('User id must be defined');
    }
    
    const userId: string = id;
    const prevResource = this.getUser(userId);
    const prevState = prevResource.user;
    const newState: Backend.User = {
      id: userId, rev: this.uuid(), 
      created: prevState.created,
      name: builder.name ? builder.name : prevState.name,
      email: builder.email ? builder.email : prevState.email,
      token: builder.token ? builder.token : prevState.token,
      status: builder.status ? builder.status : prevState.status,
    };
    const index = this.users.indexOf(prevState, 0);
    this.users.splice(index, 1);
    this.users.push(newState);
    
    const prevProjects = Object.keys(prevResource.projects);
    const prevGroups = Object.keys(prevResource.groups);

    // remove access
    const accessToRemove: Backend.Access[] = [];
    const groupUsersToRemove: Backend.GroupUser[] = []; 
    
    // from users
    prevProjects
      .filter(p => !builder.projects.includes(p))
      .map(projectId => this.access
        .filter(a => a.userId === userId)
        .filter(a => a.projectId === projectId)
        .forEach(a => accessToRemove.push(a)));

    // from groups
    prevGroups
      .filter(p => !builder.groups.includes(p))
      .map(groupId => this.groupUsers
        .filter(a => a.groupId === groupId)
        .filter(a => a.userId === userId)
        .forEach(a => groupUsersToRemove.push(a)));

    accessToRemove.forEach(a => {
      const index = this.access.indexOf(a, 0);
      this.access.splice(index, 1);
    });
    groupUsersToRemove.forEach(a => {
      const index = this.groupUsers.indexOf(a, 0);
      this.groupUsers.splice(index, 1);
    });
    
    
    // add access
    builder.projects
      .filter(p => !prevProjects.includes(p))
      .forEach(projectId => this.access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        userId: userId, 
        projectId: projectId}));

    builder.groups
      .filter(p => !prevGroups.includes(p))
      .forEach(groupId => this.groupUsers.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(),
        groupId: groupId, 
        userId: userId}));
        
    return this.toUserResource(newState); 
  }
  
  setProject = (builder: Backend.ProjectBuilder): Backend.ProjectResource => {
    const id = builder.id;
    if(!id) {
      throw new Error('Project id must be defined');
    }
    
    const projectId: string = id;
    
    const prevResource = this.getProject(projectId);
    const prevState = prevResource.project;
    const newState: Backend.Project = {
      id: projectId, rev: this.uuid(), created: prevState.created,
      name: builder.name ? builder.name : prevState.name
    };
    const index = this.projects.indexOf(prevState, 0);
    this.projects.splice(index, 1);
    this.projects.push(newState);
    
    const prevUsers = Object.keys(prevResource.users);
    const prevGroups = Object.keys(prevResource.groups);

    // remove access
    const accessToRemove: Backend.Access[] = []; 
    
    // from users
    prevUsers
      .filter(p => !builder.users.includes(p))
      .map(id => this.access
        .filter(a => a.userId === id)
        .filter(a => a.projectId === id)
        .forEach(a => accessToRemove.push(a)));

    // from groups
    prevGroups
      .filter(p => !builder.groups.includes(p))
      .map(id => this.access
        .filter(a => a.groupId === id)
        .filter(a => a.projectId === projectId)
        .forEach(a => accessToRemove.push(a)));

    accessToRemove.forEach(a => {
      const index = this.access.indexOf(a, 0);
      this.access.splice(index, 1);
    });
    
    // add access
    builder.users
      .filter(p => !prevUsers.includes(p))
      .forEach(userId => this.access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        userId, 
        projectId}));

    builder.groups
      .filter(p => !prevGroups.includes(p))
      .forEach(groupId => this.access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        groupId, projectId}));
        
    return this.toProjectResource(newState); 
  }
  
  uuid = ():string => {
    return "inmemory-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (char) => {
      let random = Math.random() * 16 | 0;
      let value = char === "x" ? random : (random % 4 + 8);
      return value.toString(16)
    });
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

export type { Store };
export { InMemoryStore };
