import Snap from 'snapsvg-cjs-ts';
import { Tree } from '../Tree';
import GridBuilderDefault from './GridBuilderDefault';
import GridShapeVisitorDefault from './GridShapeVisitorDefault';


interface GridInit {
  coords: Tree.Coordinates;
  node: { min: Tree.Dimensions, max: Tree.Dimensions },
  theme: Tree.Theme,
  listeners: Tree.Listeners,
  typography: { attr: {} }
}

const render = (
  snap: Snap.Paper, init: GridInit, tester: Tree.DimensionsTester,
  data: (builder: Tree.GridBuilder) => Tree.GridShapes): Snap.Element => {
    
  const builderInit = { id: "grid", coords: init.coords, node: init.node, tester};
    
  const builder: Tree.GridBuilder = new GridBuilderDefault(builderInit);
  const shapes: Tree.GridShapes = data(builder);
  
  console.log(shapes)
  const visitor: Tree.GridShapeVisitor<Snap.Element, {}> = new GridShapeVisitorDefault(snap, init.theme);
  return visitor.visitShapes(shapes, init.listeners);
}



export type { GridInit };
export { render };