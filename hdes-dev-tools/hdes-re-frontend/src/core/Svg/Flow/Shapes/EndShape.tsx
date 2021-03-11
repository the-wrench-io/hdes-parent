import React from 'react';
import { useTheme, useSnap } from '../../Context';

interface Props {
  cords: { x: number, y: number },
  size: { height: number },
  onClick?: () => void;
};

const EndShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  const snap = useSnap();
  
  const r = size.height/2;
  const r1 = r*70/100;
  const strokeWidth = r-r1;
  
  
  snap.circle(cords.x, cords.y, r)
  .attr({
    fill: theme.fill, 
    stroke: theme.stroke,
    strokeWidth: strokeWidth,
    pointerEvents: "all",
    //filter: "url(#dropshadow)",            
  });
  snap.circle(cords.x, cords.y, r1)
  .attr({
    fill: theme.stroke, 
    stroke: theme.fill
  });
  
  return (null);
}

export default EndShape;
