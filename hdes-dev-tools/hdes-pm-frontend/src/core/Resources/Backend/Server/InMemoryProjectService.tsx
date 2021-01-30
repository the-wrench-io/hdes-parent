import Backend from './../Backend';
import { GenericProjectBuilder } from './../Builders';
import { Store } from './ServerStore';


class InMemoryProjectQuery implements Backend.ProjectQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.ProjectResource[]) => void) {
    const { store } = this;
    
    //handle(projects.map(store.toProjectResource));
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
     
        //callback(store.getProject(newProject.id))
        
        store.setUpdates(); 
      }
    }
  }
}


export { InMemoryProjectService };
