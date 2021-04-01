import Snap from 'snapsvg-cjs-ts';
import { Tree } from '../Tree';

interface Visited { }


class GridVisitorDefault implements Tree.GridShapeVisitor<Snap.Element, Visited> {
  private _tester: Tree.DimensionsTester;
  private _snap: Snap.Paper;
  
  constructor(snap: Snap.Paper, tester: Tree.DimensionsTester) {
    this._tester = tester;
    this._snap = snap;
  }
  
  visitShapes(node: Tree.Shapes): Snap.Element {
    const group = this._snap.group();
    return group;
  }
  visitTypography(node: Tree.Typography, context: Tree.VisitorContext): Visited {
    return {}
  } 
  visitHeader(node: Tree.Shape<Tree.GridHeader>, context: Tree.VisitorContext): Visited {
    return {}
  }
  visitHeaders(nodes: readonly Tree.Shape<Tree.GridHeader>[], context: Tree.VisitorContext): Visited {
    return {}
  }
  visitRow(node: Tree.Shape<Tree.GridRow>, context: Tree.VisitorContext): Visited {
    return {}
  }
  visitRows(nodes: readonly Tree.Shape<Tree.GridRow>[], context: Tree.VisitorContext): Visited {
    return {}
  }
  visitCell(node: Tree.Shape<Tree.GridCell>, context: Tree.VisitorContext): Visited {
    return {}
  }
  visitCells(nodes: readonly Tree.Shape<Tree.GridCell>[], context: Tree.VisitorContext): Visited {
    return {}
  }
  visitNode(node: Tree.Shape<Tree.Node>, context: Tree.VisitorContext): Visited {
    return {} 
  }
}

/*
  const box = new TextVisitorDefault(props.tester, props.size).visitText(props.text);
  const textArea = new TextAreaBuilderDefault(props.size).lines(box.lines).build();
  const x = props.center.x// - props.size.width / 2;
  const y = props.center.y// - props.size.height / 2;
  const t = snap
    .text(x, y, textArea)
    .attr(props.attr);
  t.selectAll("tspan:nth-child(n+2)").attr({ x, dy: "1.2em"});
*/

export default GridVisitorDefault;