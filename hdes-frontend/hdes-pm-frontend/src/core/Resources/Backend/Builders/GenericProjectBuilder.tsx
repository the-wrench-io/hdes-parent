import Backend from './../Backend';  


export class GenericProjectBuilder implements Backend.ProjectBuilder {
  private _id?: string; 
  private _name?: string; 
  private _groups: string[];
  private _users: string[] 
  private _rev?: string;
    
  constructor(
    id?: string, name?: string, 
    groups?: string[], users?: string[],
    rev?: string) {

    this._id = id;
    this._name = name;
    this._groups = groups ? groups : [];
    this._users =  users ? users : [];
    this._rev = rev;
  }
  get id(): string | undefined {
    return this._id;
  }
  get name(): string | undefined {
    return this._name;
  }
  get resourceType(): "project" {
    return "project";
  }
  get users(): string[] {
    return this._users;
  }
  get groups(): string[] {
    return this._groups;
  }
  
  withName(name: string): Backend.ProjectBuilder {
    return new GenericProjectBuilder(this._id, name, this._groups, this._users, this._rev);
  }
  withUsers(users: string[]): Backend.ProjectBuilder {
    return new GenericProjectBuilder(this._id, this._name, this._groups, users, this._rev);
  }
  withGroups(groups: string[]) {
    return new GenericProjectBuilder(this._id, this._name, groups, this._users, this._rev);
  }
  from(from: Backend.ProjectBuilder) {
    return new GenericProjectBuilder(from.id, from.name, Object.assign([], from.groups), Object.assign([], from.users), from.rev);
  }
  withResource(from: Backend.ProjectResource) {
    return new GenericProjectBuilder(from.project.id, from.project.name, Object.keys(from.groups), Object.keys(from.users), from.project.rev);
  }
  build() {
    return {
      id: this._id,  
      rev: this._rev,
      name: this._name ? this._name : "",
      groups: this._groups,
      users: this._users
    };
  }
};