import { MutableRefObject } from 'react';
import Snap from 'snapsvg-cjs-ts';
import { Tree } from './Tree';
import { Plugins, initPlugins } from './Plugins';


interface HdesSnap extends Snap.Paper, Plugins {
  
}

const createSnap = (ref: React.RefObject<SVGSVGElement>) => {
  if(!ref.current) {
    throw new Error("SVG ref must be defined!");
  }
  initPlugins();
  
  const snapInput: MutableRefObject<SVGElement> = ref as MutableRefObject<SVGElement>;
  const newSnap = Snap(snapInput.current) as HdesSnap;
  return newSnap;
};


export { createSnap };
export type { HdesSnap, Tree }
