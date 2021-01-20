import Backend from './../Backend'; 
 

export class GenericUserBuilder implements Backend.UserBuilder {
  private _id?: string; 
  private _name?: string; 
  private _token?: string; 
  private _externalId?: string;
  private _groups: string[];
  private _projects: string[] 
  
  constructor(
    id?: string, token?: string, 
    name?: string, externalId?: string, 
    groups?: string[], projects?: string[]) {

    this._id = id;
    this._token = token;
    this._name = name;
    this._externalId = externalId;
    this._groups = groups ? groups : [];
    this._projects =  projects ? projects : [];
  }
  get id(): string | undefined {
    return this._id;
  }
  get name(): string | undefined {
    return this._name;
  }
  get token(): string | undefined {
    return this._token;
  }
  get externalId(): string | undefined {
    return this._externalId;
  }
  get projects(): string[] {
    return this._projects;
  }
  get groups(): string[] {
    return this._groups;
  }
  get resourceType(): "user" {
    return "user";
  }
  withToken(token: string): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, token, this._name, this._externalId, this._groups, this._projects);
  }
  withName(name: string): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._token, name, this._externalId, this._groups, this._projects);
  }
  withExternalId(externalId: string): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._token, this._name, externalId, this._groups, this._projects);
  }
  withProjects(projects: string[]): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._token, this._name, this._externalId, this._groups, projects);
  }
  withGroups(groups: string[]): Backend.UserBuilder {
    return new GenericUserBuilder(this._id, this._token, this._name, this._externalId, groups, this._projects);
  }
  from(from: Backend.UserBuilder): Backend.UserBuilder {
    return new GenericUserBuilder(from.id, this._token, from.name, from.externalId, Object.assign([], from.groups), Object.assign([], from.projects));
  }
  withResource(from: Backend.UserResource): Backend.UserBuilder {
    return new GenericUserBuilder(from.user.id, this._token, from.user.name, from.user.externalId, Object.keys(from.groups), Object.keys(from.projects));
  }
};