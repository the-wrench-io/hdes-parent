import * as React from "react";


type Theme = {
  stroke: string;
  fill: string;
}

type SvgContextType = {
  theme: Theme;
}


const SvgContext = React.createContext<SvgContextType>({
  theme: {
    stroke: "#0000",
    fill: "#0000"
  },
});

type SvgProviderProps = {
  theme: Theme;
  children: React.ReactNode
};

const SvgProvider: React.FC<SvgProviderProps> = ({ children, theme }) => {
  return (
    <SvgContext.Provider value={{ theme }}>
      {children}
    </SvgContext.Provider>
  );
};

export { SvgProvider, SvgContext };