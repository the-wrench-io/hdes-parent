import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number }
};

const StartShape: React.FC<Props> = ({cords}) => {
  const theme = useTheme();
  return (
      <ellipse 
        cx={cords.x} cy={cords.y}
        rx="25" ry="25"
        fill={theme.fill} stroke={theme.stroke}
        pointer-events="all" />
    );

}

export default StartShape;
