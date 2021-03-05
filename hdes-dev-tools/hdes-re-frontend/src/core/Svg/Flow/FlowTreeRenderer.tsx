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

interface FlowTreeRendererProps {
  colors?: {
    dt: string;
    st: string;
    fl: string;
  }
};

const FlowTreeRenderer: React.FC<FlowTreeRendererProps> = ({colors}) => {
  const theme = {fill: themeColors.background, stroke: themeColors.chalky };
  
  const tree = FlowFactory.root()
    .start({id: "decide-claim"})
    .switch({id: "decide-claim"}, [
      {id: "collision-claim"}, 
      {id: "vandalism-claim"}, 
      {id: "felloffroad-claim"}])
    .decision({id: "collision-claim"}, {id: "calculate-collision"})
      .decision({id: "calculate-collision"}, {id: "final-calculation"})
    .decision({id: "vandalism-claim"}, {id: "calculate-vandalism"})
      .decision({id: "calculate-vandalism"}, {id: "final-calculation"})
    .decision({id: "felloffroad-claim"}, {id: "calculate-felloffroad"})
      .decision({id: "calculate-felloffroad"}, {id: "final-calculation"})
    .service({id: "final-calculation"}, {id: "end-claim"})
    .end({id: "end-claim"})
    .build();
  
  
/*

      <Shapes.Start    cords={{x: 250, y: 40}}/>
      <Shapes.End      cords={{x: 250, y: 100}}/>
      <Shapes.Decision cords={{x: 125, y: 40}}/>
      <Shapes.Task     cords={{x: 125, y: 100}} size={{height: 50, width: 100}} clock decision service/>

*/
  const view = FlowFactory.view().tree(tree).start({x: 250, y: 40}).build();
  console.log(tree);
  console.log(view);
  
  const elements = tree.children.map(node => {
    const cords = view.nodes[node.id];
    if(!cords) {
      console.error("skipping: ", node);
      return null;
    }
    
    switch(node.type) {
      case "start":         return <Shapes.Start cords={cords.center} size={cords.size}/>;
      case "end":           return <Shapes.End cords={cords.center} size={cords.size}/>;
      case "switch":        return <Shapes.Decision cords={cords.center} size={cords.size} />;
      case "decision-loop": return <Shapes.Task decision cords={cords.center} size={cords.size} />;
      case "decision":      return <Shapes.Task service cords={cords.center} size={cords.size}/>;
      case "service-loop":  return <Shapes.Task service cords={cords.center} size={cords.size}/>;
      case "service":       return <Shapes.Task service cords={cords.center} size={cords.size}/>;
    }
  }).filter(e => e != null);
  
  return (<Context.Provider theme={theme}>
    <svg viewBox="0 0 500 600" 
      style={{ backgroundColor: themeColors.background }} 
      width="100%" height="800">
      
      <g>{elements.map((element, index) => (<React.Fragment key={index}>{element}</React.Fragment>))}</g>
    </svg>
  </Context.Provider>);

}

export default FlowTreeRenderer;
