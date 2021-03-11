import React from 'react';
import { SvgProvider as Provider, SvgContext as Type } from './SvgContext';

const useTheme = () => {
  const { theme } = React.useContext(Type);
  return theme;
};
const useSnap = () => {
  const { snap } = React.useContext(Type);
  return snap;
};

const Context = { Provider, Type, useTheme, useSnap };

export {useTheme, useSnap};
export default Context;
