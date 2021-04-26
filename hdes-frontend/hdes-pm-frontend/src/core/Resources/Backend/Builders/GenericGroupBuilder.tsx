import Backend from './../Backend'; 

export class GenericGroupBuilder implements Backend.GroupBuilder {
  private _id?: string; 
  private _name?: string; 
  private _users: string[];
  private _projects: string[]
  private _rev?: string;
  private _type?: Backend.GroupType;
  private _matcher?: string;
  
  constructor(
    id?: string, name?: string, 
    users?: string[], projects?: string[], rev?: string, 
    type?: Backend.GroupType, matcher?: string) {

    this._id = id;
    this._name = name;
    this._users = users ? users : [];
    this._projects =  projects ? projects : [];
    this._rev = rev;
    this._type = type;
    this._matcher = matcher;
  }
  get resourceType(): "group" {
    return "group";
  }
  get id(): string | undefined {
    return this._id;
  }
  get type(): Backend.GroupType | undefined {
    return this._type;
  }
  get matcher(): string | undefined {
    return this._matcher;
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
  withMatcher(matcher: string): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, this._users, this._projects, this._rev, this._type, matcher);
  }
  withType(type: Backend.GroupType): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, this._users, this._projects, this._rev, type, this._matcher);
  }
  withName(name: string): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, name, this._users, this._projects, this._rev, this._type, this._matcher);
  }
  withProjects(projects: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, this._users, projects, this._rev, this._type, this._matcher);
  }
  withUsers(users: string[]): Backend.GroupBuilder {
    return new GenericGroupBuilder(this._id, this._name, users, this._projects, this._rev, this._type, this._matcher);
  }
  from(from: Backend.GroupBuilder): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.id, from.name, Object.assign([], from.users), Object.assign([], from.projects), from.rev, from.type, from.matcher);
  }
  withResource(from: Backend.GroupResource): Backend.GroupBuilder {
    return new GenericGroupBuilder(from.group.id, from.group.name, Object.keys(from.users), Object.keys(from.projects), from.group.rev, from.group.type, from.group.matcher);
  }
  build() {
    return {
      id: this._id,  
      rev: this._rev,
      name: this._name ? this._name : "",
      type: this._type,
      matcher: this._matcher,
      projects: this._projects,
      users: this._users
    };
  }
};