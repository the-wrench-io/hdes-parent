import Backend from './../Backend';
import { GenericGroupBuilder } from './../Builders';
import { Store } from './ServerStore';



class InMemoryGroupQuery implements Backend.GroupQuery {
  store: Store;
  args?: {top?: number};
  
  constructor(store: Store, args?: {top?: number}) {
    this.store = store;
    this.args = args;
  }
  
  onSuccess(handle: (users: Backend.GroupResource[]) => void) {

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
    
        //callback({ group: newGroup, access, groupUsers, users, projects }) 
      }
    }
  }
}


export { InMemoryGroupService };
