import { Tree, Immutables } from '../Tree';

interface Visited {
  //first: Tree.VisitorContext;
  //last: Tree.VisitorContext;
  shape: Tree.Shape<Tree.Node>;
}

interface VisitorInit {
  id: string;
  coords: Tree.Coordinates;
}


class GridShapeVisitorDefault implements Tree.GridNodeVisitor<Tree.GridShapes, Visited> {
  private _init: VisitorInit;
  private headers:    Record<string, Tree.Shape<Tree.GridHeader>> = {};
  private headersRow: Tree.Shape<Tree.GridHeaderRow> = {} as Tree.Shape<Tree.GridHeaderRow>;
  private rows:       Record<string, Tree.Shape<Tree.GridRow>> = {};
  private cells:      Record<string, Tree.Shape<Tree.GridCell>> = {};
  private dimensions: Record<string, Tree.DimensionsTested> = {};
  
  constructor(init: VisitorInit) {
    this._init = init;
  }
  
  visit(props: {
      dimensions: Record<string, Tree.DimensionsTested>;
      headers: Record<string, Tree.GridHeader>;
      cells: Record<string, Tree.GridCell>; 
      rows: Record<string, Tree.GridRow>;
    }): Tree.GridShapes {

    const all: Record<string, Tree.Node> = Object.assign({}, props.cells, props.headers, props.rows);
    const context = new Immutables.Context(all, this._init.coords)
    this.dimensions = props.dimensions;
    
    const headers = this.visitHeaders(Object.values(props.headers), context);
    const next = context.addNode(headers.shape.node, headers.shape.connectors);
    this.visitRows(Object.values(props.rows), next);

    return new Immutables.GridShapes({
      id:       this._init.id,
      rows:     this.rows,
      cells:    this.cells,
      nodes:    all,
      headers:  this.headers,
      headersRow: headers.shape as Tree.Shape<Tree.GridHeaderRow>
    });
  }
  
  visitHeaders(nodes: Tree.GridHeader[], context: Tree.VisitorContext): Visited {
    nodes.sort((n0, n1) => (n0.order > n1.order) ? 1 : -1);
    const row = new Immutables.GridHeaderRow({
      id: "headers",
      order: -1,
      typography: { text: "headers" },
      cells: nodes
    });
    
    const outgoing: string[] = [];
    let width: number = 0;
    let height: number = 0;
    let index: number = 0;
    let next: Tree.VisitorContext = context; 
    for(const header of nodes) {
      outgoing.push(header.id);
      const visited = this.visitHeader(header, next);
      next = next.addNode(visited.shape.node, visited.shape.connectors, { total: nodes.length, value: index++ }); 
      width += visited.shape.dimensions.width;
      
      if(height < visited.shape.dimensions.height) {
        height = visited.shape.dimensions.height;
      }
    }
    
    const dimensions = new Immutables.Dimensions({width, height});
    const shape = new Immutables.Shape({
      node: row, dimensions: dimensions,
      connectors: new Immutables.Connectors(dimensions, { topLeft: this._init.coords }),
      associations: new Immutables.Associations({outgoing: outgoing})
    });
    this.headersRow = shape;
    return { shape };
  }
  visitHeader(node: Tree.GridHeader, context: Tree.VisitorContext): Visited {
    const tested = this.dimensions[node.id];
    const dimensions = new Immutables.Dimensions(tested.dimensions);
    const shape = new Immutables.Shape({
      node: node,
      dimensions: dimensions,
      connectors: new Immutables.Connectors(dimensions, {
        topLeft: { 
          x: context.connectors.right.x, 
          y: context.connectors.top.y }}),
      associations: new Immutables.Associations({outgoing: ["headers"]})
    });
    this.headers[shape.id] = shape;
    return { shape };
  }
  visitRows(nodes: Tree.GridRow[], context: Tree.VisitorContext): Visited {
    nodes.sort((n0, n1) => (n0.order > n1.order) ? 1 : -1);
    const width: number = this.headersRow.dimensions.width;
    let height: number = 0;
    let index: number = 0;
    let next: Tree.VisitorContext = context;
    
    for(const row of nodes) {
      const visited = this.visitRow(row, next);
      next = next.addNode(visited.shape.node, visited.shape.connectors, { total: nodes.length, value: index++ });
      height += visited.shape.dimensions.height;
    }
    
    return {} as Visited;
  }
  visitRow(node: Tree.GridRow, context: Tree.VisitorContext): Visited {
    const width: number = this.headersRow.dimensions.width;
    const outgoing: string[] = [];
    
    const topLeft = {
      x: context.connectors.left.x,
      y: context.connectors.bottom.y
    };
          
    let height: number = 0;
    let next: Tree.VisitorContext = context
      .addNode(node, new Immutables.Connectors(
        {height, width: 0}, 
        {topLeft: topLeft}));
    
    for(const headerId of this.headersRow.associations.outgoing) {
      const cell = node.getByHeader(headerId);
      if(!cell) {
        throw new Error(`No cell for column: ${headerId} for row: ${JSON.stringify(node, null, 2)}!`);
      }
      const visited = this.visitCell(cell, next)
      outgoing.push(cell.id);
      next = next.addNode(visited.shape.node, visited.shape.connectors);
      
      if(height < visited.shape.dimensions.height) {
        height = visited.shape.dimensions.height;
      }
    }
    
    const dimensions = new Immutables.Dimensions({width, height});
    const shape = new Immutables.Shape({
      node: node,
      dimensions: dimensions,
      connectors: new Immutables.Connectors(dimensions, { topLeft: topLeft }),
      associations: new Immutables.Associations({outgoing: outgoing})
    });
    this.rows[shape.id] = shape;
    return { shape: shape }
  }
  visitCell(node: Tree.GridCell, context: Tree.VisitorContext): Visited {
    const dimensions = new Immutables.Dimensions(this.dimensions[node.id].dimensions);
    const shape = new Immutables.Shape({
      node: node,
      dimensions: dimensions,
      connectors: new Immutables.Connectors(dimensions, { 
        topLeft: {
          x: context.connectors.right.x, 
          y: context.connectors.top.y, 
        } 
      }),
      associations: new Immutables.Associations({outgoing: [node.rowId, node.headerId]})
    });
    
    this.cells[shape.id] = shape;
    return { shape: shape }
  }
  visitTypography(node: Tree.Typography, context: Tree.VisitorContext): Visited {
    throw new Error("not implemented");
  }
  visitNode(node: Tree.Node, context: Tree.VisitorContext): Visited {
    throw new Error("not implemented"); 
  }
  visitCells(nodes: Tree.GridCell[], context: Tree.VisitorContext): Visited {
    throw new Error("not implemented");
  }
}

export default GridShapeVisitorDefault;