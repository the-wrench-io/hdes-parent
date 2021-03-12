import { Ast } from './Ast';
import ShapeVisitorDefault from './ShapeVisitor';


class ShapeBuilderDefault implements Ast.ShapeBuilder {
  private root?: Ast.NodeView;
  private cord?: Ast.ShapeCord;
 
  start(cord: Ast.ShapeCord) : Ast.ShapeBuilder {
    this.cord = cord;
    return this;  
  }
  tree(node: Ast.NodeView): Ast.ShapeBuilder {
    this.root = node;
    return this;
  }
  
  build(): Ast.ShapeView {
    if(!this.root) {
      throw new Error("tree is not defined!");
    }
    if(!this.cord) {
      throw new Error("start cord is not defined!");
    }
    
    const props = {sy: 50, sx: 30, mx: 120, start: this.cord };
    return new ShapeVisitorDefault(props).visitRoot(this.root);
  }

}


export default ShapeBuilderDefault;