import { Ast } from './Ast';
import AstMapper from './AstMapper';


const calcTips = (size: Ast.NodeSize, cord: Ast.Cord): {
    left: Ast.Cord, right: Ast.Cord, 
    top: Ast.Cord, bottom: Ast.Cord
  } => {
  const rx = size.width/2;
  const ry = size.height/2;
  
  return {
    left: {x: cord.x-rx, y: cord.y}, 
    right: {x: cord.x+rx, y: cord.y}, 
    top: {x: cord.x, y: cord.y - ry}, 
    bottom: {x: cord.x, y: cord.y + ry}
  };
}


class AstShapeBuilder implements Ast.ShapeBuilder {
  private root?: Ast.RootNode;
  private cord?: Ast.Cord;
  private sy: number = 50; // y axis spacer between nodes
  private sx: number = 30; // y axis spacer between nodes
  private calculated: string[] = [];
 
  start(cord: Ast.Cord) : Ast.ShapeBuilder {
    this.cord = cord;
    return this;  
  }
 
  tree(node: Ast.RootNode): Ast.ShapeBuilder {
    this.root = node;
    return this;
  }

  children(node: Ast.Node, parent: Ast.Shape): Ast.Shape[] {
    return new AstMapper<Ast.Shape[]>(node)
      .decision((decision: Ast.DecisionNode) => {
        const nodeId = decision.children.id;
        if(this.calculated.includes(nodeId)) {
          return [];
        }
        const node = this.root?.getById(nodeId) as Ast.Node;
        const center = {x: parent.center.x, y: parent.bottom.y + this.sy};
        const cord: Ast.Shape = Object.assign({
          id: node.id, 
          size: node.size, 
          center }, 
          calcTips(node.size, center)  
        );
        return [cord, ...this.children(node, cord)];
      })
      .service((decision: Ast.ServiceNode) => {
        const nodeId = decision.children.id;
        if(this.calculated.includes(nodeId)) {
          return [];
        }
        const node = this.root?.getById(nodeId) as Ast.Node;
        const center = {x: parent.center.x, y: parent.bottom.y + this.sy};
        const cord: Ast.Shape = Object.assign({
          id: node.id, 
          size: node.size, 
          center }, 
          calcTips(node.size, center)  
        );
        return [cord, ...this.children(node, cord)];
      })
      .switch((decision: Ast.SwitchNode) => {
        let index = 0;
        let evenX = 0;
        let oddX = 0;
        console.log("decision at", parent)

        const switchChildren: Ast.Shape[] = [];
        for(const child of decision.children) {
          const nodeId = child.id;
          if(this.calculated.includes(nodeId)) {
            return [];
          }
          const node = this.root?.getById(nodeId) as Ast.Node;
          let x;
          if(index === 0) {
            x = parent.left.x - node.size.width/2;
          } else if(index % 2 === 0) { //2/4/6
            x = evenX + node.size.width/2;
          } else { 
            x = oddX - node.size.width/2;
          }
          
          const center = {x, y: parent.bottom.y + this.sy};
          const cord: Ast.Shape = Object.assign({
            id: node.id, 
            size: node.size, 
            center }, 
            calcTips(node.size, center)  
          );
          
          if(index === 0) {
            oddX = cord.left.x - this.sx;
            evenX += parent.right.x;
          } else if(index % 2 === 0) { //2/4/6
            evenX += cord.right.x + this.sx;
          } else { 
            oddX -= cord.left.x - this.sx;
          }
          index++;
          
          switchChildren.push(cord);
          switchChildren.push(...this.children(node, cord));
        }
        return switchChildren;
      })
      .start((start) => {
        const nodeId = start.children.id;
        if(this.calculated.includes(nodeId)) {
          return [];
        }
        const node = this.root?.getById(nodeId) as Ast.Node;
        const center = {x: parent.center.x, y: parent.center.y + this.sy};
        const cord: Ast.Shape = Object.assign({
          id: node.id, 
          size: node.size, 
          center }, 
          calcTips(node.size, center)  
        );
        
        return [cord, ...this.children(node, cord)];
      })
      .end(() => [])
      .map();
  }
  
  build(): Ast.ShapeView {
    if(!this.root) {
      throw new Error("tree is not defined!");
    }
    if(!this.cord) {
      throw new Error("start cord is not defined!");
    }
    
    const cords: Ast.Shape[] = [];
    const start: Ast.Shape = Object.assign({ 
      id: this.root.start.id, 
      size: this.root.start.size,
      center: this.cord}, 
      calcTips(this.root.start.size, this.cord));
    
    cords.push(start);
    cords.push(...this.children(this.root.start, start))

    // format end result
    const shapes: Record<string, Ast.Shape> = {};
    cords.forEach(cord => shapes[cord.id] = cord);
    const lines: Ast.Line[] = [];

    return { lines, shapes };
  }

}


export default AstShapeBuilder;