import React from 'react';
import Context from '../Context';
import Shapes from './Shapes';

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
  return (<Context.Provider theme={theme}>
    <svg viewBox="0 0 500 200" style={{ backgroundColor: themeColors.background }}>
    <g>
      <Shapes.Start    cords={{x: 250, y: 40}}/>
      <Shapes.End      cords={{x: 250, y: 100}}/>
      <Shapes.Decision cords={{x: 125, y: 40}}/>
      <Shapes.Table    cords={{x: 125, y: 100}} size={{height: 50, width: 100}}/>
      </g>
    </svg>
  </Context.Provider>);

}

export default FlowTreeRenderer;
