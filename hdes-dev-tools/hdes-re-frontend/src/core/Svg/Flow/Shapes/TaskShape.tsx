import React, { ReactChild } from 'react';
import { useTheme, useSnap } from '../../Context';

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
  const snap = useSnap();
  const {x, y} = cords;
  
  const rect = snap.rect(x - size.width/2, y - size.height/2, size.width, size.height);
  rect
    .attr({
      fill: theme.fill, 
      stroke: theme.stroke,
      filter: "url(#dropshadow)"
    });
    
  const group: Snap.Paper = snap.group(rect);
  
  // '@material-ui/icons/AccessTime';
  if(clock) {
    const icon = {x: x+size.width/2-10, y: y-8};
    const use = snap.el("use", {
      "xlink:href": "#AccessTime", 
      transform: `translate(${icon.x}, ${icon.y})`,
      fill: theme.fill, 
      stroke: theme.stroke});
    group.add(use);
  }

  //'@material-ui/icons/TableChart';  
  if(decision) {
    const icon = {x: x-size.width/2, y: y+1-size.height/2};
    const use = snap.el("use", {
      "xlink:href": "#TableChart", 
      transform: `translate(${icon.x}, ${icon.y})`,
      fill: theme.fill, 
      stroke: theme.stroke});
    group.add(use);
  }

  if(service) {
    const icon = {x: x+size.width/2-18, y: y+1-size.height/2};
    const use = snap.el("use", {
      "xlink:href": "#SettingsApplications", 
      transform: `translate(${icon.x}, ${icon.y})`,
      fill: theme.fill, 
      stroke: theme.stroke});
    group.add(use);
  }

  /*
  return (<React.Fragment>
    <svg x={x - size.width/2} y={y - size.height/2} fill={theme.fill} stroke={theme.stroke}>
      <rect width={size.width} height={size.height} pointerEvents="all" style={{filter: "url(#dropshadow)"}}/>
      {icons.map((i, index) => (<React.Fragment key={index}>{i}</React.Fragment>))}
    </svg>
  </React.Fragment>);
  */
  return null;
}

export default TaskShape;
