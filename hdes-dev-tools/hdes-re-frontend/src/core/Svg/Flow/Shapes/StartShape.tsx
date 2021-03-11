import React from 'react';
import { FlowAst } from '../Ast';

import { useTheme, useSnap } from '../../Context';

interface Props {
  node: FlowAst.Node;
  cords: { x: number, y: number },
  size: { height: number },
  onClick?: () => void;
};

const StartShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  const snap = useSnap();
  const r = size.height/2;
    
  snap.circle(cords.x, cords.y, r)
    .attr({
      pointerEvents: "all",
      filter: "url(#dropshadow)",
      fill: theme.fill, 
      stroke: theme.stroke
    });
  return null;
}

export default StartShape;
