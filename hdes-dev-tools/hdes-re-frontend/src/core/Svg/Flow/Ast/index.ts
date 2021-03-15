import { Ast as FlowAst } from './Ast';
import NodeMapper from './NodeMapper';
import NodeBuilderDefault from './NodeBuilder';
import ShapeBuilderDefault from './ShapeBuilder';
import ShapeRendererDefault from './ShapeRenderer';


const FlowFactory = {
  mapper: (resource: FlowAst.Node) => new NodeMapper(resource),
  shapes: () => new ShapeBuilderDefault(),
  nodes: () => new NodeBuilderDefault(),
  renderer: () => new ShapeRendererDefault(),
}

export default FlowFactory;
export type { FlowAst }