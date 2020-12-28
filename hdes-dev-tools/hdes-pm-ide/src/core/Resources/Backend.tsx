
declare namespace Backend {
  
  interface Service {
    users: UserService
  }
  
  interface UserService {
    query: (args?: {top?: number}) => UserQuery,
    builder: () => UserBuilder
  }
  
  interface UserQuery {
    onSuccess: (handle: (users: User[]) => void) => void;
  }

  interface UserBuilder {
    id?: string; 
    name?: string; 
    externalId?: string;
    groups?: string[];
    projects?: string[];

    withName: (name: string) => UserBuilder;
    withExternalId: (externalId: string) => UserBuilder;
    withProjects: (projects: string[]) => UserBuilder;
    withGroups: (groups: string[]) => UserBuilder;
    from: (from: UserBuilder) => UserBuilder;
  }

  interface User {
    id: string;
    rev: string;
    name: string;
    externalId: string;
    created: Date;
    groups: UserGroup[];
    projects: Project[];
  }
  
  interface UserGroup {
    id: string;
    rev: string;
    name: string;
  }

  interface Project {
    id: string;
    rev: string;
    name: string;
  }
}
export default Backend;