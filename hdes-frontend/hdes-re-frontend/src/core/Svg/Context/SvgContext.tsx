import React from "react";
import SvgIcons from './SvgIcons';

import { createSnap, HdesSnap, Tree } from '../Snap'

type SvgContextType = {
  theme: Tree.Theme;
  snap: HdesSnap;
}

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



const SvgContext = React.createContext<SvgContextType>({
  theme: {
    stroke: "#0000",
    fill: "#0000",
    background: "#0000"
  },
  snap: {} as HdesSnap
});

type SvgProviderProps = {
  theme: Tree.Theme;
  children: React.ReactNode;
  init: (theme: Tree.Theme, snap: HdesSnap) => void;
};


const SvgProvider: React.FC<SvgProviderProps> = ({ children, theme, init }) => {
  const ref = React.useRef<SVGSVGElement>() as React.RefObject<SVGSVGElement>;
  const [snap, setSnap] = React.useState<HdesSnap>(); 

  React.useEffect(() => {
    if(!snap && ref.current) {
      const newSnap = createSnap(ref);
      if(init !== undefined) {
        init(theme, newSnap);
      }
      setSnap(newSnap);
    }
  }, [ref, snap, init, theme]);
  

  const contextSnap = snap ? snap : {} as HdesSnap;
  return (
    <SvgContext.Provider value={{ theme, snap: contextSnap }}>
      <svg ref={ref} 
        viewBox="0 0 500 800" width="100%" height="800"
        style={{ backgroundColor: theme.background }}>
        <defs>
          <filter id="dropshadow" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
            {feFunc(theme.stroke)}
            <feGaussianBlur stdDeviation="10" />
            <feOffset dx="0" dy="0" result="shadow" />
            <feComposite in="SourceGraphic" in2="shadow" operator="over" />
          </filter>

          <SvgIcons fill={theme.fill}/>
        </defs>
        {snap ? children : null}
      </svg>
    </SvgContext.Provider>
  );
};

export { SvgProvider, SvgContext };


