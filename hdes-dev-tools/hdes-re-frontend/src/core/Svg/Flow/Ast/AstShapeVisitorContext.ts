import { Ast } from './Ast';


class NullVisitorContext implements Ast.ShapeVisitorContext {
  get parent(): Ast.ShapeVisitorContext {
    throw new Error("can't get parent from null node!");    
  }
  get value(): Ast.Node {
    throw new Error("can't get value from null node!");
  }
  get shape(): Ast.Shape {
    throw new Error("can't get shape from null node!");
  }
  get shapes(): Ast.Shape[] {
    throw new Error("can't get shapes from null node!");
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
  getRoot(): Ast.RootNode {
    throw new Error("can't get root from null node!");
  }
}

class AllShapes {
  private _values: Ast.Shape[] = [];
  
  constructor(start: Ast.Shape) {
    this.addValue(start);
  }
  get values() {
    return this._values;
  }
  addValue(shape: Ast.Shape): AllShapes {
    this._values.push(shape);
    return this;
  }
}

class ImmutableShapeVisitorContext implements Ast.ShapeVisitorContext {
  private _parent: Ast.ShapeVisitorContext;
  private _node: Ast.Node;
  private _shape: Ast.Shape;
  private _shapes: AllShapes;
  
  constructor(shape: Ast.Shape, node: Ast.Node, shapes: AllShapes, parent: Ast.ShapeVisitorContext) {
    this._parent = parent;
    this._shapes = shapes; 
    this._node = node;
    this._shape = shape;
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
  get shape() {
    return this._shape;
  }
  get shapes(): readonly Ast.Shape[] {
    return this._shapes.values;
  }
  getRoot() {
    const root: Ast.RootNode = this.getNode("root").value as Ast.RootNode;
    return root;
  }
  addNode(node: Ast.Node, shape: Ast.Shape): Ast.ShapeVisitorContext {
    return new ImmutableShapeVisitorContext(shape, node, this._shapes.addValue(shape), this);
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

const createContext = (shape: Ast.Shape, node: Ast.RootNode) => new ImmutableShapeVisitorContext(
  shape, node,
  new AllShapes(shape), 
  new NullVisitorContext());

export default createContext;

