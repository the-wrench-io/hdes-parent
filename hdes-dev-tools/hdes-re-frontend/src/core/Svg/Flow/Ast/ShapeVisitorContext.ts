import { Ast } from './Ast';


class ImmutableShapeIndex implements Ast.ShapeVisitorIndex {
  private _total: number;
  private _value: number;
  private _previous?: Ast.Shape<Ast.Node>;
  constructor(value: number, total: number, previous?: Ast.Shape<Ast.Node>) {
    this._value = value;
    this._total = total;
    this._previous = previous;
  }
  get total() {
    return this._total;
  }
  get value() {
    return this._value;
  }
  get previous() {
    return this._previous;
  }
}

class NullVisitorContext implements Ast.ShapeVisitorContext {
  get parent(): Ast.ShapeVisitorContext {
    throw new Error("can't get parent from null node!");    
  }
  get value(): Ast.Node {
    throw new Error("can't get value from null node!");
  }
  get shape(): Ast.Shape<Ast.Node> {
    throw new Error("can't get shape from null node!");
  }
  get shapes(): Ast.Shape<Ast.Node>[] {
    throw new Error("can't get shapes from null node!");
  }
  get index(): Ast.ShapeVisitorIndex {
    throw new Error("can't get shape index from null node!");
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
  getRoot(): Ast.NodeView {
    throw new Error("can't get root from null node!");
  }
}

class AllShapes {
  private _values: Ast.Shape<Ast.Node>[] = [];
  
  constructor(start: Ast.Shape<Ast.Node>) {
    this.addValue(start);
  }
  get values() {
    return this._values;
  }
  addValue(shape: Ast.Shape<Ast.Node>): AllShapes {
    this._values.push(shape);
    return this;
  }
}

class ImmutableShapeVisitorContext implements Ast.ShapeVisitorContext {
  private _parent: Ast.ShapeVisitorContext;
  private _node: Ast.Node;
  private _shape: Ast.Shape<Ast.Node>;
  private _shapes: AllShapes;
  private _index: Ast.ShapeVisitorIndex;
  
  constructor(shape: Ast.Shape<Ast.Node>, index: Ast.ShapeVisitorIndex, node: Ast.Node, shapes: AllShapes, parent: Ast.ShapeVisitorContext) {
    this._parent = parent;
    this._shapes = shapes; 
    this._node = node;
    this._shape = shape;
    this._index = index;
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
  get index() {
    return this._index;
  }
  get shapes(): readonly Ast.Shape<Ast.Node>[] {
    return this._shapes.values;
  }
  getRoot() {
    const root: Ast.NodeView = this.getNode("root").value as Ast.NodeView;
    return root;
  }
  addNode(node: Ast.Node, shape: Ast.Shape<Ast.Node>, index?: Ast.ShapeVisitorIndex): Ast.ShapeVisitorContext {
    return new ImmutableShapeVisitorContext(shape, index? index : new ImmutableShapeIndex(0, 1), node, this._shapes.addValue(shape), this);
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

const createContext = (shape: Ast.Shape<Ast.Node>, node: Ast.NodeView) => new ImmutableShapeVisitorContext(
  shape, new ImmutableShapeIndex(0, 1), node,
  new AllShapes(shape), 
  new NullVisitorContext());

export {ImmutableShapeIndex};
export default createContext;

