import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number }
};

const R = 25;
const D = 7.5;

/**
    M 247.5   187.5
    L 255     197 
    L 262.5   187.5 
    
    L 264.5   190.25 
    L 257.23  200 
    L 264.5   209.75 
    L 262.5   212.5
    
    L 255     203 
    L 247.5   212.5 
    L 245.5   209.75 
    L 252.74  200 
    L 245.5   190.25 Z
 */

const DecisionShape: React.FC<Props> = ({cords}) => {
  const theme = useTheme();
  const {x, y} = cords;
  
  
  
  return (<React.Fragment>
    <path d={`
      M ${x} ${y - R}
      L ${x + R} ${y}
      L ${x} ${y + R} 
      L ${x - R} ${y} Z`} 
      fill={theme.fill} stroke={theme.stroke} strokeMiterlimit="10" pointer-events="all"/>
    <path d={`
    M 247.5   187.5
    L 255     197 
    L 262.5   187.5 
    
    L 264.5   190.25 
    L 257.23  200 
    L 264.5   209.75 
    L 262.5   212.5
    
    L 255     203 
    L 247.5   212.5 
    L 245.5   209.75 
    L 252.74  200 
    L 245.5   190.25 Z
    `} 
      fill={theme.stroke} stroke={theme.stroke} 
      stroke-miterlimit="10" pointer-events="all"/>
  </React.Fragment>);
}

export default DecisionShape;
