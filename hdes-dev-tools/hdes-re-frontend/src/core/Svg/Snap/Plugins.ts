import Snap from 'snapsvg-cjs-ts';
import { render as Typography } from './Typography';

interface TypographyInit {
  center: { x: number, y: number },
  size: { width: number, height: number }
  text: string, max_width: number,
  attributes: {}
}

interface Plugins {
  typography: (init: TypographyInit) => Snap.Element
}

const initPlugins = () => {
  Snap.plugin((Snap: any, Element: any, Paper: any, glob: any) => {
    Paper.prototype.typography = function(init: TypographyInit) {
      const svg = Snap();
      const tester = (words: string) => {
        const temp = svg.text(0, 0, words);
        temp.attr(init.attributes);
        const box = temp.getBBox();
        svg.remove();
        return { width: box.width, height: box.height };
      }

      return Typography(svg, { 
        text: init.text, 
        size: init.size, 
        center: init.center, 
        attr: init.attributes,
        tester });
    }
  });
}

export type { Plugins };
export { initPlugins };