import React from 'react';
import { SvgProvider as Provider, SvgContext as Type } from './SvgContext';

const useTheme = () => {
  const { theme } = React.useContext(Type);
  return theme;
};

const Context = { Provider, Type, useTheme };

export {useTheme};
export default Context;
