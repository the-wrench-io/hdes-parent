import Backend from './../Backend'; 


interface Store {
  users: readonly Backend.User[], 
  projects: readonly Backend.Project[],
  access: readonly Backend.Access[],
  groups: readonly Backend.Group[],
  groupUsers: readonly Backend.GroupUser[],
  
  onSave: (resource: Backend.AnyResource) => void;
  
  getAccess: (params: {projectId?: string, userId?: string, groupId?: string}) => Record<string, Backend.Access>;
  getGroupUsers: (groups: Record<string, Backend.Group>) => Record<string, Backend.GroupUser>;
  
  getGroups: (access: Record<string, Backend.Access>) => Record<string, Backend.Group>;
  setGroup: (group: Backend.GroupBuilder) => Backend.GroupResource;
  getGroup: (groupId: string) => Backend.GroupResource;
  addGroup: (builder: Backend.GroupBuilder) => Backend.GroupResource;
  deleteGroup: (project: Backend.GroupResource) => Backend.GroupResource;
  
  getUsers: (access: Record<string, Backend.Access>) => Record<string, Backend.User>;
  setUser: (user: Backend.UserBuilder) => Backend.UserResource;
  getUser: (userId: string) => Backend.UserResource;
  addUser: (builder: Backend.UserBuilder) => Backend.UserResource;
  deleteUser: (project: Backend.UserResource) => Backend.UserResource;
  
  getProjects: (access: Record<string, Backend.Access>) => Record<string, Backend.Project>;
  setProject: (project: Backend.ProjectBuilder) => Backend.ProjectResource;
  getProject: (projectId: string) => Backend.ProjectResource;
  addProject: (builder: Backend.ProjectBuilder) => Backend.ProjectResource;
  deleteProject: (project: Backend.ProjectResource) => Backend.ProjectResource; 
  
  toProjectResource: (project: Backend.Project) => Backend.ProjectResource;
  toUserResource: (user: Backend.User) => Backend.UserResource;
  toGroupResource: (group: Backend.Group) => Backend.GroupResource;
  uuid: () => string;
}

class InMemoryStore implements Store {
  private _users: Backend.User[]; 
  private _projects: Backend.Project[];
  private _access: Backend.Access[];
  private _groups: Backend.Group[];
  private _groupUsers: Backend.GroupUser[];
  private _onSave: (resource: Backend.AnyResource) => void;
  private _onDelete: (resource: Backend.AnyResource) => void;

