import Snap from 'snapsvg-cjs-ts';
import { Tree } from './Tree';
import { render as Text, TextInit, TypographyDimensions } from './Text';
import { render as Grid, GridInit } from './Grid';
import { render as Graph, GraphInit } from './Graph';


interface Plugins {
  typography: (init: TextInit) => Snap.Element
  grid: (init: GridInit, data: (builder: Tree.GridBuilder) => Tree.GridShapes) => Snap.Element;
  graph: (init: GraphInit, data: (builder: Tree.GraphBuilder) => Tree.GraphShapes) => Snap.Element;
}

const initPlugins = () => {
  Snap.plugin((Snap: any, Element: any, Paper: any, glob: any) => {
    
    Paper.prototype.typography = function(init: TextInit) {
      const svg = Snap();
      const typography = Text(svg, init);
      svg.remove();
      return typography;
    }
    
    Paper.prototype.grid = function(init: GridInit, data: (builder: Tree.GridBuilder) => Tree.GridShapes): Snap.Element {
      const svg = Snap();
      const dimensions = (props: {
        text: Tree.Typography,
        limits: { 
          max: Tree.Dimensions, 
          min: Tree.Dimensions 
      }}) => {
        const config = { words: props.text, limits: props.limits, attr: init.typography.attr };
        return TypographyDimensions(svg, config); 
      }
      const grid = Grid(svg, init, dimensions, data);
      svg.remove();
      return grid;
    }

    Paper.prototype.graph = function(init: GraphInit, data: (builder: Tree.GraphBuilder) => Tree.GraphShapes): Snap.Element {
      const svg = Snap();
      
      const dimensions = (props: {
        text: Tree.Typography, 
        limits: { 
          max: Tree.Dimensions, 
          min: Tree.Dimensions 
      }}) =>  {
        const config = { words: props.text, limits: props.limits, attr: init.typography.attr };
        return TypographyDimensions(svg, config);
      }
        
      const graph = Graph(svg, init, dimensions, data);
      svg.remove();
      return graph;
    }

     
  });
}

export type { Plugins };
export { initPlugins };