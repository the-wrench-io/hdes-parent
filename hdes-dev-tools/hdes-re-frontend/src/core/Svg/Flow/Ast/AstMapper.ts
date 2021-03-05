import { Ast } from './Ast';


class AstMapper<T> {
  private _resource?: Ast.Node; 
  private _start?: (src: Ast.StartNode) => T;
  private _end?: (src: Ast.EndNode) => T;
  private _switch?: (src: Ast.SwitchNode) => T;
  private _decision?: (src: Ast.DecisionNode) => T;
  private _decisionLoop?: (src: Ast.DecisionNode) => T;
  private _service?: (src: Ast.ServiceNode) => T;
  private _serviceLoop?: (src: Ast.ServiceNode) => T;
  
  constructor(resource: Ast.Node) {
    this._resource = resource;
  }
  start(mapper: (src: Ast.StartNode) => T) : AstMapper<T> {
    this._start = mapper;
    return this;
  }
  end(mapper: (src: Ast.EndNode) => T) : AstMapper<T> {
    this._end = mapper;
    return this;
  }
  switch(mapper: (src: Ast.SwitchNode) => T) : AstMapper<T> {
    this._switch = mapper;
    return this;
  }
  decision(mapper: (src: Ast.DecisionNode) => T) : AstMapper<T> {
    this._decision = mapper;
    return this;
  }
  decisionLoop(mapper: (src: Ast.DecisionNode) => T) : AstMapper<T> {
    this._decisionLoop = mapper;
    return this;
  }
  service(mapper: (src: Ast.ServiceNode) => T) : AstMapper<T> {
    this._service = mapper;
    return this;
  }
  serviceLoop(mapper: (src: Ast.ServiceNode) => T) : AstMapper<T> {
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
        return this._start(resource as Ast.StartNode);        
      }
      throw new Error(`start mapper undefined!`)
    } else if(resource.type === "end") {
      if(this._end) {
        return this._end(resource);
      }
      throw new Error(`end mapper undefined!`)
    } else if(resource.type === "switch") {
      if(this._switch) {
        return this._switch(resource as Ast.SwitchNode);
      }
      throw new Error(`switch mapper undefined!`)

    } else if(resource.type === "decision") {      
      if(this._decision) {
        return this._decision(resource as Ast.DecisionNode);
      }
      throw new Error(`decision mapper undefined!`)      
    } else if(resource.type === "decision-loop") {      
      if(this._decision && !this._decisionLoop) {
        return this._decision(resource as Ast.DecisionNode);
      }
      if(this._decisionLoop) {
        return this._decisionLoop(resource as Ast.DecisionNode);
      }
      
      throw new Error(`decision mapper undefined!`);
      
    } else if(resource.type === "service") {      
      if(this._service) {
        return this._service(resource as Ast.ServiceNode);
      }
      throw new Error(`service mapper undefined!`)      
    } else if(resource.type === "service-loop") {      
      if(this._service && !this._serviceLoop) {
        return this._service(resource as Ast.ServiceNode);
      }
      if(this._serviceLoop) {
        return this._serviceLoop(resource as Ast.ServiceNode);
      }
      throw new Error(`decision mapper undefined!`)       
    } else {
      throw new Error(`Unknown resource: ${resource}!`)
    }
  }
}


export default AstMapper;

