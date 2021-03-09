import { Ast } from './Ast';
import createContext from './AstShapeVisitorContext';



class ShapeViewVisitor implements Ast.ShapeVisitor {

  visitRoot(root: Ast.RootNode): Ast.ShapeView {
    
  }
  visitStart(node: Ast.StartNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  visitEnd(node: Ast.EndNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  visitSwitch(node: Ast.SwitchNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  visitDecision(node: Ast.DecisionNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  visitService(node: Ast.ServiceNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  vistChild(child: Ast.NodeChild, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
  visitLoop(node: Ast.DecisionNode | Ast.ServiceNode, context: Ast.ShapeVisitorContext): Ast.Shape {
    
  }
}