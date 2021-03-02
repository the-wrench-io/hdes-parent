declare namespace FlowTree {
  
  type NodeType = "switch" | "start" | "end" | 
    "decision"  | "decision-loop" |
    "service"   | "service-loop";
  
  interface Node {
    id: string;
    content: string;
    type: NodeType;
    onClick: (self: Node) => void;
  }
  
  interface StartNode extends Node {
    children: Node;
  }
  interface EndNode extends Node {}
  
  interface SwitchNode extends Node {
    children: readonly Node[];
  }
  interface DecisionNode extends Node {
    children: Node;
  }
  interface DecisionLoopNode extends Node {
    children: { before: Node, after: Node}
  }
  interface ServiceNode extends Node {
    children: Node;
  }
  interface ServiceLoopNode extends Node {
    children: { before: Node, after: Node}
  }
}

export type { FlowTree };