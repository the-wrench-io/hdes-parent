import { Ast } from './Ast';


class AstBuilder implements Ast.RootBuilder {
  private children: Ast.Node[];
  private startNode?: Ast.StartNode;
  private endNode?: Ast.EndNode;
 
  constructor() {
    this.children = [];
  }
  
  start(child: Ast.ChildNode): Ast.RootBuilder {
    if(this.startNode) {
      throw new Error("start node is already defined");
    }
    const children: Ast.ChildNode = Object.assign({}, child);
    this.startNode = { 
      type: "start", id: "start", content: "start",
      children, 
      size: {height: 25, width: 25} 
    }
    this.children.push(this.startNode);
    return this;
  }
  
  end(src: Ast.NodeBuilder): Ast.RootBuilder {
    if(this.endNode) {
      throw new Error("end node is already defined");
    }
    const node: Ast.EndNode = {
      type: "end", 
      id: src.id,
      size: {height: 25, width: 25}, 
      content: src.content ? src.content: src.id,
      onClick: src.onClick
    };
    this.endNode = node;
    this.children.push(node);
    return this;    
  }
  
  switch(src: Ast.NodeBuilder, srcChildren: Ast.ChildNode[]): Ast.RootBuilder {
    const children: readonly Ast.ChildNode[] = srcChildren.map(e => ({id: e.id}));
    const node: Ast.SwitchNode = {
      type: "switch", 
      id: src.id, 
      size: {height: 25, width: 25},
      content: src.content ? src.content: src.id, 
      onClick: src.onClick,
      children
    };
    this.children.push(node);
    return this;
  }
  
  decision(src: Ast.NodeBuilder, srcChildren: Ast.ChildNode, loop?: Ast.ChildNode): Ast.RootBuilder {
    const node: Ast.DecisionNode = {
      type: loop? "decision-loop" : "decision", 
      id: src.id,
      size: {height: 50, width: 100},
      content: src.content ? src.content: src.id,
      onClick: src.onClick,
      children: { id: srcChildren.id },
      loop: loop ? { id: loop.id } : undefined
    };
    this.children.push(node);
    return this;
  }
  
  service(src: Ast.NodeBuilder, srcChildren: Ast.ChildNode, loop?: Ast.ChildNode, async?: boolean): Ast.RootBuilder {
    const node: Ast.ServiceNode = {
      type: loop? "service-loop" : "service", 
      id: src.id,
      size: {height: 50, width: 100}, 
      content: src.content ? src.content: src.id,
      onClick: src.onClick,
      children: { id: srcChildren.id },
      loop: loop ? { id: loop.id } : undefined
    };
    this.children.push(node);
    return this;
  }
  
  build(): Ast.RootNode {
    if(!this.startNode) {
      throw new Error("start node is not defined");
    }
    if(!this.endNode) {
      throw new Error("end node is not defined");
    }
    return new ImmutableRootNode(this.startNode, this.endNode, this.children);
  }

}


class ImmutableRootNode implements Ast.RootNode {
  private _children: Record<string, Ast.Node> = {};
  private _start: Ast.StartNode;
  private _end: Ast.EndNode;
 
  constructor(start: Ast.StartNode, end: Ast.EndNode, children: Ast.Node[]) {
    this._start = start;
    this._end = end;
    children.forEach(c => this._children[c.id] = c)
  }
  
  getById(id: string): Ast.Node {
    const result = this._children[id];
    if(!result) {
      throw new Error("no node by id: " + id);
    }
    return result;
  }
  get start() {
    return this._start;
  }
  get end() {
    return this._end;
  }  
  get children() {
    return Object.values(this._children);
  }
}


export default AstBuilder;