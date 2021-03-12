import Snap from 'snapsvg-cjs-ts';


declare namespace Ast {
  
  interface ShapeRenderer {
    theme(them: ShapeRendererTheme): ShapeRenderer;
    snap(snap: Snap.Paper): ShapeRenderer;
    shapes(shapes: ShapeView): ShapeRenderer;
    build(): void;
  }
  
  interface ShapeBuilder {
    start(cord: ShapeCord): ShapeBuilder;
    tree(node: NodeView): ShapeBuilder;
    build(): ShapeView;
  }
  
  interface NodeBuilder {
    start(child: NodeChild): NodeBuilder;
    end(node: NodeInit): NodeBuilder;
    switch(node: NodeInit, children: NodeChild[]): NodeBuilder;
    decision(node: NodeInit, children: NodeChild, loop?: NodeChild): NodeBuilder;
    service(node: NodeInit, children: NodeChild, loop?: NodeChild, async?: boolean): NodeBuilder;
    build(): NodeView;
  }
  
  interface ShapeRendererTheme {
    fill: string;
    stroke: string;
    background: string;
  }
  
  interface ShapeCord {
    x: number;  y: number;
  }
  
  interface LineShape {
    id: string;
    cords: readonly ShapeCord[];
  }
  
  interface Shape<T extends Node> {
    id: string; node: T; 
    size: NodeSize; center: ShapeCord;
    left: ShapeCord; right: ShapeCord;
    top: ShapeCord; bottom: ShapeCord;
  }
  interface ShapeView {
    lines: LineShape[];
    shapes: Record<string, Shape<Node>>;
  }

  interface NodeView extends Node {
    start: StartNode;
    end: EndNode;
    children: readonly Node[]
    getById: (id:string) => Node;
  }

  interface NodeInit {
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
  
  
  interface RendererVisitorState {
    
  }
  
  interface RendererVisitorContext {
    visited: Record<string, Shape<Node>>;
    add(visited: Shape<Node>): RendererVisitorContext; 
  }

  interface RendererVisitor {
    visitRoot(node: ShapeView): RendererVisitorState; 
    visitStart(node: Shape<StartNode>, context: RendererVisitorContext): RendererVisitorState;
    visitEnd(node: Shape<EndNode>, context: RendererVisitorContext): RendererVisitorState;
    visitSwitch(node: Shape<SwitchNode>, context: RendererVisitorContext): RendererVisitorState;
    visitDecision(node: Shape<DecisionNode>, context: RendererVisitorContext): RendererVisitorState;
    visitService(node: Shape<ServiceNode>, context: RendererVisitorContext): RendererVisitorState;
    visitLoop(node: Shape<DecisionNode | ServiceNode>, context: RendererVisitorContext): RendererVisitorState; 
  }
  
  interface ShapeVisitorIndex {
    value: number; 
    total: number;
    previous?: Shape<Node>;
  }
  
  interface ShapeVisitorState {
    shape: Shape<Node>;
    children: Shape<Node>[];
  }
  
  interface ShapeVisitorContext {
    parent: ShapeVisitorContext;
    value: Node;
    shape: Shape<Node>;
    index: ShapeVisitorIndex;
    type: "nullNode" | NodeType;
    shapes: readonly Shape<Node>[];
    getRoot(): NodeView;
    addNode(node: Node, shape: Shape<Node>, index?: ShapeVisitorIndex): ShapeVisitorContext; 
    getNode(type: NodeType): ShapeVisitorContext;
  }
  
  interface ShapeVisitor {
    visitRoot(root: NodeView): ShapeView;
    visitX(node: Node, context: ShapeVisitorContext): number;
    visitY(node: Node, context: ShapeVisitorContext): number;

    visitStart(node: StartNode, context: ShapeVisitorContext): ShapeVisitorState;
    visitEnd(node: EndNode, context: ShapeVisitorContext): ShapeVisitorState;
    visitSwitch(node: SwitchNode, context: ShapeVisitorContext): ShapeVisitorState;
    visitDecision(node: DecisionNode, context: ShapeVisitorContext): ShapeVisitorState;
    visitService(node: ServiceNode, context: ShapeVisitorContext): ShapeVisitorState;
    vistChild(child: NodeChild, context: ShapeVisitorContext): ShapeVisitorState;
    visitChildren(child: NodeChild, context: ShapeVisitorContext): Shape<Node>[];
    visitLoop(node: DecisionNode | ServiceNode, context: ShapeVisitorContext): ShapeVisitorState;
  }
}

export type { Ast };