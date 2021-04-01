import { Tree, Immutables, GraphMapper } from '../Tree'

interface Visited {

}

class GraphShapeVisitorDefault implements Tree.GraphShapeVisitor<Snap.Element, Visited> {
  private _snap: Snap.Paper;
  private _tester: Tree.DimensionsTester;
  private _theme: Tree.Theme;
  private _group: Snap.Element;
  private _shapes: Tree.GraphShapes = {} as Tree.GraphShapes;
  private _visited: string[] = [];
  
  constructor(snap: Snap.Paper, tester: Tree.DimensionsTester, theme: Tree.Theme) {
    this._snap = snap;
    this._tester = tester;
    this._theme = theme;
    this._group = this._snap.group();
  }
  visitShapes(shapes: Tree.GraphShapes, listeners: Tree.Listeners): Snap.Element {
    const start = shapes.shapes[shapes.start.id];
    const context = new Immutables.Context(shapes.nodes, start.connectors.center);
    this._shapes = shapes;
    this.visitNode(start, context);
    return this._group;
  }
  visitNode(shape: Tree.Shape<Tree.Node>, context: Tree.VisitorContext): Visited {
    return new GraphMapper<Visited>(shape.node)
      .start    (_target => this.visitStart(    shape as Tree.Shape<Tree.GraphStart>, context))
      .end      (_target => this.visitEnd(      shape as Tree.Shape<Tree.GraphEnd>, context))
      .switch   (_target => this.visitSwitch(   shape as Tree.Shape<Tree.GraphSwitch>, context))
      .service  (_target => this.visitService(  shape as Tree.Shape<Tree.GraphService>, context))
      .decision (_target => this.visitDecision( shape as Tree.Shape<Tree.GraphDecision>, context))
      .map();
  }
  visitStart(shape: Tree.Shape<Tree.GraphStart>, context: Tree.VisitorContext): Visited {
    const { height } = shape.dimensions;
    const { x, y } = shape.connectors.center;
    const r = Math.round(height / 2);
    const theme = this._theme;

    const circle = this._snap.circle(x, y, r)
    .attr({
      pointerEvents: "all",
      filter: "url(#dropshadow)",
      fill: theme.fill,
      stroke: theme.stroke
    });
    this._group.add(circle);
    
    const next = this._shapes.getById(shape.node.children.id);
    return this.visitNode(next, context.addNode(shape.node, shape.connectors));
  }
  visitEnd(node: Tree.Shape<Tree.GraphEnd>, context: Tree.VisitorContext): Visited {
    const theme = this._theme;

    const { height } = node.dimensions;
    const { x, y } = node.connectors.center;

    const r = Math.round(height / 2);
    const r1 = Math.round(r * 70 / 100);
    const strokeWidth = r - r1;

    const circle1 = this._snap.circle(x, y, r)
      .attr({
        fill: theme.fill,
        stroke: theme.stroke,
        strokeWidth: strokeWidth,
        pointerEvents: "all",
        //filter: "url(#dropshadow)",            
      });
    const circle2 = this._snap.circle(x, y, r1)
      .attr({
        fill: theme.stroke,
        stroke: theme.fill
      });
    this._group.add(circle1);
    this._group.add(circle2);
    return {};
  }
  visitSwitch(shape: Tree.Shape<Tree.GraphSwitch>, context: Tree.VisitorContext): Visited {
    const theme = this._theme;
    const snap = this._snap;
    const { x, y } = shape.connectors.center;

    const box = snap.el("use", {
      "xlink:href": "#CheckBoxOutlineBlank",
      transform: `scale(2.3, 2.3)  rotate(-45)`,
      fill: theme.stroke,
      stroke: theme.fill,
    });

    const games = snap.el("use", {
      "xlink:href": "#Games",
      transform: `translate(22.5, -17) scale(2, 2)`,
      fill: theme.stroke,
      stroke: theme.stroke
    });

    const group = snap.group(box, games);
    group.attr({
      transform: `translate(${x - 35}, ${y}) scale(0.9, 0.9)`,
    })
    
    this._group.add(group)
    
    const total = shape.node.children.length;
    let previous: Tree.Shape<Tree.Node> = shape
    let value = 0;
    for(const child of shape.node.children) {
      const index: Tree.VisitorIndex = { total, value: value++, previous: previous.node };
      const next = context.addNode(previous.node, previous.connectors, index);
      previous = this._shapes.getById(child.id);
      this.visitNode(previous, next);
    }
    return {};
  }
  visitDecision(node: Tree.Shape<Tree.GraphDecision>, context: Tree.VisitorContext): Visited {
    return this.visitTask(node, context); 
  }
  visitService(node: Tree.Shape<Tree.GraphService>, context: Tree.VisitorContext): Visited {
    return this.visitTask(node, context); 
  }  
  visitLoop(node: Tree.Shape<Tree.GraphDecision | Tree.GraphService>, context: Tree.VisitorContext): Visited {
    return {};
  }
  