  constructor(
    onSave: (resource: Backend.AnyResource) => void,
    onDelete: (resource: Backend.AnyResource) => void, 
    users: Backend.User[], 
    projects: Backend.Project[], 
    access: Backend.Access[], 
    groups: Backend.Group[], 
    groupUsers: Backend.GroupUser[]) {
    
    this._users = users;
    this._projects = projects;
    this._access = access;
    this._groupUsers = groupUsers;
    this._groups = groups;
    this._onSave = onSave;
    this._onDelete = onDelete;
  }
  get users(): readonly Backend.User[] { 
    return this._users;
  } 
  get projects(): readonly Backend.Project[] { 
    return this._projects;
  }
  get access(): readonly Backend.Access[] {
    return this._access;
  }
  get groups(): readonly Backend.Group[] {
    return this._groups;
  }
  get groupUsers(): readonly Backend.GroupUser[] {
    return this._groupUsers;
  }
  onSave(resource: Backend.AnyResource) {
    return this._onSave(resource);
  }
  onDelete(resource: Backend.AnyResource) {
    return this._onDelete(resource);
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
    this._groups.splice(index, 1);
    this._groups.push(newState);
    
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
      this._access.splice(index, 1);
    });
    groupUsersToRemove.forEach(a => {
      const index = this.groupUsers.indexOf(a, 0);
      this._groupUsers.splice(index, 1);
    });
    
    
    // add access
    builder.projects
      .filter(p => !prevProjects.includes(p))
      .forEach(projectId => this._access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        groupId, projectId}));

    builder.users
      .filter(p => !prevUsers.includes(p))
      .forEach(userId => this._groupUsers.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(),
        groupId, userId}));
    
    const saved = this.toGroupResource(newState);
    this.onSave(saved);
    return saved;
  }
  
  setUser = (builder: Backend.UserBuilder): Backend.UserResource => {
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
    this._users.splice(index, 1);
    this._users.push(newState);
    
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
      this._access.splice(index, 1);
    });
    groupUsersToRemove.forEach(a => {
      const index = this.groupUsers.indexOf(a, 0);
      this._groupUsers.splice(index, 1);
    });
    
    // add access
    builder.projects
      .filter(p => !prevProjects.includes(p))
      .forEach(projectId => this._access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        userId: userId, 
        projectId: projectId}));

    builder.groups
      .filter(p => !prevGroups.includes(p))
      .forEach(groupId => this._groupUsers.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(),
        groupId: groupId, 
        userId: userId}));
        
    const saved = this.toUserResource(newState);
    this.onSave(saved);
    return saved; 
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
    this._projects.splice(index, 1);
    this._projects.push(newState);
    
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
      this._access.splice(index, 1);
    });
    
    // add access
    builder.users
      .filter(p => !prevUsers.includes(p))
      .forEach(userId => this._access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        userId, 
        projectId}));

    builder.groups
      .filter(p => !prevGroups.includes(p))
      .forEach(groupId => this._access.push({
        id: this.uuid(), rev: this.uuid(), created: new Date(), name: "",
        groupId, projectId}));
        
    const saved = this.toProjectResource(newState);
    this.onSave(saved);
    return saved;  
  }
  addProject(builder: Backend.ProjectBuilder): Backend.ProjectResource {
    // user entry
    const newProject: Backend.Project = {
      id: this.uuid(),
      rev: this.uuid(), 
      name: builder.name ? builder.name : "",
      created: new Date()
    };
    
    // direct access to users
    const newAccess: Backend.Access[] = [];
    if(builder.users) {
      for(let userId of builder.users) {
        newAccess.push({
          id: this.uuid(), 
          rev: this.uuid(), 
          name: "inmemory", 
          projectId: newProject.id,
          userId: userId,
          created: new Date()
        });
      }
    }
    
    // direct access to groups
    if(builder.groups) {
      for(let groupId of builder.groups) {
        newAccess.push({
          id: this.uuid(), 
          rev: this.uuid(), 
          name: "inmemory", 
          projectId: newProject.id,
          groupId: groupId,
          created: new Date()
        });
      }
    }
  
    this._access.push(...newAccess);
    this._projects.push(newProject);
    
    const created = this.getProject(newProject.id);
    this.onSave(created);
    return created;
  }
  addUser(builder: Backend.UserBuilder): Backend.UserResource {
    // user entry
    const newUser: Backend.User = {
      id: this.uuid(),
      rev: this.uuid(),
      status: builder.status ? builder.status: "ENABLED",
      token: builder.token ? builder.token : "",
      name: builder.name ? builder.name : "",
      email: builder.email,
      externalId: builder.externalId, 
      created: new Date()
    };
    
    // direct access to projects
    const newAccess: Backend.Access[] = [];
    if(builder.projects) {
      for(let projectId of builder.projects) {
        newAccess.push({
          id: this.uuid(), 
          rev: this.uuid(), 
          name: "inmemory", 
          projectId: projectId, 
          created: new Date()
        });
      }
    }
    
    // access to groups
    const newGroupUsers: Backend.GroupUser[] = [];
    for(const group of this.groups) {
      if(group.matcher && (
        newUser.name.match(group.matcher) || (
        newUser.email && newUser.email.match(group.matcher)))
      ) {
        newGroupUsers.push({
          id: this.uuid(), 
          rev: this.uuid(), 
          groupId: group.id,
          userId: newUser.id,
          created: new Date()
        });
      }
    }
    if(builder.groups) {
      for(let groupId of builder.groups) {
        newGroupUsers.push({
          id: this.uuid(), 
          rev: this.uuid(), 
          groupId: groupId,
          userId: newUser.id,
          created: new Date()
        });
      }
    }

    this._groupUsers.push(...newGroupUsers);
    this._access.push(...newAccess);
    this._users.push(newUser);
    
    const access = this.getAccess({userId: newUser.id});
    const groups = this.getGroups(access);
    const groupUsers = this.getGroupUsers(groups);
    const projects = this.getProjects(access);
    const created = { user: newUser, access, groups, groupUsers, projects }
    this.onSave(created);
    return created;
  }

  addGroup(builder: Backend.GroupBuilder): Backend.GroupResource {
      // user entry
    const newGroup: Backend.Group = {
      id: this.uuid(),
      rev: this.uuid(),
      type: builder.type ? builder.type : "USER",
      matcher: builder.matcher,
      name: builder.name ? builder.name : "",
      created: new Date()
    };

    const newGroupUsers: Backend.GroupUser[] = [];
    for(let userId of builder.users) {
      newGroupUsers.push({
        id: this.uuid(), 
        rev: this.uuid(), 
        groupId: newGroup.id,
        userId: userId,
        created: new Date()
      });
    }
    
    const newAccess: Backend.Access[] = [];
    for(let projectId of builder.projects) {
      newAccess.push({
        id: this.uuid(), 
        rev: this.uuid(), 
        name: "inmemory", 
        projectId: projectId,
        groupId: newGroup.id,
        created: new Date()
      });
    }

    this._groups.push(newGroup);
    this._access.push(...newAccess);
    this._groupUsers.push(...newGroupUsers);

    const access = this.getAccess({groupId: newGroup.id});
    const projects = this.getProjects(access);
    const groups = this.getGroups(access);
    const groupUsers = this.getGroupUsers(groups);
    const users = this.getUsers(access);
    
    const created = { group: newGroup, access, groupUsers, users, projects };
    this.onSave(created);
    return created;
  }
  
  deleteGroup(group: Backend.GroupResource): Backend.GroupResource {
    this._groups = [...this._groups.filter(g => g.id !== group.group.id)]
    this._groupUsers = [...this._groupUsers.filter(g => g.groupId !== group.group.id)]
    this._access = [...this._access.filter(g => g.groupId !== group.group.id)]
    this.onDelete(group)
    return group;
  }
  deleteUser(user: Backend.UserResource): Backend.UserResource {
    this._users = [...this._users.filter(g => g.id !== user.user.id)]
    this._groupUsers = [...this._groupUsers.filter(g => g.userId !== user.user.id)]
    this._access = [...this._access.filter(g => g.userId !== user.user.id)]
    this.onDelete(user)
    return user;
  }  
  deleteProject(project: Backend.ProjectResource): Backend.ProjectResource {
    this._projects = [...this._projects.filter(g => g.id !== project.project.id)]
    this._access = [...this._access.filter(g => g.projectId !== project.project.id)]
    this.onDelete(project)
    return project;
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
