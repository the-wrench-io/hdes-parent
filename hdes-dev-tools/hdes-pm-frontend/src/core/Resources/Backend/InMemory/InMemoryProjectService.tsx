import Backend from './../Backend';
import { GenericProjectBuilder } from './../Builders';
import { Store } from './InMemoryStore';


class InMemoryProjectQuery implements Backend.ProjectQuery {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  onSuccess(handle: (users: Backend.ProjectResource[]) => void) {
    const store = this.store;
    const result: Backend.ProjectResource[] = store.projects.map(project => {
      const access = store.getAccess({projectId: project.id});
      const groups = store.getGroups(access);
      const groupUsers = store.getGroupUsers(groups);
      const users = store.getUsers(access);
      return { project, access, groups, groupUsers, users};
      
    });
    handle(result);
  }
}

class InMemoryProjectService implements Backend.ProjectService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query() {
    return new InMemoryProjectQuery(this.store);
  }
  builder(from?: Backend.ProjectResource) {
    const result = new GenericProjectBuilder();
    if(from) {
      result.withResource(from);
    }
    return result;
  }
  save(builder: Backend.ProjectBuilder)  {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.ProjectResource) => void) => {
          
        // user entry
        const newProject: Backend.Project = {
          id: store.uuid(),
          rev: store.uuid(), 
          name: builder.name ? builder.name : "",
          created: new Date()
        };
        
        // direct access to users
        const newAccess: Backend.Access[] = [];
        if(builder.users) {
          for(let userId of builder.users) {
            newAccess.push({
              id: store.uuid(), 
              rev: store.uuid(), 
              name: "inmemory", 
              projectId: newProject.id,
              userId: userId,
              created: new Date()
            });
          }
        }
        
        // direct access to groups
        if(builder.groups) {
          for(let groupId of builder.groups) {
            newAccess.push({
              id: store.uuid(), 
              rev: store.uuid(), 
              name: "inmemory", 
              projectId: newProject.id,
              groupId: groupId,
              created: new Date()
            });
          }
        }
        
        store.access.push(...newAccess);
        store.projects.push(newProject);
        store.setUpdates();
        
        const access = store.getAccess({projectId: newProject.id});
        const groups = store.getGroups(access);
        const groupUsers = store.getGroupUsers(groups);
        const users = store.getUsers(access);
        callback({ project: newProject, access, groups, groupUsers, users }) 
      
        
      }
    }
  }
}


export { InMemoryProjectService };
