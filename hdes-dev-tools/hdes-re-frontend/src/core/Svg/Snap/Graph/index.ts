import Snap from 'snapsvg-cjs-ts';
import { Tree } from '../Tree';
import GraphBuilderDefault from './GraphBuilderDefault';
import GraphShapeVisitorDefault from './GraphShapeVisitorDefault';


interface GraphInit {
  coords: Tree.Coordinates;
  node: { min: Tree.Dimensions, max: Tree.Dimensions },
  listeners: Tree.Listeners,
  theme: Tree.Theme,
  typography: { attr: {} }
}

const render = (
  snap: Snap.Paper, init: GraphInit, tester: Tree.DimensionsTester,
  data: (builder: Tree.GraphBuilder) => Tree.GraphShapes): Snap.Element => {
    
  const builder: Tree.GraphBuilder = new GraphBuilderDefault({coords: init.coords, node: init.node});
  const shapes: Tree.GraphShapes = data(builder);
  const visitor: Tree.GraphShapeVisitor<Snap.Element, {}> = new GraphShapeVisitorDefault(snap, tester, init.theme);
  return visitor.visitShapes(shapes, init.listeners); 
}



export type { GraphInit };
export { render };