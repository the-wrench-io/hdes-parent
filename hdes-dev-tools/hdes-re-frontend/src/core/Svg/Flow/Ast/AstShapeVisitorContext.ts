import { Ast } from './Ast';


class NullVisitorContext implements Ast.ShapeVisitorContext {
  get parent(): Ast.ShapeVisitorContext {
    throw new Error("can't get parent from null node!");    
  }
  get value(): Ast.Node {
    throw new Error("can't get value from null node!");
  }
  get type(): "nullNode" {
    return "nullNode";
  }
  addNode(): Ast.ShapeVisitorContext {
    throw new Error("can't add to null node!");
  }
  getNode(): Ast.ShapeVisitorContext {
    throw new Error("can't find from null node!");
  }
}

class ImmutableShapeVisitorContext implements Ast.ShapeVisitorContext {
  private _parent: Ast.ShapeVisitorContext;
  private _node: Ast.Node;
  
  constructor(node: Ast.Node, parent?: Ast.ShapeVisitorContext) {
    this._parent = parent ? parent : new NullVisitorContext(); 
    this._node = node;
  }
  
  get parent(): Ast.ShapeVisitorContext {
    return this._parent;
  }
  get value(): Ast.Node {
    return this._node;
  }
  get type() {
    return this._node.type;
  }
  addNode(node: Ast.Node): Ast.ShapeVisitorContext {
    return new ImmutableShapeVisitorContext(node, this);
  }
  getNode(type: Ast.NodeType): Ast.ShapeVisitorContext {
    let iterator: Ast.ShapeVisitorContext = this;
    
    while(iterator.type !== "nullNode") {
      if(iterator.value.type === type) {
        return iterator;
      }
      iterator = iterator.parent;
    }
    throw new Error("can't find node of type: '"+ type +"'")
  }
}

const createContext = (node: Ast.RootNode) => new ImmutableShapeVisitorContext(node);

export default createContext;

