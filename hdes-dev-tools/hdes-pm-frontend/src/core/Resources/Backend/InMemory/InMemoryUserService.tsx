import Backend from './../Backend';
import { GenericUserBuilder } from './../Builders';
import { Store } from './InMemoryStore';


class InMemoryUserQuery implements Backend.UserQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.UserResource[]) => void) {
    const store = this.store;
    const result: Backend.UserResource[] = this.store.users.map(user => {    
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
  
  query() {
    return new InMemoryUserQuery(this.store);
  }
  builder(from?: Backend.UserResource) {
    const result = new GenericUserBuilder();
    if(from) {
      result.withResource(from);
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
          name: builder.name ? builder.name : "",
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
