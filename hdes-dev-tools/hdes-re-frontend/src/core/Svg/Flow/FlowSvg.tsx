import React from 'react';
import Context from '../Context';
import Shapes from './Shapes';
import FlowFactory from './Ast';

const themeColors = {
  chalky: "#e5c07b",
  coral: "#e06c75",
  picarm: "#cc0054",  //--pictorial carmine
  ivory: "#abb2bf",
  stone: "#5c6370",
  malibu: "#61afef",
  sage: "#98c379",
  whiskey: "#d19a66",
  violet: "#c678dd",

  invalid: '#ff206e',
  background: '#0c0f0a',
  selection: "#405948", //feldgraun
  cursor: "#528bff" // united nations blue
};

const hexToRgb = (hex: string) => {
  const result = hex.replace(/^#?([a-f\d])([a-f\d])([a-f\d])$/i, (m, r, g, b) => '#' + r + r + g + g + b + b).substring(1);
  const newLocal = result?.match(/.{2}/g);
  return newLocal?.map(x => parseInt(x, 16)) as number[]
}
const feFunc = (hex: string) => {
  const colors = hexToRgb(hex);
  return (
    <feComponentTransfer in="SourceAlpha">
      <feFuncR type="discrete" tableValues={colors[0]} />
      <feFuncG type="discrete" tableValues={colors[1]} />
      <feFuncB type="discrete" tableValues={colors[2]} />
    </feComponentTransfer>);
}

interface FlowSvgProps {
  colors?: {
    dt: string;
    st: string;
    fl: string;
  }
};

const FlowSvg: React.FC<FlowSvgProps> = ({ colors }) => {
  const theme = { fill: themeColors.background, stroke: themeColors.chalky };


  const tree = FlowFactory.nodes()
    .start({ id: "decide-claim" })
    .switch({ id: "decide-claim", onClick: () => console.log("clicked") }, [
      { id: "collision-claim" },
      { id: "vandalism-claim" },
      { id: "felloffroad-claim" }])
    .decision({ id: "collision-claim" }, { id: "calculate-collision" })
    .decision({ id: "calculate-collision" }, { id: "final-calculation" })
    .decision({ id: "vandalism-claim" }, { id: "calculate-vandalism" })
    .decision({ id: "calculate-vandalism" }, { id: "final-calculation" })
    .decision({ id: "felloffroad-claim" }, { id: "calculate-felloffroad" })
    .decision({ id: "calculate-felloffroad" }, { id: "final-calculation" })
    .service({ id: "final-calculation" }, { id: "end-claim" })
    .end({ id: "end-claim" })
    .build();
/*
const tree = FlowFactory.nodes()
  .start({id: "decide-claim"})
  .decision({id: "decide-claim"}, {id: "calculate-collision"})
  .decision({id: "calculate-collision"}, {id: "final-calculation"})
  .service({id: "final-calculation"}, {id: "end-claim"})
  .end({id: "end-claim"})
  .build();
*/

  const view = FlowFactory.shapes().tree(tree).start({ x: 250, y: 40 }).build();
  //console.log(tree);
  //console.log(view);

  const elements = tree.children.map(node => {
    const cords = view.shapes[node.id];
    if (!cords) {
      console.error("skipping: ", node);
      return null;
    }
    switch (node.type) {
      case "start": return <Shapes.Start cords={cords.center} size={cords.size} />;
      case "end": return <Shapes.End cords={cords.center} size={cords.size} />;
      case "switch": return <Shapes.Decision cords={cords.center} size={cords.size} />;
      case "decision-loop": return <Shapes.Task decision cords={cords.center} size={cords.size} />;
      case "decision": return <Shapes.Task service cords={cords.center} size={cords.size} />;
      case "service-loop": return <Shapes.Task service cords={cords.center} size={cords.size} />;
      case "service": return <Shapes.Task service cords={cords.center} size={cords.size} />;
      default: return null;
    }
  }).filter(e => e != null);

  return (<Context.Provider theme={theme}>
    <svg viewBox="0 0 500 800"
      style={{ backgroundColor: themeColors.background }}
      width="100%" height="800">

      <defs>
        <filter id="dropshadow" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
          {feFunc(theme.stroke)}
          <feGaussianBlur stdDeviation="10" />
          <feOffset dx="0" dy="0" result="shadow" />
          <feComposite in="SourceGraphic" in2="shadow" operator="over" />
        </filter>
      </defs>

      <g>{elements.map((element, index) => (<React.Fragment key={index}>{element}</React.Fragment>))}</g>
    </svg>
  </Context.Provider>);

}

export default FlowSvg;