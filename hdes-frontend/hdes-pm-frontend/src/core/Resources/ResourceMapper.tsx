import { Backend } from './Backend';


const isProject = (resource: any) : resource is Backend.ProjectResource => {
  return resource.project !== undefined;
}
const isUser = (resource: any) : resource is Backend.UserResource => {
  return resource.user !== undefined;
}
const isGroup = (resource: any) : resource is Backend.GroupResource => {
  return resource.group !== undefined;
}


class ResourceMapper<T> {
  private _resource?: Backend.AnyResource; 
  private _userMapper?: (src: Backend.UserResource) => T;
  private _groupMapper?: (src: Backend.GroupResource) => T;
  private _projectMapper?: (src: Backend.ProjectResource) => T;
  
  constructor(resource?: Backend.AnyResource) {
    this._resource = resource;
  }
  user(mapper: (src: Backend.UserResource) => T) : ResourceMapper<T> {
    this._userMapper = mapper;
    return this;
  }
  group(mapper: (src: Backend.GroupResource) => T) : ResourceMapper<T> {
    this._groupMapper = mapper;
    return this;
  }
  project(mapper: (src: Backend.ProjectResource) => T) : ResourceMapper<T> {
    this._projectMapper = mapper;
    return this;
  }
  map(): T {
    let resource = this._resource;
    if(!resource) {
      throw new Error(`Resource undefined!`)
    }

    if(isProject(resource)) {
      if(this._projectMapper) {
        return this._projectMapper(resource);        
      }
      throw new Error(`projectMapper undefined!`)
    } else if(isGroup(resource)) {
      if(this._groupMapper) {
        return this._groupMapper(resource);
      }
      throw new Error(`groupMapper undefined!`)
    } else if(isUser(resource)) {
      if(this._userMapper) {
        return this._userMapper(resource);
      }
      throw new Error(`userMapper undefined!`)
    } else {
      throw new Error(`Unknown resource: ${resource}!`)
    }
  }
}


export { ResourceMapper };

