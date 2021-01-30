import Backend from './../Backend';
import { GenericUserBuilder } from './../Builders';
import { Store } from './InMemoryStore';


class InMemoryUserQuery implements Backend.UserQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.UserResource[]) => void) {
    const store = this.store;
    let users = store.users.sort((p1, p2) => (p1.created as Date).getTime() - (p2.created as Date).getTime());
    if(this.args && this.args.top) {
      users = users.slice(0, this.args.top);
    }
    const result: Backend.UserResource[] = users.map(user => {    
      const access = store.getAccess({userId: user.id});
      const groups = store.getGroups(access);
      const groupUsers = store.getGroupUsers(groups);
      const projects = store.getProjects(access);
      return { user, access, groups, groupUsers, projects };
    });
    handle(result);
  }
}

class InMemoryUserService implements Backend.UserService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  query(args?: {top?: number}) {
    return new InMemoryUserQuery(this.store, args);
  }
  builder(from?: Backend.UserResource) {
    const result = new GenericUserBuilder();
    if(from) {
      return result.withResource(from);
    }
    return result;
  }
  save(builder: Backend.UserBuilder) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.UserResource) => void) => {
        
        // user entry
        const newUser: Backend.User = {
          id: store.uuid(),
          rev: store.uuid(),
          token: builder.token ? builder.token : "",
          name: builder.name ? builder.name : "",
          email: builder.email ? builder.email : "",
          externalId: builder.externalId, 
          created: new Date()
        };
        
        // direct access to projects
        const newAccess: Backend.Access[] = [];
        if(builder.projects) {
          for(let projectId of builder.projects) {
            newAccess.push({
              id: store.uuid(), 
              rev: store.uuid(), 
              name: "inmemory", 
              projectId: projectId, 
              created: new Date()
            });
          }
        }
        
        // access to groups
        const newGroupUsers: Backend.GroupUser[] = [];
        if(builder.groups) {
          for(let groupId of builder.groups) {
            newGroupUsers.push({
              id: store.uuid(), 
              rev: store.uuid(), 
              groupId: groupId,
              userId: newUser.id,
              created: new Date()
            });
          }
        }

        store.groupUsers.push(...newGroupUsers);
        store.access.push(...newAccess);
        store.users.push(newUser);
        store.setUpdates();
        
        const access = store.getAccess({userId: newUser.id});
        const groups = store.getGroups(access);
        const groupUsers = store.getGroupUsers(groups);
        const projects = store.getProjects(access);
        callback({ user: newUser, access, groups, groupUsers, projects }) 
      }
    }
  }
}

export { InMemoryUserService };
