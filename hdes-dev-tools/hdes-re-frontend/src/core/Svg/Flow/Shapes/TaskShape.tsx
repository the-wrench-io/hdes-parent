import React, { ReactChild } from 'react';
import { useTheme } from '../../Context';

interface Props {
  cords: { x: number, y: number };
  size: {height: number, width: number};
  clock?: boolean;
  decision?: boolean;
  service?: boolean;
  onClick?: () => void;
};

const TaskShape: React.FC<Props> = ({cords, size, clock, decision, service}) => {
  const theme = useTheme();
  const {x, y} = cords;
  const icons: ReactChild[] = [];
  
  // '@material-ui/icons/AccessTime';
  if(clock) {  
    const icon = {x: size.width, y: size.height/2};
    const clock = (<React.Fragment>
      <g transform={`translate(${icon.x}, ${icon.y})`}>
        <ellipse rx="8" ry="8" pointerEvents="all" fill={theme.fill} stroke={"transparent"}/>
      </g> 
      <g transform={`translate(${icon.x-10}, ${icon.y-10}) scale(0.8, 0.8)`}>
        <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"></path>
        <path d="M12.5 7H11v6l5.25 3.15.75-1.23-4.5-2.67z"></path>
      </g>      
    </React.Fragment>);
    icons.push(clock);
  }

  //'@material-ui/icons/TableChart';  
  if(decision) {
    const icon = {x: 0, y: 0};
    const table = (
      <g transform={`translate(${icon.x}, ${icon.y}) scale(0.7, 0.7)`}>
        <path d="M10 10.02h5V21h-5zM17 21h3c1.1 0 2-.9 2-2v-9h-5v11zm3-18H5c-1.1 0-2 .9-2 2v3h19V5c0-1.1-.9-2-2-2zM3 19c0 1.1.9 2 2 2h3V10H3v9z"></path>
      </g>
    );
    icons.push(table);
  }
  
  //'@material-ui/icons/SettingsApplications'';  
  if(service) {
    const icon = {x: size.width - 17, y: 0};
    const table = (
      <g transform={`translate(${icon.x}, ${icon.y}) scale(0.7, 0.7)`}>
        <path d="M12 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm7-7H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.11 0 2-.9 2-2V5c0-1.1-.89-2-2-2zm-1.75 9c0 .23-.02.46-.05.68l1.48 1.16c.13.11.17.3.08.45l-1.4 2.42c-.09.15-.27.21-.43.15l-1.74-.7c-.36.28-.76.51-1.18.69l-.26 1.85c-.03.17-.18.3-.35.3h-2.8c-.17 0-.32-.13-.35-.29l-.26-1.85c-.43-.18-.82-.41-1.18-.69l-1.74.7c-.16.06-.34 0-.43-.15l-1.4-2.42c-.09-.15-.05-.34.08-.45l1.48-1.16c-.03-.23-.05-.46-.05-.69 0-.23.02-.46.05-.68l-1.48-1.16c-.13-.11-.17-.3-.08-.45l1.4-2.42c.09-.15.27-.21.43-.15l1.74.7c.36-.28.76-.51 1.18-.69l.26-1.85c.03-.17.18-.3.35-.3h2.8c.17 0 .32.13.35.29l.26 1.85c.43.18.82.41 1.18.69l1.74-.7c.16-.06.34 0 .43.15l1.4 2.42c.09.15.05.34-.08.45l-1.48 1.16c.03.23.05.46.05.69z"></path>
      </g>
    );
    icons.push(table);
  }
  
  return (<React.Fragment>
    <svg x={x - size.width/2} y={y - size.height/2} fill={theme.fill} stroke={theme.stroke}>
      <rect width={size.width} height={size.height} pointerEvents="all"/>
      {icons.map((i, index) => (<React.Fragment key={index}>{i}</React.Fragment>))}
    </svg>
  </React.Fragment>);
}

export default TaskShape;
