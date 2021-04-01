import Snap from 'snapsvg-cjs-ts';
import { Tree } from './Tree';
import { render as Typography } from './Typography';
import { render as Grid, GridInit } from './Grid';
import { render as Graph, GraphInit } from './Graph';

interface TypographyInit {
  center: { x: number, y: number },
  size: Tree.Dimensions,
  text: string, max_width: number,
  attributes: {}
}

interface Plugins {
  typography: (init: TypographyInit) => Snap.Element
  grid: (init: GridInit, data: (builder: Tree.GridBuilder) => Tree.GridShapes) => Snap.Element;
  graph: (init: GraphInit, data: (builder: Tree.GraphBuilder) => Tree.GraphShapes) => Snap.Element;
}


const cache: Record<string, {
  dimensions: Tree.Dimensions,
  avg: number
}> = {};


const tester = (props: {
  words: string; 
  limits: { max: Tree.Dimensions, min: Tree.Dimensions };
  attr: {};
  svg: Snap.Paper;
  }): { dimensions: Tree.Dimensions, avg: number } => {
  
  const cacheKey = props.words + JSON.stringify(props.attr);
  if(cache[cacheKey]) {
    return cache[cacheKey];
  }
  
  const temp = props.svg.text(0, 0, props.words);
  temp.attr(props.attr);
  
  const box = temp.getBBox();
  const dimensions = { width: box.width, height: box.height };
  const avg = dimensions.width/props.words.length;
  const result = { dimensions, avg };
  cache[cacheKey] = result;
  return result;
}

const initPlugins = () => {
  Snap.plugin((Snap: any, Element: any, Paper: any, glob: any) => {
    
    Paper.prototype.typography = function(init: TypographyInit) {
      const svg = Snap();
      const typography = Typography(svg, { 
        text: init.text, 
        size: init.size, 
        center: init.center, 
        attr: init.attributes,
        tester: (words) => tester({words, svg, attr: init.attributes, limits: {min: init.size, max: init.size} })
      });
      svg.remove();
        
      return typography;
    }
    
    Paper.prototype.grid = function(init: GridInit, data: (builder: Tree.GridBuilder) => Tree.GridShapes): Snap.Element {
      const svg = Snap();
      const dimensions = (props: {text: string, limits: { max: Tree.Dimensions, min: Tree.Dimensions }}) => 
        tester({words: props.text, svg, attr: init.typography.attr, limits: props.limits});
        
      const grid = Grid(svg, init, dimensions, data);
      svg.remove();
      return grid;
    }

    Paper.prototype.graph = function(init: GraphInit, data: (builder: Tree.GraphBuilder) => Tree.GraphShapes): Snap.Element {
      const svg = Snap();
      
      const dimensions = (props: {text: string, limits: { max: Tree.Dimensions, min: Tree.Dimensions }}) => 
        tester({words: props.text, svg, attr: init.typography.attr, limits: props.limits});
        
      const graph = Graph(svg, init, dimensions, data);
      svg.remove();
      return graph;
    }

     
  });
}

export type { Plugins };
export { initPlugins };