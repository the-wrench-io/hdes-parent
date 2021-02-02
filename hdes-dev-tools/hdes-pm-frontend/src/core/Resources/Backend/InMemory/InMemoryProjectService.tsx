import Backend from './../Backend';
import { GenericProjectBuilder } from './../Builders';
import { Store } from './InMemoryStore';


class InMemoryProjectQuery implements Backend.ProjectQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.ProjectResource[]) => void) {
    const { store } = this;
    let projects = store.projects.sort((p1, p2) => (p1.created as Date).getTime() - (p2.created as Date).getTime());
    if(this.args && this.args.top) {
      projects = projects.slice(0, this.args.top);
    }
    handle(projects.map(store.toProjectResource));
  }
}

class InMemoryProjectService implements Backend.ProjectService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  query(args?: {top?: number}) {
    return new InMemoryProjectQuery(this.store, args);
  }
  builder(from?: Backend.ProjectResource) {
    const result = new GenericProjectBuilder();
    if(from) {
      return result.withResource(from);
    }
    return result;
  }
  save(builder: Backend.ProjectBuilder) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.ProjectResource) => void) => {
          
        // delete old resources
        if(builder.id) {
          const saved = store.setProject(builder);
          callback(saved)
          store.onSave(saved);
          return; 
        }
                  
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
        
        const created = store.getProject(newProject.id);
        callback(created)
        store.onSave(created); 
      }
    }
  }
}


export { InMemoryProjectService };
