import * as React from "react";
import Snap from 'snapsvg-cjs-ts';


type Theme = {
  stroke: string;
  fill: string;
}

type SvgContextType = {
  theme: Theme;
  snap: Snap.Paper;
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
    fill: "#0000"
  },
  snap: {} as Snap.Paper
});

type SvgProviderProps = {
  theme: Theme;
  children: React.ReactNode;
  svg: React.SVGProps<SVGSVGElement>;
};


const SvgProvider: React.FC<SvgProviderProps> = ({ children, theme, svg }) => {
  const ref = React.useRef<SVGElement>();
  const [snap, setSnap] = React.useState<Snap.Paper>(); 

  const svgRef: React.RefObject<SVGSVGElement> = ref as unknown as React.RefObject<SVGSVGElement>;
  React.useEffect(() => {
    if(!snap && ref.current) {
      console.log(ref.current)
      const newSnap = Snap(ref.current);
      setSnap(newSnap);
    }
  }, [ref, snap]);

  const contextSnap = snap ? snap : {} as Snap.Paper;
  return (
    <SvgContext.Provider value={{ theme, snap: contextSnap }}>
      <svg ref={svgRef} {...svg}>
        <defs>
          <filter id="dropshadow" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
            {feFunc(theme.stroke)}
            <feGaussianBlur stdDeviation="10" />
            <feOffset dx="0" dy="0" result="shadow" />
            <feComposite in="SourceGraphic" in2="shadow" operator="over" />
          </filter>
        
          {/*  //'@material-ui/icons/AccessTime'; */}
          <g id="AccessTime">
            <g transform={`scale(0.8, 0.8)`}>
              <g transform={`translate(12, 12)`}>
                <ellipse rx="10" ry="10" pointerEvents="all" fill={theme.fill} stroke={"transparent"}/>
              </g> 
              <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"></path>
              <path d="M12.5 7H11v6l5.25 3.15.75-1.23-4.5-2.67z"></path>
            </g>      
          </g>
          
          {/*  //'@material-ui/icons/TableChart'; */}
          <g id="TableChart" transform={`scale(0.7, 0.7)`}>
            <path d="M10 10.02h5V21h-5zM17 21h3c1.1 0 2-.9 2-2v-9h-5v11zm3-18H5c-1.1 0-2 .9-2 2v3h19V5c0-1.1-.9-2-2-2zM3 19c0 1.1.9 2 2 2h3V10H3v9z"></path>
          </g>
          
          {/*  //'@material-ui/icons/SettingsApplications'; */}
          <g id="SettingsApplications" transform={`scale(0.7, 0.7)`}>
            <path d="M12 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm7-7H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.11 0 2-.9 2-2V5c0-1.1-.89-2-2-2zm-1.75 9c0 .23-.02.46-.05.68l1.48 1.16c.13.11.17.3.08.45l-1.4 2.42c-.09.15-.27.21-.43.15l-1.74-.7c-.36.28-.76.51-1.18.69l-.26 1.85c-.03.17-.18.3-.35.3h-2.8c-.17 0-.32-.13-.35-.29l-.26-1.85c-.43-.18-.82-.41-1.18-.69l-1.74.7c-.16.06-.34 0-.43-.15l-1.4-2.42c-.09-.15-.05-.34.08-.45l1.48-1.16c-.03-.23-.05-.46-.05-.69 0-.23.02-.46.05-.68l-1.48-1.16c-.13-.11-.17-.3-.08-.45l1.4-2.42c.09-.15.27-.21.43-.15l1.74.7c.36-.28.76-.51 1.18-.69l.26-1.85c.03-.17.18-.3.35-.3h2.8c.17 0 .32.13.35.29l.26 1.85c.43.18.82.41 1.18.69l1.74-.7c.16-.06.34 0 .43.15l1.4 2.42c.09.15.05.34-.08.45l-1.48 1.16c.03.23.05.46.05.69z"></path>
          </g>
        </defs>
        {snap ? children : null}
      </svg>
    </SvgContext.Provider>
  );
};

export { SvgProvider, SvgContext };