import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number },
  size: { height: number },
  onClick?: () => void;
};

const StartShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  const r = size.height/2;
  return (
      <ellipse style={{filter: "url(#dropshadow)"}}
        cx={cords.x} cy={cords.y}
        rx={r} ry={r}
        fill={theme.fill} stroke={theme.stroke}
        pointerEvents="all" />
    );

}

export default StartShape;
