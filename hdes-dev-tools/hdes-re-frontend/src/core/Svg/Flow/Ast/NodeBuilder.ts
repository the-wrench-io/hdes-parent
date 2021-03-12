import { Ast } from './Ast';


class NodeBuilderDefault implements Ast.NodeBuilder {
  private children: Ast.Node[];
  private startNode?: Ast.StartNode;
  private endNode?: Ast.EndNode;
 
  constructor() {
    this.children = []; 
  }
  
  start(child: Ast.NodeChild): Ast.NodeBuilder {
    if(this.startNode) {
      throw new Error("start node is already defined");
    }
    const children: Ast.NodeChild = Object.assign({}, child);
    this.startNode = new ImmutableStartNode({ 
      id: "start", content: "start", children,
      size: { height: 25, width: 25 } 
    });
    
    this.children.push(this.startNode);
    return this;
  }
  
  end(src: Ast.NodeInit): Ast.NodeBuilder {
    if(this.endNode) {
      throw new Error("end node is already defined");
    }
    const node: Ast.EndNode = new ImmutableEndNode({
      id: src.id,
      size: {height: 25, width: 25}, 
      content: src.content ? src.content: src.id,
      onClick: src.onClick
    });
    this.endNode = node;
    this.children.push(node);
    return this;    
  }
  
  switch(src: Ast.NodeInit, srcChildren: Ast.NodeChild[]): Ast.NodeBuilder {
    const children: Ast.NodeChild[] = srcChildren.map(e => ({id: e.id}));
    const node: Ast.SwitchNode = new ImmutableSwitchNode({
      id: src.id, 
      size: {height: 25, width: 25},
      content: src.content ? src.content: src.id, 
      onClick: src.onClick, children
    });
    this.children.push(node);
    return this;
  }
  
  decision(src: Ast.NodeInit, srcChildren: Ast.NodeChild, loop?: Ast.NodeChild): Ast.NodeBuilder {
    const node: Ast.DecisionNode = new ImmutableDecisionNode({ 
      id: src.id,
      size: {height: 50, width: 100},
      content: src.content ? src.content: src.id,
      onClick: src.onClick,
      children: { id: srcChildren.id },
    }, loop ? { id: loop.id } : undefined);
    this.children.push(node);
    return this;
  }
  
  service(src: Ast.NodeInit, srcChildren: Ast.NodeChild, loop?: Ast.NodeChild, async?: boolean): Ast.NodeBuilder {
    const node: Ast.ServiceNode = new ImmutableServiceNode({ 
      id: src.id,
      size: {height: 50, width: 100}, 
      content: src.content ? src.content: src.id,
      onClick: src.onClick,
      children: { id: srcChildren.id }
    }, loop ? { id: loop.id } : undefined);
    this.children.push(node);
    return this;
  }
  
  build(): Ast.NodeView {
    if(!this.startNode) {
      throw new Error("start node is not defined");
    }
    if(!this.endNode) {
      throw new Error("end node is not defined");
    }
    return new ImmutableRootNode(this.startNode, this.endNode, this.children);
  }
}


class TemplateNode implements Ast.Node {
  private _id: string;
  private _content: string;
  private _type: Ast.NodeType;
  private _size: Ast.NodeSize;
  private _onClick?: (self: Ast.Node) => void;
  
  constructor(props: {
    id: string, content: string,
    size: Ast.NodeSize,
    onClick?: (self: Ast.Node) => void }, type: Ast.NodeType) {
    
    this._id = props.id;
    this._content = props.content;
    this._type = type;
    this._size = props.size;
    this._onClick = props.onClick;
  }
  
  get id() {
    return this._id;
  }
  get content() {
    return this._content;
  }
  get type() {
    return this._type;
  }
  get size() {
    return this._size;
  }
  onClick() {
    if(this._onClick) {
      this._onClick(this);
    }
  }
}

class ImmutableStartNode extends TemplateNode implements Ast.StartNode {
  private _children: Ast.NodeChild;

  constructor(props: {id: string, content: string,
    size: Ast.NodeSize,
    children: Ast.NodeChild,
    onClick?: (self: Ast.Node) => void}) {
      
    super(props, "start");
    this._children = props.children;
  }
  get children() {
    return this._children;
  } 
}
class ImmutableEndNode extends TemplateNode implements Ast.EndNode {
  constructor(props: {
    id: string, content: string,
    size: Ast.NodeSize,
    onClick?: (self: Ast.Node) => void}) {
      
    super(props, "end");
  }
}
class ImmutableSwitchNode extends TemplateNode implements Ast.SwitchNode {
  private _children: readonly Ast.NodeChild[];

  constructor(props: {
    id: string, content: string,
    size: Ast.NodeSize, 
    children: Ast.NodeChild[],
    onClick?: (self: Ast.Node) => void}) {
      
    super(props, "switch");
    this._children = props.children;
  }
  get children() {
    return this._children;
  }
}
class ImmutableDecisionNode extends TemplateNode implements Ast.DecisionNode {
  private _children: Ast.NodeChild;
  private _loop?: Ast.NodeChild;
  
  constructor(props: {
    id: string, content: string,
    size: Ast.NodeSize, 
    children: Ast.NodeChild,
    onClick?: (self: Ast.Node) => void }, 
    loop?: Ast.NodeChild) {
      
    super(props, loop ? "decision-loop" : "decision");
    this._children = props.children;
  }
  get children() {
    return this._children;
  }
  get loop() {
    return this._loop;
  }
}
class ImmutableServiceNode extends TemplateNode implements Ast.ServiceNode {
  private _children: Ast.NodeChild;
  private _loop?: Ast.NodeChild;
  
  constructor(props: {
    id: string, content: string,
    size: Ast.NodeSize, 
    children: Ast.NodeChild,
    onClick?: (self: Ast.Node) => void }, 
    loop?: Ast.NodeChild) {
      
    super(props, loop ? "service-loop" : "service");
    this._children = props.children;
  }
  get children() {
    return this._children;
  }
  get loop() {
    return this._loop;
  }
}


class ImmutableRootNode implements Ast.NodeView {
  private _children: Record<string, Ast.Node> = {};
  private _start: Ast.StartNode;
  private _end: Ast.EndNode;
 
  constructor(start: Ast.StartNode, end: Ast.EndNode, children: Ast.Node[]) {
    this._start = start;
    this._end = end;
    children.forEach(c => this._children[c.id] = c)
  }
  onClick() {
  }
  getById(id: string): Ast.Node {
    const result = this._children[id];
    if(!result) {
      throw new Error("no node by id: " + id);
    }
    return result;
  }
  get id() {
    return this.type;
  }
  get size() {
    return {height: 0, width: 0};
  }
  get content() {
    return "";
  }
  get type(): "root" {
    return "root";
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


export default NodeBuilderDefault;