  visitTask(shape: Tree.Shape<Tree.GraphDecision | Tree.GraphService>, context: Tree.VisitorContext): Visited {
    if(this._visited.includes(shape.id)) {
      return {};
    }
    this._visited.push(shape.id)
    
    const theme = this._theme;
    const snap = this._snap;

    const { x, y } = shape.connectors.center;
    const { width, height } = shape.dimensions;
    const clock = true;
    const decision = shape.node.type === 'decision' || shape.node.type === 'decision-loop';
    const service = shape.node.type === 'service' || shape.node.type === 'service-loop';

    console.log("drawing rect: ", Math.round(x - width / 2) )

    const rect = snap.rect(
      Math.round(x - width / 2),
      Math.round(y - height / 2), 
    
    width, height);
    rect.attr({
      fill: theme.fill,
      stroke: theme.stroke,
      filter: "url(#dropshadow)"
    });

    const lable = this.visitText(shape, context)

    const group: Snap.Paper = snap.group(rect, lable);
    if (clock) {
      const icon = { x: Math.round(x + width / 2 - 10), y: y + 15 };
      const use = snap.el("use", {
        "xlink:href": "#AccessTime",
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill,
        stroke: theme.stroke
      });
      group.add(use);
    }

    if (decision) {
      const icon = { x: Math.round(x + width / 2 - 17), y: Math.round(y - height / 2) };
      const use = snap.el("use", {
        "xlink:href": "#TableChart",
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill,
        stroke: theme.stroke
      });
      group.add(use);
    }

    if (service) {
      const icon = { x: Math.round(x + width / 2 - 17), y: Math.round(y - height / 2) };
      const use = snap.el("use", {
        "xlink:href": "#SettingsApplications",
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill,
        stroke: theme.stroke
      });
      group.add(use);
    }
    
    this._group.add(group)
    
    const next = this._shapes.getById(shape.node.children.id);
    return this.visitNode(next, context.addNode(shape.node, shape.connectors));
  }
  visitText(shape: Tree.Shape<Tree.Node>, context: Tree.VisitorContext): Snap.Element {
    const snap = this._snap as unknown as { typography: ({}) => Snap.Element };
    const theme = this._theme;

    const lable = snap.typography({
      center: shape.connectors.center,
      size: shape.dimensions,
      max_width: shape.dimensions.width,
      attributes: {
        fontSize: '0.9em',
        fontWeight: 100,
        fill: theme.stroke,
        stroke: theme.stroke,
        alignmentBaseline: "middle",
        textAnchor: "middle"
      },
      text: shape.node.typography.name
    });
    lable.attr({
      stroke: theme.stroke
    });
    return lable;
  }
  visitTypography(node: Tree.Typography, context: Tree.VisitorContext): Visited {
    return {};
  }
}

export default GraphShapeVisitorDefault;