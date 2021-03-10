import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number },
  size: { height: number },
  onClick?: () => void; 
};

const DecisionShape: React.FC<Props> = ({cords, size}) => {
  
  const R = size.height/2;
  const theme = useTheme();
  const {x, y} = cords;
  
  const diamond = (<path fill={theme.fill} stroke={theme.stroke} strokeMiterlimit="10" pointerEvents="all" d={`
      M ${x} ${y - R}
      L ${x + R} ${y}
      L ${x} ${y + R} 
      L ${x - R} ${y} Z`} />);
  
  const R1 = R*30/100;
  const R2 = R*54/100;
  const R3 = R*12/100;
  const R4 = R2-R3;
  const diamondIn = (<path fill={theme.stroke} stroke={theme.stroke} strokeMiterlimit="10" pointer-events="all" 
      d={`M ${x-R1} ${y-R2} L ${x} ${y-R3} L ${x+R1} ${y-R2} L ${x+R4} ${y-R4} L ${x+R3} ${y} L ${x+R4} ${y+R4} L ${x+R1} ${y+R2} L ${x} ${y+R3} L ${x-R1} ${y+R2} L ${x-R4} ${y+R4} L ${x-R3} ${y} L ${x-R4} ${y-R4} Z`} />);
  
  
  return (<React.Fragment>{diamond}{diamondIn}</React.Fragment>);
}

export default DecisionShape;
