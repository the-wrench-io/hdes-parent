import React from 'react';


import Context from '../Context';
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

interface FlowSvgProps {
  colors?: {
    dt: string;
    st: string;
    fl: string;
  }
};

const FlowSvg: React.FC<FlowSvgProps> = ({ colors }) => {
  const theme = { fill: themeColors.background, stroke: themeColors.chalky, background: themeColors.background};
  const view = FlowFactory.nodes()
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
// console.log(tree);
// console.log(view);
const tree = FlowFactory.nodes()
  .start({id: "decide-claim"})
  .decision({id: "decide-claim"}, {id: "calculate-collision"})
  .decision({id: "calculate-collision"}, {id: "final-calculation"})
  .service({id: "final-calculation"}, {id: "end-claim"})
  .end({id: "end-claim"})
  .build();
*/

  const shapes = FlowFactory.shapes().tree(view).start({ x: 250, y: 40 }).build();
  return (<Context.Provider theme={theme} 
    init={(theme, snap) => FlowFactory.renderer().shapes(shapes).snap(snap).theme(theme).build() }
    svg={{
      viewBox: "0 0 500 800", width: "100%", height: "800",
      style: { backgroundColor: themeColors.background },
    }}>
  </Context.Provider>);
}


export default FlowSvg;
