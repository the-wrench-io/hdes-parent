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
    let users = [...store.users].sort((p1, p2) => (p1.created as Date).getTime() - (p2.created as Date).getTime());
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
  delete(user: Backend.UserResource): Backend.ServiceCallback<Backend.UserResource> {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.UserResource) => void) => {
        callback(store.deleteUser(user))
      }
    }
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
        
        if(builder.id) {
          const saved = store.setUser(builder);
          callback(saved)
          return;
        }
        callback(store.addUser(builder))
      }
    }
  }
}

export { InMemoryUserService };
