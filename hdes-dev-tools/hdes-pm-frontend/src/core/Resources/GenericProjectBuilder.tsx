import Backend from './Backend'; 


export class GenericProjectBuilder implements Backend.ProjectBuilder {
  private _id?: string; 
  private _name?: string; 
  private _groups: string[];
  private _users: string[] 
  
  constructor(
    id?: string, name?: string, 
    groups?: string[], users?: string[]) {

    this._id = id;
    this._name = name;
    this._groups = groups ? groups : [];
    this._users =  users ? users : [];
  }
  get name(): string | undefined {
    return this._name;
  }
  withName(name: string): Backend.ProjectBuilder {
    return new GenericProjectBuilder(this._id, name, this._groups, this._users);
  }
  get users(): string[] {
    return this._users;
  }
  withUsers(users: string[]): Backend.ProjectBuilder {
    return new GenericProjectBuilder(this._id, this._name, this._groups, users);
  }
  get groups(): string[] {
    return this._groups;
  }
  withGroups(groups: string[]) {
    return new GenericProjectBuilder(this._id, this._name, groups, this._users);
  }
  from(from: Backend.ProjectBuilder) {
    return new GenericProjectBuilder(from.id, from.name, Object.assign([], from.groups), Object.assign([], from.users));
  }
  withResource(from: Backend.ProjectResource) {
    return new GenericProjectBuilder(from.project.id, from.project.name, Object.keys(from.groups), Object.keys(from.users));
  }
};