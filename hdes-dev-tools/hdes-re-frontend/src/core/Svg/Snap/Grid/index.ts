import Snap from 'snapsvg-cjs-ts';
import { Tree } from '../Tree';
import GridBuilderDefault from './GridBuilderDefault';
import GridVisitorDefault from './GridVisitorDefault';


interface GridInit {
  cell: { min: Tree.Dimensions, max: Tree.Dimensions },
  theme: Tree.Theme,
  listeners: Tree.Listeners,
  typography: { attr: {} }
}

const render = (
  snap: Snap.Paper, init: GridInit, tester: Tree.DimensionsTester,
  data: (builder: Tree.GridBuilder) => Tree.GridShapes): Snap.Element => {
    
  const builder: Tree.GridBuilder = new GridBuilderDefault(tester, init.cell);
  const shapes: Tree.GridShapes = data(builder);
  const visitor: Tree.GridShapeVisitor<Snap.Element, {}> = new GridVisitorDefault(snap, tester, );
  return visitor.visitShapes(shapes, init.listeners);
}



export type { GridInit };
export { render };