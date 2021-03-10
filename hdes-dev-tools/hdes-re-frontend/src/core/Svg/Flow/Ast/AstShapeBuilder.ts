import { Ast } from './Ast';
import AstShapeVisitor from './AstShapeVisitor';


class AstShapeBuilder implements Ast.ShapeBuilder {
  private root?: Ast.RootNode;
  private cord?: Ast.Cord;
 
  start(cord: Ast.Cord) : Ast.ShapeBuilder {
    this.cord = cord;
    return this;  
  }
  tree(node: Ast.RootNode): Ast.ShapeBuilder {
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
    return new AstShapeVisitor(props).visitRoot(this.root);
  }

}


export default AstShapeBuilder;