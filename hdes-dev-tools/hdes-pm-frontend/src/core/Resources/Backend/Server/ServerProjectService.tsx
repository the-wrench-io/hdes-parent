import Backend from './../Backend';
import { GenericProjectBuilder } from './../Builders';
import { Store } from './ServerStore';


class ServerProjectQuery implements Backend.ProjectQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.ProjectResource[]) => void) {
    const store = this.store;
    const init: RequestInit = { method: 'GET', credentials: 'same-origin', headers: store.config.headers }
    const url: string = store.config.projects
    store.fetch<Backend.ProjectResource[]>(url, init).then(handle)
  }
}

class ServerProjectService implements Backend.ProjectService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  query(args?: {top?: number}) {
    return new ServerProjectQuery(this.store, args);
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
        const init: RequestInit = { 
          method: 'POST', 
          credentials: 'same-origin', 
          headers: store.config.headers,
          body: JSON.stringify(builder.build())
        };
          
        const url: string = store.config.projects
        store.fetch<Backend.ProjectResource>(url, init)
          .then(callback)
      }
    }
  }
}

export { ServerProjectService };
