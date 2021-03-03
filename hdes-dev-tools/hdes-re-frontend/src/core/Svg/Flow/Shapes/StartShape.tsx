import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number },
  size: { height: number },
};

const StartShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  const r = size.height/2;
  return (
      <ellipse 
        cx={cords.x} cy={cords.y}
        rx={r} ry={r}
        fill={theme.fill} stroke={theme.stroke}
        pointer-events="all" />
    );

}

export default StartShape;
