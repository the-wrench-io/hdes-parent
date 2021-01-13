import Backend from './../Backend'; 

export class GenericGroupBuilder implements Backend.GroupBuilder {
  private _id?: string; 
  private _name?: string; 
  private _users: string[];
  private _projects: string[] 
  
  constructor(
    id?: string, name?: string, 
    users?: string[], projects?: string[]) {

    this._id = id;
    this._name = name;
    this._users = users ? users : [];
    this._projects =  projects ? projects : [];
  }
  get id(): string | undefined {
    return this._id;
  }
  get name(): string | undefined {
    return this._name;
  }
  withName(name: string): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, name, this._users, this._projects);
  }
  get projects(): string[] {
    return this._projects;
  }
  withProjects(projects: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, this._users, projects);
  }
  get users(): string[] {
    return this._users;
  }
  withUsers(users: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, users, this._projects);
  }
  from(from: Backend.GroupBuilder): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.id, from.name, Object.assign([], from.users), Object.assign([], from.projects));
  }
  withResource(from: Backend.GroupResource): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.group.id, from.group.name, Object.keys(from.users), Object.keys(from.projects));
  }
};