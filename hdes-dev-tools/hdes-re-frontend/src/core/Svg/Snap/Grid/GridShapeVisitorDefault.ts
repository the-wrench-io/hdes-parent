import Snap from 'snapsvg-cjs-ts';
import { Tree, Immutables } from '../Tree';

interface Visited { }


class GridShapeVisitorDefault implements Tree.GridShapeVisitor<Snap.Element, Visited> {
  private _theme: Tree.Theme;
  private _snap: Snap.Paper;
  private _children: Snap.Element;
  private _shapes: Tree.GridShapes = {} as Tree.GridShapes;
  
  constructor(snap: Snap.Paper, theme: Tree.Theme) {
    this._snap = snap;
    this._children = this._snap.group();
    this._theme = theme;
  }
  
  visitShapes(node: Tree.GridShapes): Snap.Element {
    const context = new Immutables.Context(node.nodes, {
      x: node.headersRow.connectors.left.x,
      y: node.headersRow.connectors.top.y
    })
    this._shapes = node;
    this.visitHeaders(node.headersRow, context);
    this.visitRows(node.rows, context);
    return this._children;
  }
  visitTypography(shape: Tree.Shape<Tree.Node>, context: Tree.VisitorContext): Visited {
    const name = shape.node.typography;
    const snap = this._snap as unknown as { typography: ({}) => Snap.Element };
    const theme = this._theme;
    const lable = snap.typography({
      center: shape.connectors.center,
      size: shape.dimensions,
      text: name,
      theme: theme
    });
    lable.attr({
      stroke: theme.stroke
    });
    
    this._children.add(lable)
    return {}
  }
  visitHeaders(nodes: Tree.Shape<Tree.GridHeaderRow>, context: Tree.VisitorContext): Visited {
    let next: Tree.VisitorContext = context; 
    for(const headerId of nodes.associations.outgoing) {
      const header = this._shapes.getById(headerId) as Tree.Shape<Tree.GridHeader>;
      const visited = this.visitHeader(header, context);
      next = next.addNode(header.node, header.connectors); 
    }
    return {}
  } 
  visitHeader(node: Tree.Shape<Tree.GridHeader>, context: Tree.VisitorContext): Visited {
    const theme = this._theme;
    const con = node.connectors;
    const x = con.left.x;
    const y = con.top.y;
    const pathstring = `M ${x} ${y} h ${con.dimensions.width} v ${con.dimensions.height} H ${x} L ${x} ${y}`;
    const path = this._snap
      .path(pathstring)
      .attr({stroke: theme.stroke});
      
    this._children.add(path);
    this.visitTypography(node, context);
    return {}
  }
  visitRow(node: Tree.Shape<Tree.GridRow>, context: Tree.VisitorContext): Visited {
    const theme = this._theme;
    const con = node.connectors;
    const x = con.left.x;
    const y = con.top.y;
    const pathstring = `M ${x} ${y} h ${con.dimensions.width} v ${con.dimensions.height} H ${x} L ${x} ${y}`;
    const path = this._snap
      .path(pathstring)
      .attr({stroke: theme.stroke});
      
    let next: Tree.VisitorContext = context;
    this._children.add(path);
    for(const cellId of node.associations.outgoing) {
      const cell = this._shapes.getById(cellId) as Tree.Shape<Tree.GridCell>;
      const visited = this.visitCell(cell, next);
      next = next.addNode(cell.node, cell.connectors); 
    }
      
    return {}
  }
  visitRows(nodes: readonly Tree.Shape<Tree.GridRow>[], context: Tree.VisitorContext): Visited {
    let next: Tree.VisitorContext = context; 
    for(const row of nodes) {
      const visited = this.visitRow(row, next);
      next = next.addNode(row.node, row.connectors); 
    }
    return {}
  }
  visitCell(node: Tree.Shape<Tree.GridCell>, context: Tree.VisitorContext): Visited {
    const theme = this._theme;
    const con = node.connectors;
    const x = con.left.x;
    const y = con.top.y;
    const pathstring = `M ${x} ${y} h ${con.dimensions.width} v ${con.dimensions.height} H ${x} L ${x} ${y}`;
    const path = this._snap
      .path(pathstring)
      .attr({stroke: theme.stroke});
      
    this._children.add(path);
    this.visitTypography(node, context);
    return {}
  }
  visitCells(nodes: readonly Tree.Shape<Tree.GridCell>[], context: Tree.VisitorContext): Visited {
    return {}
  }
  visitNode(node: Tree.Shape<Tree.Node>, context: Tree.VisitorContext): Visited {
    return {} 
  }
}

export default GridShapeVisitorDefault;