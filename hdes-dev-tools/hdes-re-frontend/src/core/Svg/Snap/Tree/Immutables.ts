import { Tree } from './';

class ImmutableShape<T extends Tree.Node> implements Tree.Shape<T> {
  private _node: T; 
  private _dimensions: Tree.Dimensions; 
  private _connectors: Tree.Connectors;
  private _associations: Tree.Associations;
    
  constructor(props: {node: T, dimensions: Tree.Dimensions, connectors: Tree.Connectors, associations: Tree.Associations}) {
    this._node = props.node;
    this._dimensions = props.dimensions;
    this._connectors = props.connectors;
    this._associations = props.associations;
  }
  get id() {
    return this._node.id;
  }
  get node() {
    return this._node;
  }
  get dimensions() {
    return this._dimensions;
  }
  get connectors() {
    return this._connectors;
  }
  get associations() {
    return this._associations;
  }
}

class ImmutableOrder implements Tree.Order {
  private _value: number;
  
  constructor(value?: number) {
    this._value = value ? value : 0;
  }
  get value() {
    return this._value;
  }
}

class ImmutableAssociations implements Tree.Associations {
  private _incoming: string[];
  private _outgoing: string[];
  
  constructor(props?: {
    incoming?: string[];
    outgoing?: string[];
  }) {
    this._incoming = props?.incoming ? props.incoming : [];
    this._outgoing = props?.outgoing ? props.outgoing : [];
  }  
  get incoming() {
    return this._incoming;
  }
  get outgoing() {
    return this._outgoing;
  }
}

class ImmutableTypography implements Tree.Typography {
  private _name: string;
  private _desc?: string;
  private _icon?: string;

  constructor(props?: {
    name: string;
    desc?: string;
    icon?: string;
  }) {
    this._name = props?.name ? props.name : "";
    this._desc = props?.desc;
    this._icon = props?.icon;
  }
  get name() {
    return this._name;
  }
  get desc() {
    return this._desc;
  }
  get icon() {
    return this._icon;
  } 
}

class ImmutableCoordinates implements Tree.Coordinates {
  private _x: number; 
  private _y: number;
  constructor(x: number, y: number) {
    this._x = Math.round(x);
    this._y = Math.round(y);
  }  
  get x() {
    return this._x;
  }
  get y() {
    return this._y;
  }
}

class ImmutableDimensions implements Tree.Dimensions {
  private _height: number; 
  private _width: number; 
  
  constructor(props: {width: number, height: number}) {
    this._height = Math.round(props.height);
    this._width = Math.round(props.width);
  }

  get height(): number {
    return this._height;
  }
  get width(): number {
    return this._width;
  }
}

class ImmutableConnectors implements Tree.Connectors { 
  private _center: Tree.Coordinates;
  private _left: Tree.Coordinates;
  private _right: Tree.Coordinates;
  private _top: Tree.Coordinates; 
  private _bottom: Tree.Coordinates;
  
  constructor(size: Tree.Dimensions, start: Tree.Coordinates) {
    const rx = Math.round(size.width/2);
    const ry = Math.round(size.height/2);
    this._center = new ImmutableCoordinates(start.x, start.y + ry);
    this._left =   new ImmutableCoordinates(this._center.x-rx, this._center.y);
    this._right =  new ImmutableCoordinates(this._center.x+rx, this._center.y);
    this._top =    new ImmutableCoordinates(this._center.x,    this._center.y - ry);
    this._bottom = new ImmutableCoordinates(this._center.x,    this._center.y + ry);
  } 
  get center() {
    return this._center;
  }
  get left() {
    return this._left;
  }
  get right() {
    return this._right;
  }
  get top() {
    return this._top;
  }
  get bottom() {
    return this._bottom;
  }
}

