import { Tree, Immutables } from '../Tree'

class GridBuilderDefault implements Tree.GridBuilder {
  private _tester: Tree.DimensionsTester;
  private _cell: { min: Tree.Dimensions, max: Tree.Dimensions };
  
  constructor(
    tester: Tree.DimensionsTester, 
    cell: { min: Tree.Dimensions, max: Tree.Dimensions }) {
    
    this._tester = tester;
    this._cell = cell;
  }
  
  row(id: string, init: Tree.InitRow): Tree.GridBuilder {
    return this;
  }
  cell(id: string, init: Tree.InitCell): Tree.GridBuilder {
    return this;
  }
  header(id: string, init: Tree.InitHeader): Tree.GridBuilder {
    return this;
  }
  build(): Tree.GridShapes {
    return {} as Tree.GridShapes;
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