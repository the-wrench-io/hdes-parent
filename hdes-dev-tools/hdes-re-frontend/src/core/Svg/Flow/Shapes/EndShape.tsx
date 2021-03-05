import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number },
  size: { height: number },
};

const EndShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  
  const r = size.height/2;
  const r1 = r*70/100;
  const strokeWidth = r-r1;
  return (<React.Fragment>
    <ellipse cx={cords.x} cy={cords.y} fill={theme.stroke} stroke={theme.fill} rx={r} ry={r} strokeWidth={strokeWidth} pointerEvents="all"/>
    <ellipse cx={cords.x} cy={cords.y} fill={theme.stroke} stroke={theme.fill} rx={r1} ry={r1} pointerEvents="all"/>
  </React.Fragment>);
}

export default EndShape;
