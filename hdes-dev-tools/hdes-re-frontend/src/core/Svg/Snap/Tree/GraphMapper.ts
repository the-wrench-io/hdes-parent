import { Tree } from './Tree';


class GraphMapper<T> {
  private _resource?: Tree.Node; 
  private _start?: (src: Tree.GraphStart) => T;
  private _end?: (src: Tree.GraphEnd) => T;
  private _switch?: (src: Tree.GraphSwitch) => T;
  private _decision?: (src: Tree.GraphDecision) => T;
  private _decisionLoop?: (src: Tree.GraphDecision) => T;
  private _service?: (src: Tree.GraphService) => T;
  private _serviceLoop?: (src: Tree.GraphService) => T;
  
  constructor(resource: Tree.Node) {
    this._resource = resource;
  }
  start(mapper: (src: Tree.GraphStart) => T) : GraphMapper<T> {
    this._start = mapper;
    return this;
  }
  end(mapper: (src: Tree.GraphEnd) => T) : GraphMapper<T> {
    this._end = mapper;
    return this;
  }
  switch(mapper: (src: Tree.GraphSwitch) => T) : GraphMapper<T> {
    this._switch = mapper;
    return this;
  }
  decision(mapper: (src: Tree.GraphDecision) => T) : GraphMapper<T> {
    this._decision = mapper;
    return this;
  }
  decisionLoop(mapper: (src: Tree.GraphDecision) => T) : GraphMapper<T> {
    this._decisionLoop = mapper;
    return this;
  }
  service(mapper: (src: Tree.GraphService) => T) : GraphMapper<T> {
    this._service = mapper;
    return this;
  }
  serviceLoop(mapper: (src: Tree.GraphService) => T) : GraphMapper<T> {
    this._serviceLoop = mapper;
    return this;
  }
  map(): T {
    let resource = this._resource;
    if(!resource) {
      throw new Error(`Resource undefined!`)
    }

    if(resource.type === "start") {
      if(this._start) {
        return this._start(resource as Tree.GraphStart);        
      }
      throw new Error(`start mapper undefined!`)
    } else if(resource.type === "end") {
      if(this._end) {
        return this._end(resource);
      }
      throw new Error(`end mapper undefined!`)
    } else if(resource.type === "switch") {
      if(this._switch) {
        return this._switch(resource as Tree.GraphSwitch);
      }
      throw new Error(`switch mapper undefined!`)

    } else if(resource.type === "decision") {      
      if(this._decision) {
        return this._decision(resource as Tree.GraphDecision);
      }
      throw new Error(`decision mapper undefined!`)      
    } else if(resource.type === "decision-loop") {      
      if(this._decision && !this._decisionLoop) {
        return this._decision(resource as Tree.GraphDecision);
      }
      if(this._decisionLoop) {
        return this._decisionLoop(resource as Tree.GraphDecision);
      }
      
      throw new Error(`decision mapper undefined!`);
      
    } else if(resource.type === "service") {      
      if(this._service) {
        return this._service(resource as Tree.GraphService);
      }
      throw new Error(`service mapper undefined!`)      
    } else if(resource.type === "service-loop") {      
      if(this._service && !this._serviceLoop) {
        return this._service(resource as Tree.GraphService);
      }
      if(this._serviceLoop) {
        return this._serviceLoop(resource as Tree.GraphService);
      }
      throw new Error(`decision mapper undefined!`)       
    } else {
      throw new Error(`Unknown resource: ${resource}!`)
    }
  }
}


export default GraphMapper;

