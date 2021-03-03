import { FlowTree } from './FlowTree';



class FlowTreeViewBuilder implements FlowTree.ViewBuilder {
  private root?: FlowTree.RootNode;
  private cord?: FlowTree.Cord;
 
  start(cord: FlowTree.Cord) : FlowTree.ViewBuilder {
    this.cord = cord;
    return this;  
  }
 
  tree(node: FlowTree.RootNode): FlowTree.ViewBuilder {
    this.root = node;
    return this;
  }
  
  drawNext() {
    
  }
  
  build(): FlowTree.View {
    if(!this.root) {
      throw new Error("tree is not defined!");
    }
    if(!this.cord) {
      throw new Error("start cord is not defined!");
    }
    
    const cords: FlowTree.NodeCord[] = [];
    
    const start = this.root.start;
    cords.push({ id: start.id, 
      size: this.root.start.size,
      center: this.cord,
      left: {x: 0, y: 0},
      right: {x: 0, y: 0},
      
      top: {x: 0, y: 0}, 
      bottom: {x: 0, y: 0}
    });


    // format end result
    const nodes: Record<string, FlowTree.NodeCord> = {};
    cords.forEach(cord => nodes[cord.id] = cord);
    const arrows: FlowTree.Arrow[] = [];

    return { arrows, nodes };
  }

}


export default FlowTreeViewBuilder;