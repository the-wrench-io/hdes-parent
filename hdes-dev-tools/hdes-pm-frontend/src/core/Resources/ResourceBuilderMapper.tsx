import { Backend } from './Backend';


const isProject = (resource: Backend.AnyBuilder) : resource is Backend.ProjectBuilder => {
  return resource.resourceType === "project";
}
const isUser = (resource: Backend.AnyBuilder) : resource is Backend.UserBuilder => {
  return resource.resourceType === "user";
}
const isGroup = (resource: Backend.AnyBuilder) : resource is Backend.GroupBuilder => {
  return resource.resourceType === "group";
}


class ResourceBuilderMapper<T> {
  private _resource?: Backend.AnyBuilder; 
  private _userMapper?: (src: Backend.UserBuilder) => T;
  private _groupMapper?: (src: Backend.GroupBuilder) => T;
  private _projectMapper?: (src: Backend.ProjectBuilder) => T;
  
  constructor(resource?: Backend.AnyBuilder) {
    this._resource = resource;
  }
  user(mapper: (src: Backend.UserBuilder) => T) : ResourceBuilderMapper<T> {
    this._userMapper = mapper;
    return this;
  }
  group(mapper: (src: Backend.GroupBuilder) => T) : ResourceBuilderMapper<T> {
    this._groupMapper = mapper;
    return this;
  }
  project(mapper: (src: Backend.ProjectBuilder) => T) : ResourceBuilderMapper<T> {
    this._projectMapper = mapper;
    return this;
  }
  map(): T {
    let resource = this._resource;
    if(!resource) {
      throw new Error(`Builder undefined!`)
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


export { ResourceBuilderMapper };

