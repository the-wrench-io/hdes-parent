import Backend from './../Backend';
import { GenericUserBuilder } from './../Builders';
import { Store } from './ServerStore';


class ServerUserQuery implements Backend.UserQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.UserResource[]) => void) {
    const store = this.store;
    const init: RequestInit = { method: 'GET', credentials: 'same-origin', headers: store.config.headers }
    const url: string = store.config.users
    store.fetch<Backend.UserResource[]>(url, init).then(handle)
  }
}

class ServerUserService implements Backend.UserService {
  store: Store;
  constructor(store: Store) {
    this.store = store;
  }
  
  query(args?: {top?: number}) {
    return new ServerUserQuery(this.store, args);
  }
  builder(from?: Backend.UserResource) {
    const result = new GenericUserBuilder();
    if(from) {
      return result.withResource(from);
    }
    return result;
  }
  delete(resource: Backend.UserResource) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.UserResource) => void) => {
        const init: RequestInit = { 
          method: 'DELETE',
          credentials: 'same-origin', 
          headers: store.config.headers,
          body: JSON.stringify(resource)
        };
          
        const url: string = store.config.users
        store.fetch<Backend.UserResource>(url, init).then(callback)
      }
    }
  }
  save(builder: Backend.UserBuilder) {
    const store = this.store;
    return {
      onSuccess: (callback: (resource: Backend.UserResource) => void) => {
        const init: RequestInit = { 
          method: builder.id ? 'PUT' : 'POST',
          credentials: 'same-origin', 
          headers: store.config.headers,
          body: JSON.stringify(builder.build())
        };
          
        const url: string = store.config.users
        store.fetch<Backend.UserResource>(url, init)
          .then(callback)
      }
    }
  }
}

export { ServerUserService };