class ImmutableGridShapes implements Tree.GridShapes {
  private _rows:    Record<string, Tree.Shape<Tree.GridRow>>;
  private _headers: Record<string, Tree.Shape<Tree.GridHeader>>;
  private _cells:   Record<string, Tree.Shape<Tree.GridCell>>;
  private _nodes:   Record<string, Tree.Node>;
  private _id: string;
  private _typography: Tree.Typography;
  private _associations: Tree.Associations;
  private _order = new ImmutableOrder(0);
  constructor(
    id: string,
    rows:     Record<string, Tree.Shape<Tree.GridRow>>, 
    headers:  Record<string, Tree.Shape<Tree.GridHeader>>, 
    cells:    Record<string, Tree.Shape<Tree.GridCell>>,
    nodes:    Record<string, Tree.Node>,
    associations?: Tree.Associations) {
    
    this._id = id;
    this._rows = rows;
    this._nodes = nodes;
    this._headers = headers;
    this._cells = cells;
    this._typography = new ImmutableTypography({name: id});
    this._associations = associations ? associations : new ImmutableAssociations(); 
  }
  get rows() {
    return Object.values(this._rows);
  }
  get headers() {
    return Object.values(this._headers);
  }
  get cells() {
    return Object.values(this._cells);
  }
  get nodes() {
    return this._nodes;
  }
  get shapes() {
    return Object.assign({}, this._rows, this._cells, this._headers);
  }
  get id() {
    return this._id; 
  }
  get type(): "grid" {
    return "grid";
  }
  get typography() {
    return this._typography;
  }
  get associations() {
    return this._associations;
  }
  get order() {
    return this._order
  }
  getById(id: string) {
    if(this._rows[id]) {
      return this._rows[id];
    }
    if(this._headers[id]) {
      return this._headers[id];
    }
    if(this._cells[id]) {
      return this._cells[id];
    }
    throw new Error(`Shape not found by id: '${id}'!`)
  }
}
class ImmutableShapes implements Tree.Shape<Tree.Shapes> {
  private _id: string; 
  private _node: Tree.Shapes;
  private _dimensions: Tree.Dimensions;
  private _connectors: Tree.Connectors;
  private _associations = new ImmutableAssociations();
    
  constructor(node: Tree.Shapes, start: Tree.Coordinates, connectors?: Tree.Connectors) {
    this._id = "root";
    this._node = node;
    this._dimensions = {height: 0, width: 0};
    this._connectors = connectors ? connectors : new ImmutableConnectors(this._dimensions, start);
  }  
  get id() {
    return this._id;
  } 
  get node() {
    return this._node;
  }
  get dimensions() {
    return this._dimensions;
  }
  get connectors() {
    return this._connectors; 
  }
  get associations() {
    return this._associations;
  }
}

class TemplateNode implements Tree.Node {
  private _id: string;
  private _type: Tree.NodeType;
  private _typography: Tree.Typography;
  private _order: Tree.Order;

  constructor(props: {
      id: string;
      typography?: Tree.Typography;
      order?: number;
    }, 
    type: Tree.NodeType) {
    
    this._id = props.id;
    this._type = type;
    this._order = new ImmutableOrder(props.order);
    this._typography = new ImmutableTypography(props.typography);
  }
  get id() {
    return this._id;
  }
  get type() {
    return this._type;
  }
  get typography() {
    return this._typography;
  }
  get order() {
    return this._order;
  }
}

class ImmutableGridHeader extends TemplateNode implements Tree.GridHeader {
  private _kind: Tree.GridHeaderKind;
  constructor(props: {
    id: string,
    order: number;
    kind: Tree.GridHeaderKind,
    typography: Tree.Typography}) {
      
    super(props, "header");
    this._kind = props.kind;
  }
  get kind() {
    return this._kind;
  }
}


class ImmutableGridCell extends TemplateNode implements Tree.GridCell {
  private _headerId: string;
  private _rowId: string;

  constructor(props: {
    id: string,
    order: number;
    typography: Tree.Typography
    headerId: string,
    rowId: string
  }) {
      
    super(props, "cell");
    this._headerId = props.headerId;
    this._rowId = props.rowId;
  }
  get rowId() {
    return this._rowId;
  }
  get headerId() {
    return this._headerId;
  }
}

class ImmutableGridRow extends TemplateNode implements Tree.GridRow {
  private _cells: readonly Tree.GridCell[];
  constructor(props: {
    id: string,
    order: number,
    typography: Tree.Typography,
    cells: Tree.GridCell[]}) {
      
    super(props, "row");
    this._cells = props.cells;
  }
  get cells() {
    return this._cells;
  }
}

class ImmutableGraphChild implements Tree.GraphChild {
  private _id: string;
  private _typography: Tree.Typography;
  constructor(id: string, typography: Tree.Typography) {
    this._id = id;
    this._typography = new ImmutableTypography(typography);
  }
  get id() {
    return this._id;
  }
  get typography() {
    return this._typography;
  }
}

