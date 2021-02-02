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
    
    let src = store.groups.sort((p1, p2) => (p1.created as Date).getTime() - (p2.created as Date).getTime());
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
  save(builder: Backend.GroupBuilder)  {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
        
        if(builder.id) {
          const saved = store.setGroup(builder);
          callback(saved)
          store.onSave(saved);
          return; 
        }
        
        // user entry
        const newGroup: Backend.Group = {
          id: store.uuid(),
          rev: store.uuid(), 
          name: builder.name ? builder.name : "",
          created: new Date()
        };


        const newGroupUsers: Backend.GroupUser[] = [];
        for(let userId of builder.users) {
          newGroupUsers.push({
            id: store.uuid(), 
            rev: store.uuid(), 
            groupId: newGroup.id,
            userId: userId,
            created: new Date()
          });
        }
        
        const newAccess: Backend.Access[] = [];
        for(let projectId of builder.projects) {
          newAccess.push({
            id: store.uuid(), 
            rev: store.uuid(), 
            name: "inmemory", 
            projectId: projectId,
            groupId: newGroup.id,
            created: new Date()
          });
        }
      

        
        store.groups.push(newGroup);
        store.access.push(...newAccess);
        store.groupUsers.push(...newGroupUsers);

        
        const access = store.getAccess({groupId: newGroup.id});
        const projects = store.getProjects(access);
        const groups = store.getGroups(access);
        const groupUsers = store.getGroupUsers(groups);
        const users = store.getUsers(access);
        
        const created = { group: newGroup, access, groupUsers, users, projects };
        callback(created)
        store.onSave(created);
         
      }
    }
  }
}


export { InMemoryGroupService };
