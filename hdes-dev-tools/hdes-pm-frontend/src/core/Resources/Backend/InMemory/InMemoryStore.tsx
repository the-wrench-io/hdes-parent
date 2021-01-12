import Backend from './../Backend'; 


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
  uuid: () => string;
}

class InMemoryStore implements Store {
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
