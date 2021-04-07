import { Tree, Immutables } from '../Tree'
import GridNodeVisitorDefault from './GridNodeVisitorDefault';


interface BuilderInit {
  id: string;
  coords: Tree.Coordinates;
  tester: Tree.DimensionsTester;
  node: { min: Tree.Dimensions, max: Tree.Dimensions }
}



type GridTester = (text: Tree.Typography) => Tree.DimensionsTested;


class GridBuilderDefault implements Tree.GridBuilder {
  private _init:        BuilderInit;
  private _tester:      GridTester;
  private _rows:        Record<string, Tree.InitRow> = {};
  private _rowscells:   Record<string, Tree.GridCell[]> = {};
  private _cells:       Record<string, Tree.GridCell> = {};
  private _cellsPos:    Record<string, Tree.GridCell> = {};
  private _headers:     Record<string, Tree.GridHeader> = {};
  private _headersPos:  Record<number, Tree.GridHeader> = {};
  private _dimensions:  Record<string, Tree.DimensionsTested> = {};
  
  constructor(init: BuilderInit) {
    this._init = init;
    this._tester = (text: Tree.Typography) => init.tester({limits: init.node, text: text});
  }
  
  row(id: string, init: Tree.InitRow): Tree.GridBuilder {
    this._rows[id] = init;
    return this;
  }
  cell(id: string, init: Tree.InitCell): Tree.GridBuilder {
    if(this._cells[id]) {
      throw new Error(`Cell with id: '${id}' already defined!`);
    }
    const pos = init.headerId + "/" + init.rowId;
    if(this._cellsPos[pos]) {
      throw new Error(`Cell at positions header: '${init.headerId}', row: '${init.rowId}' is already defined!`);
    }
    
    const cell = new Immutables.GridCell({
      id: id,
      headerId: init.headerId,
      rowId: init.rowId,
      order: 0,
      typography: init.typography
    });
    this._cells[id] = cell;
    this._cellsPos[pos] = cell;
    this._dimensions[id] = this._tester(cell.typography);
    let cells = this._rowscells[init.rowId];
    if(!cells) {
      cells = [];
      this._rowscells[cell.rowId] = cells;
    }
    cells.push(cell);
    return this;
  }
  header(id: string, init: Tree.InitHeader): Tree.GridBuilder {
    if(this._headers[id]) {
      throw new Error(`Header with id: '${id}' already defined!`);
    }
    const pos = init.order;
    if(this._headersPos[pos]) {
      throw new Error(`Header at positions: '${init.order}' is already defined!`);
    }
    
    const header = new Immutables.GridHeader({
      id: id,
      kind: init.kind,
      order: init.order,
      typography: init.typography
    });
    
    this._dimensions[id] = this._tester(header.typography);
    this._headers[id] = header;
    this._headersPos[pos] = header;
    return this;
  }
  build(): Tree.GridShapes {
    const rows: Record<string, Tree.GridRow> = {};
    for(const rowId of Object.keys(this._rows)) {
      const init = this._rows[rowId];
      const cells = this._rowscells[rowId]
      const row = new Immutables.GridRow({
        id: rowId,
        order: init.order,
        typography: init.typography,
        cells: cells});
      rows[rowId] = row;
    }

    const props = {
      dimensions: this._dimensions, 
      headers: this._headers,
      cells: this._cells,
      rows: rows };
    return new GridNodeVisitorDefault(this._init).visit(props);
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

export default GridBuilderDefault;