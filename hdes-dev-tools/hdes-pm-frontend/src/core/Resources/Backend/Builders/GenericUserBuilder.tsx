import Backend from './../Backend'; 
 

export class GenericUserBuilder implements Backend.UserBuilder {
  private _id?: string; 
  private _name?: string; 
  private _externalId?: string;
  private _groups: string[];
  private _projects: string[] 
  
  constructor(
    id?: string, name?: string, externalId?: string, 
    groups?: string[], projects?: string[]) {

    this._id = id;
    this._name = name;
    this._externalId = externalId;
    this._groups = groups ? groups : [];
    this._projects =  projects ? projects : [];
  }
  get name(): string | undefined {
    return this._name;
  }
  withName(name: string): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, name, this._externalId, this._groups, this._projects);
  }
  get externalId(): string | undefined {
    return this._externalId;
  }
  withExternalId(externalId: string): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._name, externalId, this._groups, this._projects);
  }
  get projects(): string[] {
    return this._projects;
  }
  withProjects(projects: string[]): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._name, this._externalId, this._groups, projects);
  }
  get groups(): string[] {
    return this._groups;
  }
  withGroups(groups: string[]): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._name, this._externalId, groups, this._projects);
  }
  from(from: Backend.UserBuilder): Backend.UserBuilder {
    return new GenericUserBuilder(from.id, from.name, from.externalId, Object.assign([], from.groups), Object.assign([], from.projects));
  }
  withResource(from: Backend.UserResource): Backend.UserBuilder {
    return new GenericUserBuilder(from.user.id, from.user.name, from.user.externalId, Object.keys(from.groups), Object.keys(from.projects));
  }
};