class ImmutableGraphStart extends TemplateNode implements Tree.GraphStart {
  private _children: Tree.GraphChild;

  constructor(props: {
    id: string,
    typography: Tree.Typography,
    children: Tree.GraphChild
  }) {
      
    super(props, "start");
    this._children = props.children;
  }
  get children() {
    return this._children;
  } 
}
class ImmutableGraphEnd extends TemplateNode implements Tree.GraphEnd {
  constructor(props: {
    id: string,
    typography: Tree.Typography
  }) {
    super(props, "end");
  }
}
class ImmutableGraphSwitch extends TemplateNode implements Tree.GraphSwitch {
  private _children: Tree.GraphChild[];

  constructor(
    props: {
      id: string,
      typography: Tree.Typography,
      children: Tree.GraphChild[]
    }) {
      
    super(props, "switch");
    this._children = props.children;
  }
  get children() {
    return this._children;
  }
}
class ImmutableGraphDecision extends TemplateNode implements Tree.GraphDecision {
  private _children: Tree.GraphChild;
  private _loop?: Tree.GraphChild;
  
  constructor(
    props: {
      id: string,
      typography: Tree.Typography,
      children: Tree.GraphChild
    }, 
    loop?: Tree.GraphChild) {
      
    super(props, loop ? "decision-loop" : "decision");
    this._children = props.children;
    this._loop = loop;
  }
  get children() {
    return this._children;
  }
  get loop() {
    return this._loop;
  }
}
class ImmutableGraphService extends TemplateNode implements Tree.GraphService {
  private _children: Tree.GraphChild;
  private _loop?: Tree.GraphChild;
  private _async: boolean;
  constructor(
    props: {
      id: string,
      typography: Tree.Typography,
      children: Tree.GraphChild
    }, 
    loop?: Tree.GraphChild,
    async?: boolean) {
      
    super(props, loop ? "service-loop" : "service");
    this._children = props.children;
    this._loop = loop;
    this._async = async ? async : false;
  }
  get children() {
    return this._children;
  }
  get loop() {
    return this._loop;
  }
  get async() {
    return this._async;
  }
}


class ImmutableGraphShapes implements Tree.GraphShapes {
  private _children: Record<string, Tree.Shape<Tree.Node>> = {};
  private _nodes: Record<string, Tree.Node> = {};
  private _start: Tree.GraphStart;
  private _id: string;
  private _typography: Tree.Typography;
  private _end: Tree.GraphEnd;
  private _order = new ImmutableOrder(0);
  private _associations: Tree.Associations;  
    
  constructor(props: {
    id: string, start: Tree.GraphStart, end: Tree.GraphEnd, 
    children: Record<string, Tree.Shape<Tree.Node>>,
    nodes: Record<string, Tree.Node>}) {
    
    this._id = props.id;
    this._start = props.start;
    this._end = props.end;
    this._children = props.children;
    this._nodes = props.nodes;
    this._typography = new ImmutableTypography({name: props.id});
    this._associations = new ImmutableAssociations();
  }
  get start() {
    return this._start;
  }
  get shapes() {
    return this._children;
  }
  get nodes() {
    return this._nodes;
  }
  get id() {
    return this._id; 
  }
  get end() {
    return this._end; 
  }
  get type(): "grid" {
    return "grid";
  }
  get typography() {
    return this._typography;
  }
  get associations() {
    return this._associations;
  }
  get order() {
    return this._order
  }
  getById(id: string) {
    if(this._children[id]) {
      return this._children[id];
    }
    throw new Error(`Shape not found by id: '${id}'!`)
  }
}

  
class ImmutableVisitorIndex implements Tree.VisitorIndex {
  private _value: number; 
  private _total: number;
  private _previous?: Tree.Node;
  
  constructor(props: {value: number, total: number, previous?: Tree.Node}) {
    this._value = props.value;
    this._total = props.total;
    this._previous = props.previous;
  }
  get value() {
    return this._value;
  }
  get total() {
    return this._total;
  }
  get previous(): Tree.Node | undefined {
    return this._previous;
  }
}


/*

private _all: Record<string, Tree.Node>;
    parent: VisitorContext;
    index: VisitorIndex;
    type: "nullNode" | NodeType;
    
    coords: Coordinates;
    node: Node;
    nodes: readonly Node[];
    getRoot(): Shapes;
    addNode(node: Node, coords: Coordinates, index?: VisitorIndex): VisitorContext; 
    getNode(type: NodeType): VisitorContext;
*/

