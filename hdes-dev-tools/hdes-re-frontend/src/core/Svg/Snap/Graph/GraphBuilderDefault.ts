import { Tree, Immutables } from '../Tree'
import GraphNodeVisitorDefault from './GraphNodeVisitorDefault';

const typography = (id: string, init: { typography?: Tree.Typography }): Tree.Typography => {
  return new Immutables.Typography(init.typography ? init.typography : { text: id });
}

interface BuilderInit {
  coords: Tree.Coordinates;
  node: { min: Tree.Dimensions, max: Tree.Dimensions }
}
  
class GraphBuilderDefault implements Tree.GraphBuilder {
  private _init: BuilderInit;
  private _children: Record<string, Tree.Node> = {};
  private _start?: Tree.GraphStart;
  private _end?: Tree.GraphEnd;

  constructor(init: BuilderInit) {
    this._init = init;
  }

  start(id: string, init: Tree.InitStart): Tree.GraphBuilder {
    if(this._start) {
      throw new Error("start is already defined!");
    }
    
    const node = new Immutables.GraphStart({
      id, 
      children: new Immutables.GraphChild(init.next, typography(init.next, {})),
      typography: typography(id, init),
    });
    this._start = node;
    return this.visitAssociations(node);
  }
  end(id: string, init: Tree.InitEnd): Tree.GraphBuilder {
    if(this._end) {
      throw new Error("end is already defined!");
    }
    const node = new Immutables.GraphEnd({
      id,
      typography: typography(id, init),
    });
    this._end = node;
    return this.visitAssociations(node);
  }
  switch(id: string, init: Tree.InitSwitch): Tree.GraphBuilder {
    const node = new Immutables.GraphSwitch({
      id,
      typography: typography(id, init),
      children: init.next.map(n => new Immutables.GraphChild(n.id, typography(n.id, n)))
    });
    return this.visitAssociations(node);
  }
  decision(id: string, init: Tree.InitDecision): Tree.GraphBuilder {
    const node = new Immutables.GraphDecision({
      id,
      typography: typography(id, init),
      children: new Immutables.GraphChild(init.next, typography(init.next, {})),
    }, init.loop ? new Immutables.GraphChild(init.loop, typography(init.loop, {})) : undefined);
    return this.visitAssociations(node);
  }
  service(id: string, init: Tree.InitService): Tree.GraphBuilder {
    const node = new Immutables.GraphService({
      id,
      typography: typography(id, init),
      children: new Immutables.GraphChild(init.next, typography(init.next, {})),
    }, init.loop ? new Immutables.GraphChild(init.loop, typography(init.loop, {})) : undefined);

    return this.visitAssociations(node);
  }
  visitAssociations(node: Tree.Node): Tree.GraphBuilder {
    this._children[node.id] = node;
    return this;
  }
  build(): Tree.GraphShapes {
    if(!this._start) {
      throw new Error("start is not defined");
    }
    if(!this._end) {
      throw new Error("end is not defined");
    }
    const props = {sy: 50, sx: 30, mx: 120, id: "graph", coords: this._init.coords};
    return new GraphNodeVisitorDefault(props).visit(this._start.id, this._children);
  }
}


export default GraphBuilderDefault;

