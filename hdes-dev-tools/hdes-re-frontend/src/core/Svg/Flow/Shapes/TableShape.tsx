import React from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number }
  size: {height: number, width: number}
};

const R = 25;

const TableShape: React.FC<Props> = ({cords, size}) => {
  const theme = useTheme();
  const {x, y} = cords;
  
  
  return (<React.Fragment>
    <svg x={x - size.width/2} y={y - size.height/2} fill={theme.fill} stroke={theme.stroke}>
      <rect width={size.width} height={size.height} pointerEvents="all"/>      
      <svg x={size.width-15} y={size.height/2 - 16}>  
        <g transform="translate(16,16)">
          <ellipse rx="15" ry="15" pointerEvents="all"/>
          <ellipse rx="11.818181818181818" ry="11.818181818181818"  pointerEvents="all"/>
        </g>
        <g transform="translate(-104,-114)">
          <path d="M 120 130 L 120.61 119.85 M 120 130 L 126.67 130 M 120 118.18 L 120 119.7 M 125.91 119.73 L 125.06 121.21 M 130.21 124.03 L 128.73 124.94 M 131.82 130 L 130.3 130 M 130.21 135.97 L 128.73 135.06 M 114.09 119.73 L 114.94 121.21 M 125.91 140.27 L 125.06 138.79 M 120 140.3 L 120 141.82 M 114.09 140.27 L 114.94 138.79 M 109.79 135.97 L 111.27 135.06 M 108.18 130 L 109.7 130 M 109.79 124.03 L 111.27 124.94" 
            strokeMiterlimit="10" pointerEvents="all"/>
        </g>
      </svg>
    </svg>
  </React.Fragment>);
}

export default TableShape;
