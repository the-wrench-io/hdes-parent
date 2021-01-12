import Backend from './../Backend';
import { GenericGroupBuilder } from './../Builders';
import { Store } from './InMemoryStore';



class InMemoryGroupQuery implements Backend.GroupQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.GroupResource[]) => void) {
    const store = this.store;
    const result: Backend.GroupResource[] = this.store.groups.map(group => {
    
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
  query() {
    return new InMemoryGroupQuery(this.store);
  }
  builder(from?: Backend.GroupResource) {
    const result = new GenericGroupBuilder();
    if(from) {
      result.withResource(from);
    }
    return result;
  }
  save(builder: Backend.GroupBuilder)  {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
          
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
        store.setUpdates();
        
        const access = store.getAccess({groupId: newGroup.id});
        const projects = store.getProjects(access);
        const groups = store.getGroups(access);
        const groupUsers = store.getGroupUsers(groups);
        const users = store.getUsers(access);
        callback({ group: newGroup, access, groupUsers, users, projects }) 
      }
    }
  }
}


export { InMemoryGroupService };