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
  
  interface ShapeViewProps {
    sy: number, 
    sx: number, 
    start: Cord;
  }
  
  interface ShapeVisitorContext {
    parent: ShapeVisitorContext;
    value: Node;
    shape: Shape;
    type: "nullNode" | NodeType;
    shapes: readonly Shape[];
    getRoot(): Ast.RootNode;
    addNode(node: Ast.Node, shape: Ast.Shape): ShapeVisitorContext; 
    getNode(type: NodeType): ShapeVisitorContext;
  }
  
  interface VisitedShapes {
    shape: Shape;
    children: Shape[];
  }
  
  interface ShapeVisitor {
    visitRoot(root: RootNode, props: ShapeViewProps): ShapeView;
    visitX(node: Node, context: ShapeVisitorContext): number;
    visitY(node: Node, context: ShapeVisitorContext): number;
    visitYDecision(node: Node, context: ShapeVisitorContext): number;
    visitStart(node: StartNode, context: ShapeVisitorContext): VisitedShapes;
    visitEnd(node: EndNode, context: ShapeVisitorContext): VisitedShapes;
    visitSwitch(node: SwitchNode, context: ShapeVisitorContext): VisitedShapes;
    visitDecision(node: DecisionNode, context: ShapeVisitorContext): VisitedShapes;
    visitService(node: ServiceNode, context: ShapeVisitorContext): VisitedShapes;
    vistChild(child: NodeChild, context: ShapeVisitorContext): Ast.VisitedShapes;
    visitChildren(child: NodeChild, context: ShapeVisitorContext): Ast.Shape[];
    visitLoop(node: DecisionNode | ServiceNode, context: ShapeVisitorContext): VisitedShapes;
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

  interface RootNode extends Node {
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
    onClick: () => void;
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
  type NodeType = "root" | 
    "switch"    | "start" | "end" | 
    "decision"  | "decision-loop" |
    "service"   | "service-loop";
  
  interface NodeSize {
    height: number, width: number
  }
}

export type { Ast };