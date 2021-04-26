import Backend from './../Backend';
import { GenericGroupBuilder } from './../Builders';
import { Store } from './InMemoryStore';



class InMemoryGroupQuery implements Backend.GroupQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.GroupResource[]) => void) {
    const store = this.store;
    
    let src = [...store.groups].sort((p1, p2) => (p1.created as Date).getTime() - (p2.created as Date).getTime());
    if(this.args && this.args.top) {
      src = src.slice(0, this.args.top);
    }
    const result: Backend.GroupResource[] = src.map(group => {
      const groups:Record<string, Backend.Group> = {};
      groups[group.id] = group;
  
      const access = store.getAccess({groupId: group.id});
      const users = store.getUsers(access);
      const groupUsers = store.getGroupUsers(groups);
      const projects = store.getProjects(access);
      return { group, users, access, groupUsers, projects };

    }); 
    handle(result);
  }
}

class InMemoryGroupService implements Backend.GroupService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query(args?: {top?: number}) {
    return new InMemoryGroupQuery(this.store, args);
  }
  builder(from?: Backend.GroupResource) {
    const result = new GenericGroupBuilder();
    if(from) {
      return result.withResource(from);
    }
    return result;
  }  
  delete(group: Backend.GroupResource): Backend.ServiceCallback<Backend.GroupResource> {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
        callback(store.deleteGroup(group))
      }
    }
  }
  save(builder: Backend.GroupBuilder)  {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
        if(builder.id) {
          const saved = store.setGroup(builder);
          callback(saved)
          return; 
        }
        callback(store.addGroup(builder))         
      }
    }
  }
}


export { InMemoryGroupService };
