declare namespace Ast {
  
  type NodeType = "switch" | "start" | "end" | 
    "decision"  | "decision-loop" |
    "service"   | "service-loop";
  
  
  interface Cord {
    x: number;  y: number;
  }
  
  interface NodeSize {
    height: number, width: number
  }
  
  interface NodeCord {
    id: string; 
    size: NodeSize;
    center: Cord;
    left: Cord; right: Cord;
    top: Cord; bottom: Cord;
  }
  
  interface Arrow {
    id: string;
    cords: readonly Cord[];
  }
  
  interface View {
    arrows: Arrow[];
    nodes: Record<string, NodeCord>;    
  }
  
  interface ViewBuilder {
    start(cord: Cord) : ViewBuilder;
    tree(node: RootNode) : ViewBuilder;
    build(): View;
  }
  
  interface RootBuilder {
    start(child: ChildNode): RootBuilder;
    end(node: NodeBuilder): RootBuilder;
    switch(node: NodeBuilder, children: ChildNode[]): RootBuilder;
    decision(node: NodeBuilder, children: ChildNode, loop?: ChildNode): RootBuilder;
    service(node: NodeBuilder, children: ChildNode, loop?: ChildNode, async?: boolean): RootBuilder;
    build(): RootNode;
  }

  interface NodeBuilder {
    id: string;
    content?: string;
    onClick?: (self: Node) => void;
  }
  
  interface RootNode {
    start: StartNode;
    end: EndNode;
    children: readonly Node[]
    getById: (id:string) => Node;
  }
  
  interface ChildNode {
    id: string;
  }
  interface Node {
    id: string;
    content: string;
    type: NodeType;
    size: NodeSize;
    onClick?: (self: Node) => void;
  }
  
  interface StartNode extends Node {
    children: ChildNode;
  }
  interface EndNode extends Node {}
  
  interface SwitchNode extends Node {
    children: readonly ChildNode[];
  }
  interface DecisionNode extends Node {
    loop?: ChildNode;
    children: ChildNode;
  }
  interface ServiceNode extends Node {
    async?: boolean;
    loop?: ChildNode;
    children: ChildNode;
  }
}

export type { Ast };