import { Ast as FlowAst } from './Ast';
import AstMapper from './AstMapper';
import AstRootBuilder from './AstRootBuilder';
import AstViewBuilder from './AstViewBuilder';


const FlowFactory = {
  mapper: (resource: FlowAst.Node) => new AstMapper(resource),
  view: () => new AstViewBuilder(),
  root: () => new AstRootBuilder()
}

export default FlowFactory;
export type { FlowAst }