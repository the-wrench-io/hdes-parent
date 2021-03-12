import { Ast as FlowAst } from './Ast';
import NodeMapper from './NodeMapper';
import NodeBuilderDefault from './NodeBuilder';
import ShapeVisitorDefault from './ShapeBuilder';


const FlowFactory = {
  mapper: (resource: FlowAst.Node) => new NodeMapper(resource),
  shapes: () => new ShapeVisitorDefault(),
  nodes: () => new NodeBuilderDefault()
}

export default FlowFactory;
export type { FlowAst }