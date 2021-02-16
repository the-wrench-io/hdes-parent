import Backend from './../Backend';
import { GenericGroupBuilder } from './../Builders';
import { Store } from './ServerStore';


class ServerGroupQuery implements Backend.GroupQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.GroupResource[]) => void) {
    const store = this.store;
    const init: RequestInit = { method: 'GET', credentials: 'same-origin', headers: store.config.headers }
    const url: string = store.config.groups
    store.fetch<Backend.GroupResource[]>(url, init).then(handle)
  }
}

class ServerGroupService implements Backend.GroupService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  query(args?: {top?: number}) {
    return new ServerGroupQuery(this.store, args);
  }
  builder(from?: Backend.GroupResource) {
    const result = new GenericGroupBuilder();
    if(from) {
      return result.withResource(from);
    }
    return result;
  }
  delete(resource: Backend.GroupResource) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
        const init: RequestInit = { 
          method: 'DELETE',
          credentials: 'same-origin', 
          headers: store.config.headers,
          body: JSON.stringify(resource)
        };
          
        const url: string = store.config.groups
        store.fetch<Backend.GroupResource>(url, init).then(callback)
      }
    }
  }
  save(builder: Backend.GroupBuilder) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.GroupResource) => void) => {
        const init: RequestInit = { 
          method: builder.id ? 'PUT' : 'POST',
          credentials: 'same-origin', 
          headers: store.config.headers,
          body: JSON.stringify(builder.build())
        };
          
        const url: string = store.config.groups
        store.fetch<Backend.GroupResource>(url, init).then(callback)
      }
    }
  }
}

export { ServerGroupService };
