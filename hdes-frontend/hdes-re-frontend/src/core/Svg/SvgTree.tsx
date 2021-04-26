import React from 'react';

import Context from './Context';
import { HdesSnap, Tree } from './Snap'


const colors = {
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

interface SvgTreeProps {
  coords: Tree.Coordinates;
  node: { min: Tree.Dimensions, max: Tree.Dimensions };
  listeners: Tree.Listeners;
  graph?: (data: Tree.GraphBuilder) => Tree.GraphShapes;
  grid?: (data: Tree.GridBuilder) => Tree.GridShapes;
};

const theme = { fill: colors.background, stroke: colors.chalky, background: colors.background };

const init = (
  theme: Tree.Theme, 
  snap: HdesSnap,
  listeners: Tree.Listeners,
  node: { min: Tree.Dimensions, max: Tree.Dimensions },
  coords: Tree.Coordinates, 
  render: {
    graph?: (data: Tree.GraphBuilder) => Tree.GraphShapes;
    grid?: (data: Tree.GridBuilder) => Tree.GridShapes
  }) => {
  if(!render.graph && !render.grid) {
    throw new Error("Expecting one of: 'graph' or 'grid' to be defined but was null!");
  }
  if(render.grid) {
    const elements = snap.grid({
      node, theme, coords,
      listeners: listeners,
      typography: { attr: {
        fontSize: '0.9em', fontWeight: 100
      } }
    }, render.grid);
    
    snap.add(elements);
  } else if(render.graph) {
    
    const elements = snap.graph({
      node, theme, coords,
      listeners: listeners,
      typography: { attr: {
        fontSize: '0.9em', fontWeight: 100
      } }
    }, render.graph);
    snap.add(elements);    
  }
}

const SvgTree: React.FC<SvgTreeProps> = (props) => {
  return (
    <Context.Provider theme={theme} init={
      (theme, snap) => (
        init(
          theme, snap, props.listeners, props.node, props.coords,
          { graph: props.graph, grid: props.grid }) 
      )}>
    </Context.Provider>);
}


export type { SvgTreeProps };
export { SvgTree };
