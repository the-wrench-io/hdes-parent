import { FlowTree } from './FlowTree';



class FlowTreeBuilder implements FlowTree.RootBuilder {
  private children: FlowTree.Node[];
  private startNode?: FlowTree.StartNode;
 
  constructor() {
    this.children = [];
  }
  
  start(child: FlowTree.ChildNode): FlowTree.RootBuilder {
    if(this.startNode) {
      throw new Error("start node is already defined");
    }
    const children: FlowTree.ChildNode = Object.assign({}, child);
    this.startNode = { 
      type: "start", id: "start", content: "start",
      children, 
      size: {height: 25, width: 25} 
    }
    this.children.push(this.startNode);
    return this;
  }
  
  end(src: FlowTree.NodeBuilder): FlowTree.RootBuilder {
    const node: FlowTree.EndNode = {
      type: "end", 
      id: src.id,
      size: {height: 25, width: 25}, 
      content: src.content ? src.content: src.id,
      onClick: src.onClick
    };
    this.children.push(node);
    return this;    
  }
  
  switch(src: FlowTree.NodeBuilder, srcChildren: FlowTree.ChildNode[]): FlowTree.RootBuilder {
    const children: readonly FlowTree.ChildNode[] = srcChildren.map(e => ({id: e.id}));
    const node: FlowTree.SwitchNode = {
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
  
  decision(src: FlowTree.NodeBuilder, srcChildren: FlowTree.ChildNode, loop?: FlowTree.ChildNode): FlowTree.RootBuilder {
    const node: FlowTree.DecisionNode = {
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
  
  service(src: FlowTree.NodeBuilder, srcChildren: FlowTree.ChildNode, loop?: FlowTree.ChildNode, async?: boolean): FlowTree.RootBuilder {
    const node: FlowTree.ServiceNode = {
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
  
  build(): FlowTree.RootNode {
    if(!this.startNode) {
      throw new Error("start node is not defined");
    }
    return { start: this.startNode, children: [...this.children] };
  }

}


export default FlowTreeBuilder;