import { Ast as FlowAst } from './Ast';
import AstMapper from './AstMapper';
import AstRootBuilder from './AstRootBuilder';
import AstShapeBuilder from './AstShapeBuilder';


const FlowFactory = {
  mapper: (resource: FlowAst.Node) => new AstMapper(resource),
  shapes: () => new AstShapeBuilder(),
  nodes: () => new AstRootBuilder()
}

export default FlowFactory;
export type { FlowAst }