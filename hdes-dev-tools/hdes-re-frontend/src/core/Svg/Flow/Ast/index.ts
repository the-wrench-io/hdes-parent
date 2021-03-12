import { Ast as FlowAst } from './Ast';
import NodeMapper from './NodeMapper';
import NodeBuilderDefault from './NodeBuilder';
import ShapeVisitorDefault from './ShapeBuilder';
import RendererVisitorDefault from './RendererVisitor';
import Snap from 'snapsvg-cjs-ts';

const FlowFactory = {
  mapper: (resource: FlowAst.Node) => new NodeMapper(resource),
  shapes: () => new ShapeVisitorDefault(),
  nodes: () => new NodeBuilderDefault(),
  renderer: (theme: FlowAst.ShapeRendererTheme, snap: Snap.Paper) => new RendererVisitorDefault(snap, theme),
}

export default FlowFactory;
export type { FlowAst }