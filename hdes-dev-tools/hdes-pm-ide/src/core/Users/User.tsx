



export default class User {
  id?: string; 
  name?: string; 
  externalId?: string;
  groups?: string[];
  projects?: string[] 
  
  constructor(id?: string) {
    this.id = id
  }

  withName(name: string) {
    this.name = name;
    return this;
  }
  
  withExternalId(externalId: string) {
    this.externalId = externalId;
    return this;
  }
  
  withProjects(projects: string[]) {
    this.projects = projects;
    return this;
  }
  
  withGroups(groups: string[]) {
    this.groups = groups;
    return this;
  }
  
  from(from: User) {
    this.id = from.id;
    this.name = from.name;
    this.externalId = from.externalId;
    this.groups = Object.assign([], from.groups);
    this.projects = Object.assign([], from.projects);
    return this;
  }
};