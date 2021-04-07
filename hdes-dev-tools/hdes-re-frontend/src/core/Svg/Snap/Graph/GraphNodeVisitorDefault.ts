import { Tree, Immutables, GraphMapper } from '../Tree'


interface Visited {
  first: Tree.VisitorContext;
  last: Tree.VisitorContext;
}

interface VisitorInit {
  id: string, 
  coords: Tree.Coordinates,
  sy: number, sx: number;
  mx: number; 
}

class GraphNodeVisitorDefault implements Tree.GraphNodeVisitor<Tree.GraphShapes, Visited> {
  private _nodes: Record<string, Tree.Node> = {};
  private _assoications: Record<string, Tree.Associations> = {};
  private _dimensions: Record<string, Tree.Dimensions> = {};
  private _connectors: Record<string, Tree.Connectors> = {};
  private _visited: Record<string, Visited> = {};
  private _init: VisitorInit;
  private _start?: Tree.GraphStart;
  private _end?: Tree.GraphEnd;
  
  constructor(init: VisitorInit) {
    this._init = init;
  }
  visit(startId: string, nodes: Record<string, Tree.Node>): Tree.GraphShapes {
    this._nodes = nodes;
    const context = new Immutables.Context(nodes, this._init.coords);
    this.visitNode(nodes[startId], context);
    
    if(!this._start) {
      throw new Error("start is not defined!");
    }
    if(!this._end) {
      throw new Error("end is not defined!");
    }
    
    const children: Record<string, Tree.Shape<Tree.Node>> = {};
    for(const node of Object.values(nodes)) {
      children[node.id] = new Immutables.Shape({
        node: node,
        dimensions: this._dimensions[node.id],
        connectors: this._connectors[node.id],
        associations: this._assoications[node.id]});
    }
    
    return new Immutables.GraphShapes({
      id: this._init.id, 
      start: this._start,
      end: this._end,
      children: children,
      nodes: nodes
    });
  }
  visitNode(node: Tree.Node, context: Tree.VisitorContext): Visited {
    if(this._visited[node.id]) {
      return this._visited[node.id];
    }
    this.visitAssociations(node, context);
    const result = new GraphMapper<Visited>(node)
      .start    (target => this.visitStart(target, context))
      .end      (target => this.visitEnd(target, context))
      .switch   (target => this.visitSwitch(target, context))
      .service  (target => this.visitService(target, context))
      .decision (target => this.visitDecision(target, context))
      .map();
      
    this._visited[node.id] = result;
    return result;
  }
  visitStart(node: Tree.GraphStart, context: Tree.VisitorContext): Visited {
    const dimensions = new Immutables.Dimensions({height: 25, width: 25});
    const connectors = new Immutables.Connectors(dimensions, {center: this._init.coords});
    this._dimensions[node.id] = dimensions;
    this._connectors[node.id] = connectors;
    this._start = node;
    const next = context.addNode(node, connectors);
    const visited = this.visitNode(this._nodes[node.children.id], next);
    return { first: next, last: visited.last };
  }
  visitEnd(node: Tree.GraphEnd, context: Tree.VisitorContext): Visited {
    const center = {x: this._init.coords.x, y: this.visitY(node, context)};
    const dimensions = new Immutables.Dimensions({height: 25, width: 25});
    const connectors = new Immutables.Connectors(dimensions, {center});
    this._dimensions[node.id] = dimensions;
    this._connectors[node.id] = connectors;
    this._end = node;
    const next = context.addNode(node, connectors);
    return { first: next, last: next };
  }
  visitSwitch(node: Tree.GraphSwitch, context: Tree.VisitorContext): Visited {
    const center = {x: this.visitX(node, context), y: this.visitY(node, context)};
    const dimensions = new Immutables.Dimensions({height: 25, width: 25});
    const connectors = new Immutables.Connectors(dimensions, {center});
    this._dimensions[node.id] = dimensions;
    this._connectors[node.id] = connectors;

    const total = node.children.length    

    let previous: Visited | undefined;
    let value = 0;
    for(const child of node.children) {
      const index: Tree.VisitorIndex = { total, value: value++, previous: previous?.first.node };
      const next = context.addNode(node, connectors, index);
      previous = this.visitNode(this._nodes[child.id], next);
    }

    const first = context.addNode(node, connectors, { total, value: 0});
    return { first: first, last: previous ? previous.last : first };
  }
  visitDecision(node: Tree.GraphDecision, context: Tree.VisitorContext): Visited {
    const dimensions = new Immutables.Dimensions({height: 50, width: 100});
    const center = { x: this.visitX(node, context), y: this.visitY(node, context) };
    const connectors = new Immutables.Connectors(dimensions, {center});
    
    this._dimensions[node.id] = dimensions;
    this._connectors[node.id] = connectors;
    
    const next = context.addNode(node, connectors);
    const visited = this.visitNode(this._nodes[node.children.id], next);
    return { first: next, last: visited.last };
  }
  visitService(node: Tree.GraphService, context: Tree.VisitorContext): Visited {
    const dimensions = new Immutables.Dimensions({height: 50, width: 100});
    const center = { x: this.visitX(node, context), y: this.visitY(node, context) };
    const connectors = new Immutables.Connectors(dimensions, {center});
    
    this._dimensions[node.id] = dimensions;
    this._connectors[node.id] = connectors;
    
    const next = context.addNode(node, connectors);
    const visited = this.visitNode(this._nodes[node.children.id], next);
    return { first: next, last: visited.last };
  }
  visitLoop(node: Tree.GraphDecision | Tree.GraphService, context: Tree.VisitorContext): Visited {
    throw new Error("loop not implemented!");
  }
  visitX(node: Tree.Node, context: Tree.VisitorContext): number {
    if(context.index.total > 1) {
      return this.visitXN(node, context);
    }
    return context.connectors.center.x;
  }
  visitY(node: Tree.Node, context: Tree.VisitorContext): number {
    return context.connectors.bottom.y + this._init.sy;
  }
  visitXN(node: Tree.Node, context: Tree.VisitorContext): number {
    const start = context.connectors.bottom.x - this._init.mx*context.index.total/2;
    const current = start + this._init.mx/2 + context.index.value*this._init.mx;
    
    /*
    if(context.index.total % 2 === 0) {
      const total = this.props.mx * context.index.value;
      const x = total + node.size.width/2;
      const shape = context.shape;
      return shape.bottom.y + (x);      
    }
    const total = this.props.mx * context.index.value;
    const x = total + node.size.width/2;
    
    return shape.bottom.y + (x);
    */
    return current;
  }
  visitAssociations(node: Tree.Node, context: Tree.VisitorContext) {
    // main node
    let main = this._assoications[node.id];
    if(!main) {
      main = { incoming: [], outgoing: [] };
      this._assoications[node.id] = main;
    }
    
    const connections = new GraphMapper<string[]>(node)
      .start    (target => [target.children.id])
      .end      (_target => [])
      .switch   (target => target.children.map(c => c.id))
      .service  (target => [target.children.id])
      .decision (target => [target.children.id])
      .map();
    for(const con of connections) {
      main.outgoing.push(con);
      let next = this._assoications[con];  
      if(!next) {
        next = { incoming: [], outgoing: [] };  
      }
      next.incoming.push(node.id);
    }
  }
}


export default GraphNodeVisitorDefault;

