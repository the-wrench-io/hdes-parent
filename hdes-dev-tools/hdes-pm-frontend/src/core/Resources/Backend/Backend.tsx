
declare namespace Backend {
  
  type AnyResource = ProjectResource | UserResource | GroupResource;
  type AnyBuilder = ProjectBuilder | UserBuilder | GroupBuilder;
  
  interface ResourceBuilder {
    resourceType: 'user' | 'project' | 'group';
  }
  
  interface ServiceCallback<T> {
    onSuccess: (handle: (resource: T) => void) => void; 
  }
  
  interface ServiceListeners {
    onSave: (saved: AnyResource) => void;
    onDelete: (saved: AnyResource) => void;
    onError: (error: ServerError) => void;
  }
  
  interface Service {
    users: UserService;
    projects: ProjectService;
    groups: GroupService;
    listeners: ServiceListeners;
    withListeners: (listeners: ServiceListeners) => Service;
  }

  interface GroupService {
    query: (args?: {top?: number}) => GroupQuery;
    builder: (from?: GroupResource) => GroupBuilder;
    delete: (value: GroupResource) => ServiceCallback<GroupResource>;
    save: (builder: GroupBuilder) => ServiceCallback<GroupResource>;
  }
  
  interface GroupQuery {
    onSuccess: (handle: (groups: GroupResource[]) => void) => void;
  }
  
  interface ProjectService {
    query: (args?: {top?: number}) => ProjectQuery;
    builder: (from?: ProjectResource) => ProjectBuilder;
    delete: (value: ProjectResource) => ServiceCallback<ProjectResource>;
    save: (builder: ProjectBuilder) => ServiceCallback<ProjectResource>;
  }
  
  interface ProjectQuery {
    onSuccess: (handle: (projects: ProjectResource[]) => void) => void;
  }
  
  interface UserService {
    query: (args?: {top?: number, id?: string}) => UserQuery;
    builder: (from?: UserResource) => UserBuilder;
    delete: (value: UserResource) => ServiceCallback<UserResource>;
    save: (builder: UserBuilder) => ServiceCallback<UserResource>;
  }
  
  interface UserQuery {
    onSuccess: (handle: (users: UserResource[]) => void) => void;
  }
  
  interface ProjectResource {
    project: Project;
    users: Record<string, User>;
    access: Record<string, Access>;
    groups: Record<string, Group>;
    groupUsers: Record<string, GroupUser>;
  }
  
  interface ProjectBuilder extends ResourceBuilder {
    id?: string;
    rev?: string;
    name?: string; 
    groups: string[];
    users: string[];

    withResource: (from: ProjectResource) => ProjectBuilder;
    withName: (name: string) => ProjectBuilder;
    withUsers: (users: string[]) => ProjectBuilder;
    withGroups: (groups: string[]) => ProjectBuilder;
    from: (from: ProjectBuilder) => ProjectBuilder;
    build: () => {
      id?: string;  
      rev?: string;
      name: string;
      groups: string[];
      users: string[] };
  }
  
  interface GroupResource {
    group: Group;
    users: Record<string, User>;
    access: Record<string, Access>;
    groupUsers: Record<string, GroupUser>;
    projects: Record<string, Project>;
  }
  
  interface GroupBuilder extends ResourceBuilder {
    id?: string; 
    rev?: string;
    name?: string; 
    matcher?: string; 
    type?: GroupType;
    projects: string[];
    users: string[];

    withResource: (from: GroupResource) => GroupBuilder;
    withName: (name: string) => GroupBuilder;
    withMatcher: (matcher: string) => GroupBuilder;
    withType: (type: GroupType) => GroupBuilder;
    withUsers: (users: string[]) => GroupBuilder;
    withProjects: (projectes: string[]) => GroupBuilder;
    from: (from: GroupBuilder) => GroupBuilder;
    build: () => {
      id?: string;  
      rev?: string;
      name: string;
      type?: GroupType;
      matcher?: string; 
      projects: string[];
      users: string[] };
  }
  
  
  interface UserResource {
    user: User;
    projects: Record<string, Project>;
    access: Record<string, Project>;
    groups: Record<string, Group>;
    groupUsers: Record<string, GroupUser>;
  }
  

  interface UserBuilder extends ResourceBuilder {
    id?: string; 
    name?: string;
    email?: string; 
    rev?: string;
    externalId?: string;
    token?: string;
    status?: UserStatus;
    groups: string[];
    projects: string[];

    withEmail: (email: string) => UserBuilder;
    withResource: (from: UserResource) => UserBuilder;
    withName: (name: string) => UserBuilder;
    withExternalId: (externalId: string) => UserBuilder;
    withToken: (token: string) => UserBuilder;
    withProjects: (projects: string[]) => UserBuilder;
    withGroups: (groups: string[]) => UserBuilder;
    withStatus: (status: Backend.UserStatus) => Backend.UserBuilder;
    from: (from: UserBuilder) => UserBuilder;
    build: () => {
      id?: string;  
      rev?: string;
      externalId?: string;
      name: string;
      email?: string;
      status?: UserStatus;
      groups: string[];
      projects: string[] };
  }
  
  type UserStatus = "PENDING" | "ENABLED" | "DISABLED";
  type GroupType = "USER" | "ADMIN";
  
  interface User {
    id: string;
    rev: string;
    name: string;
    token: string;
    status: UserStatus;
    externalId?: string;
    email?: string;
    created: Date | number[];
  }
  
  interface Access {
    id: string;
    rev: string;
    created: Date | number[];
    name: string;
    projectId: string;
    groupId?: string;
    userId?: string;
  }

  interface Group {
    id: string;
    rev: string;
    created: Date | number[];
    type: GroupType;
    matcher?: string; 
    name: string;
  }

  interface GroupUser {
    id: string;
    rev: string;
    created: Date | number[];
    userId: string;
    groupId: string;
  }

  interface Project {
    id: string;
    rev: string;
    created: Date | number[];
    name: string;
  }
  
  interface ServerConfig {
    ctx: string;
    users: string;
    projects: string;
    groups: string;
    headers: {}
  }
  
  interface ServerError {
    id: string,
    messages: { code: string, value: string}[]
  }
}
export default Backend;