class ImmutableNullTypeVisitorContext implements Tree.VisitorContext {
  private _all: Record<string, Tree.Node>;
  private _connectors: Tree.Connectors;
  constructor(all: Record<string, Tree.Node>, coords: Tree.Coordinates) {
    this._all = all;
    this._connectors = new ImmutableConnectors({height: 0, width: 0}, coords);
  }
  get all(): Record<string, Tree.Node> {
    return this._all;
  }
  get type(): "nullNode" {
    return "nullNode";
  }
  get connectors(): Tree.Connectors {
    return this._connectors;
  }
  addNode(node: Tree.Node, connectors: Tree.Connectors, index?: Tree.VisitorIndex): Tree.VisitorContext {
    const nodeIndex = index? index : new ImmutableVisitorIndex({value: 0, total: 1});
    return new ImmutableVisitorContext(this, nodeIndex, connectors, node);
  }
  get parent(): Tree.VisitorContext {
    throw new Error("can't get parent from null node!");    
  }
  get node(): Tree.Node {
    throw new Error("can't get node from null node!");
  }
  get root(): Tree.VisitorContext {
    throw new Error("can't get root from null node!");
  }
  get index(): Tree.VisitorIndex {
    throw new Error("can't get index from null node!");
  }
  getNode(): Tree.VisitorContext {
    throw new Error("can't find from null node!");
  }
}

class ImmutableVisitorContext implements Tree.VisitorContext {
  private _parent: Tree.VisitorContext;
  private _index: Tree.VisitorIndex;
  private _connectors: Tree.Connectors;
  private _node: Tree.Node;
  
  constructor(
    parent: Tree.VisitorContext,
    index: Tree.VisitorIndex,
    connectors: Tree.Connectors,
    node: Tree.Node) {
    
    this._parent = parent;
    this._index = index;
    this._connectors = connectors;
    this._node = node;
  }
  get parent(): Tree.VisitorContext {
    return this._parent;
  }
  get node(): Tree.Node {
    return this._node;
  }
  get type() {
    return this._node.type;
  }
  get connectors() {
    return this._connectors;
  }
  get all() {
    let iterator: Tree.VisitorContext = this;
    while(iterator.type !== "nullNode") {
      iterator = iterator.parent;
    }
    return iterator.all;
  }
  get index() {
    return this._index;
  }
  get root(): Tree.VisitorContext {
    let iterator: Tree.VisitorContext = this;
    while(iterator.type !== "nullNode") {
      if(iterator.parent.type === "nullNode") {
        return iterator;
      }
      iterator = iterator.parent;
    }
    throw new Error("can't find root node")
  }
  addNode(node: Tree.Node, connectors: Tree.Connectors, index?: Tree.VisitorIndex): Tree.VisitorContext {
    const nodeIndex = index? index : new ImmutableVisitorIndex({value: 0, total: 1});
    return new ImmutableVisitorContext(this, nodeIndex, connectors, node);
  }
  getNode(type: Tree.NodeType): Tree.VisitorContext {
    let iterator: Tree.VisitorContext = this;
    
    while(iterator) {
      if(iterator.node.type === type) {
        return iterator;
      }
      iterator = iterator.parent;
    }
    throw new Error("can't find node of type: '"+ type +"'")
  }
}


const Immutables = {
  GridShapes: ImmutableGridShapes,
  GridHeader: ImmutableGridHeader,
  GridCell: ImmutableGridCell,
  GridRow: ImmutableGridRow,
  
  GraphStart: ImmutableGraphStart,
  GraphEnd: ImmutableGraphEnd,
  GraphShapes: ImmutableGraphShapes,
  GraphService: ImmutableGraphService,
  GraphDecision: ImmutableGraphDecision,
  GraphSwitch: ImmutableGraphSwitch,
  GraphChild: ImmutableGraphChild,
  
  Context: ImmutableNullTypeVisitorContext,
  Coordinates: ImmutableCoordinates,
  Order: ImmutableOrder,
  Connectors: ImmutableConnectors,
  Dimensions: ImmutableDimensions,
  Typography: ImmutableTypography,
  Associations: ImmutableAssociations,
  Shapes: ImmutableShapes,
  Shape: ImmutableShape
};

export default Immutables;

