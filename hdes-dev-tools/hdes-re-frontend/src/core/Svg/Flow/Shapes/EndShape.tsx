import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number }
};

const EndShape: React.FC<Props> = ({cords}) => {
  const theme = useTheme();
  return (<React.Fragment>
    <ellipse cx={cords.x} cy={cords.y} fill={theme.stroke} stroke={theme.fill} rx="25" ry="25" stroke-width="3" pointer-events="all"/>
    <ellipse cx={cords.x} cy={cords.y} fill={theme.stroke} stroke={theme.fill} rx="22.5" ry="22.5" pointer-events="all"/>
  </React.Fragment>);
}

export default EndShape;
