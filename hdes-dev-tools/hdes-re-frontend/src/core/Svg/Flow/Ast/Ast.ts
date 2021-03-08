declare namespace Ast {
  
  interface Cord {
    x: number;  y: number;
  }
  
  interface Line {
    id: string;
    cords: readonly Cord[];
  }
  
  interface Shape {
    id: string; 
    size: NodeSize; center: Cord;
    left: Cord; right: Cord;
    top: Cord; bottom: Cord;
  }
  
  interface ShapeView {
    lines: Line[];
    shapes: Record<string, Shape>;
  }
  
  interface ShapeVisitorContext {
    parent?: ShapeVisitorContext;
    value: Node;
    findNode<T extends Node>(type: NodeType): T | undefined;
  }
  
  interface ShapeVisitor {
    start(): ShapeVisitor;
    visitRoot(root: RootNode): ShapeVisitor;
    visitStart(node: StartNode): ShapeVisitor;
    visitEnd(node: EndNode): ShapeVisitor;
    visitSwitch(node: SwitchNode): ShapeVisitor;
    visitDecision(node: DecisionNode): ShapeVisitor;
    visitService(node: ServiceNode): ShapeVisitor;
    vistChild(child: NodeChild): ShapeVisitor;
    visitLoop(node: DecisionNode | ServiceNode): ShapeVisitor;
    end(): ShapeView;
  }
  
  interface ShapeBuilder {
    start(cord: Cord): ShapeBuilder;
    tree(node: RootNode): ShapeBuilder;
    build(): ShapeView;
  }
  
  
  interface RootBuilder {
    start(child: NodeChild): RootBuilder;
    end(node: NodeBuilder): RootBuilder;
    switch(node: NodeBuilder, children: NodeChild[]): RootBuilder;
    decision(node: NodeBuilder, children: NodeChild, loop?: NodeChild): RootBuilder;
    service(node: NodeBuilder, children: NodeChild, loop?: NodeChild, async?: boolean): RootBuilder;
    build(): RootNode;
  }

  interface RootNode {
    start: StartNode;
    end: EndNode;
    children: readonly Node[]
    getById: (id:string) => Node;
  }

  interface NodeBuilder {
    id: string;
    content?: string;
    onClick?: (self: Node) => void;
  }

  interface Node {
    id: string;
    content: string;
    type: NodeType;
    size: NodeSize;
    onClick?: (self: Node) => void;
  }
  
  interface StartNode extends Node {
    children: NodeChild;
  }
  interface EndNode extends Node {}
  
  interface SwitchNode extends Node {
    children: readonly NodeChild[];
  }
  interface DecisionNode extends Node {
    loop?: NodeChild;
    children: NodeChild;
  }
  interface ServiceNode extends Node {
    async?: boolean;
    loop?: NodeChild;
    children: NodeChild;
  }
  
  interface NodeChild {
    id: string;
  }
  type NodeType = "switch"        | 
    "start"     | "end"           | 
    "decision"  | "decision-loop" |
    "service"   | "service-loop";
  
  interface NodeSize {
    height: number, width: number
  }
}

export type { Ast };