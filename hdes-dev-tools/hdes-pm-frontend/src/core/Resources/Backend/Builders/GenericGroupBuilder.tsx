import Backend from './../Backend'; 

export class GenericGroupBuilder implements Backend.GroupBuilder {
  private _id?: string; 
  private _name?: string; 
  private _users: string[];
  private _projects: string[]
  private _rev?: string;
  
  constructor(
    id?: string, name?: string, 
    users?: string[], projects?: string[], rev?: string) {

    this._id = id;
    this._name = name;
    this._users = users ? users : [];
    this._projects =  projects ? projects : [];
    this._rev = rev;
  }
  get resourceType(): "group" {
    return "group";
  }
  get id(): string | undefined {
    return this._id;
  }
  get name(): string | undefined {
    return this._name;
  }
  get projects(): string[] {
    return this._projects;
  }
  get users(): string[] {
    return this._users;
  }
  withName(name: string): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, name, this._users, this._projects, this._rev);
  }
  withProjects(projects: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, this._users, projects, this._rev);
  }
  withUsers(users: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, users, this._projects, this._rev);
  }
  from(from: Backend.GroupBuilder): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.id, from.name, Object.assign([], from.users), Object.assign([], from.projects, from.rev));
  }
  withResource(from: Backend.GroupResource): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.group.id, from.group.name, Object.keys(from.users), Object.keys(from.projects), from.group.rev);
  }
  build() {
    return {
      id: this._id,  
      rev: this._rev,
      name: this._name ? this._name : "",
      projects: this._projects,
      users: this._users
    };
  }
};