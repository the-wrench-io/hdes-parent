import Backend from './Backend'; 


export class GenericUserBuilder implements Backend.UserBuilder {
  id?: string; 
  name?: string; 
  externalId?: string;
  groups: string[];
  projects: string[] 
  
  constructor(id?: string) {
    this.id = id
    this.groups = [];
    this.projects = [];
  }

  as(): Backend.UserBuilder {
    const result = this as unknown as Backend.UserBuilder;
    return result;
  }
  withName(name: string): Backend.UserBuilder {
    this.name = name;
    return this.as();
  }
  withExternalId(externalId: string) {
    this.externalId = externalId;
    return this.as();
  }
  withProjects(projects: string[]) {
    this.projects = projects;
    return this.as();
  }
  withGroups(groups: string[]) {
    this.groups = groups;
    return this.as();
  }
  from(from: Backend.UserBuilder) {
    this.id = from.id;
    this.name = from.name;
    this.externalId = from.externalId;
    this.groups = Object.assign([], from.groups);
    this.projects = Object.assign([], from.projects);
    return this.as();
  }
  withResource(from: Backend.UserResource) {
    this.id = from.user.id;
    this.name = from.user.name;
    this.externalId = from.user.externalId;
    this.groups = Object.keys(from.groups);
    this.projects = Object.keys(from.projects);
    return this.as();
  }
};