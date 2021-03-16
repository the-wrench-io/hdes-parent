import { Ast } from './Ast';
import Snap from 'snapsvg-cjs-ts';
import {HdesSnap} from '../../Snap'

class DefaultContext implements Ast.RendererVisitorContext {
  private _visited: Record<string, Ast.Shape<Ast.Node>>;
  
  constructor() {
    this._visited = {};
  }
  get visited() {
    return Object.values(this._visited);
  }
  add(visited: Ast.Shape<Ast.Node>): Ast.RendererVisitorContext {
    this._visited[visited.id] = visited; 
    return this;
  }
}


class RendererVisitorDefault implements Ast.RendererVisitor {

  private _snap: HdesSnap;
  private _theme: Ast.ShapeRendererTheme;

  constructor(snap: HdesSnap, theme: Ast.ShapeRendererTheme) {
    this._snap = snap;
    this._theme = theme;
  }

  visitRoot(view: Ast.ShapeView): Ast.RendererVisitorState {
    const ctx: Ast.RendererVisitorContext = new DefaultContext();
    for(const shape of Object.values(view.shapes)) {
      switch(shape.node.type) {
      case "decision":
      case "decision-loop": this.visitDecision(shape as Ast.Shape<Ast.DecisionNode>, ctx); break;
      case "service":
      case "service-loop": this.visitService(shape as Ast.Shape<Ast.ServiceNode>, ctx); break;
      
      case "switch": this.visitSwitch(shape as Ast.Shape<Ast.SwitchNode>, ctx); break;
      case "start": this.visitStart(shape as Ast.Shape<Ast.StartNode>, ctx); break;
      case "end": this.visitEnd(shape as Ast.Shape<Ast.EndNode>, ctx); break;
      }
      
      ctx.add(shape);
    }
    return {};
  }
  visitStart(shape: Ast.Shape<Ast.StartNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    const theme = this._theme;
    const snap = this._snap;
    const {height} = shape.size;
    const {x, y} = shape.center;
    const r = height/2;
    
    snap.circle(x, y, r)
    .attr({
      pointerEvents: "all",
      filter: "url(#dropshadow)",
      fill: theme.fill, 
      stroke: theme.stroke
    });
    return {};
  }
  visitEnd(shape: Ast.Shape<Ast.EndNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    const theme = this._theme;
    const snap = this._snap;
    const {height} = shape.size;
    const {x, y} = shape.center;
    
    const r = height/2;
    const r1 = r*70/100;
    const strokeWidth = r-r1;
    
    snap.circle(x, y, r)
    .attr({
      fill: theme.fill, 
      stroke: theme.stroke,
      strokeWidth: strokeWidth,
      pointerEvents: "all",
      //filter: "url(#dropshadow)",            
    });
    snap.circle(x, y, r1)
    .attr({
      fill: theme.stroke, 
      stroke: theme.fill
    });
    return {};
  }
  visitSwitch(shape: Ast.Shape<Ast.SwitchNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    const theme = this._theme;
    const snap = this._snap;
    const {x, y} = shape.center;
    
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
      stroke: theme.stroke});
  
    const group = snap.group(box, games);
    group.attr({
      transform: `translate(${x-35}, ${y}) scale(0.9, 0.9)`,
    })
    return {};    
  }
  visitDecision(node: Ast.Shape<Ast.DecisionNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    return this.visitTask(node, context);
  }
  visitService(node: Ast.Shape<Ast.ServiceNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    return this.visitTask(node, context);
  }
  visitLoop(node: Ast.Shape<Ast.DecisionNode | Ast.ServiceNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    return this.visitTask(node, context);
  }
  visitText(shape: Ast.Shape<Ast.Node>, context: Ast.RendererVisitorContext): Snap.Element {
    const snap = this._snap;
    const theme = this._theme;
    
    const lable = snap.typography({
      center: shape.center,
      size: shape.size,
      max_width: shape.size.width,
      attributes: {
        fontSize: '0.9em',
        fontWeight: 100,
        fill: theme.stroke, 
        stroke: theme.stroke,
        alignmentBaseline: "middle", 
        textAnchor: "middle"
      },
      text: shape.node.content
    });
    lable.attr({
      stroke: theme.stroke    
    });
    return lable;
  }
  
  visitTask(shape: Ast.Shape<Ast.DecisionNode | Ast.ServiceNode>, context: Ast.RendererVisitorContext): Ast.RendererVisitorState {
    const theme = this._theme;
    const snap = this._snap;
    
    const {x, y} = shape.center;
    const {width, height} = shape.size;
    const {content} = shape.node;
    const clock = true;
    const decision = shape.node.type === 'decision' || shape.node.type === 'decision-loop';
    const service = shape.node.type === 'service' || shape.node.type === 'service-loop';
    
    const rect = snap.rect(x - width/2, y - height/2, width, height);
    rect.attr({
        fill: theme.fill, 
        stroke: theme.stroke,
        filter: "url(#dropshadow)"
      });
  
    const lable = this.visitText(shape, context)
  
    const group: Snap.Paper = snap.group(rect, lable);
    if(clock) {
      const icon = {x: x+width/2-10, y: y+15};
      const use = snap.el("use", {
        "xlink:href": "#AccessTime", 
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill, 
        stroke: theme.stroke});
      group.add(use);
    }
    
    if(decision) {
      const icon = {x: x+width/2-17, y: y-height/2};
      const use = snap.el("use", {
        "xlink:href": "#TableChart", 
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill, 
        stroke: theme.stroke});
      group.add(use);
    }
  
    if(service) {
      const icon = {x: x+width/2-17, y: y-height/2};
      const use = snap.el("use", {
        "xlink:href": "#SettingsApplications", 
        transform: `translate(${icon.x}, ${icon.y})`,
        fill: theme.fill, 
        stroke: theme.stroke});
      group.add(use);
    }
    
    return {};
  }
}

export default RendererVisitorDefault;