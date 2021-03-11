import React from 'react';
import { useTheme, useSnap } from '../../Context';
import { FlowAst } from '../Ast';

interface Props {
  cords: { x: number, y: number };
  node: FlowAst.Node;
  size: {height: number, width: number};
  clock?: boolean;
  decision?: boolean;
  service?: boolean;
  onClick?: () => void;
};

const TaskShape: React.FC<Props> = ({cords, size, clock, decision, service, node}) => {
  const theme = useTheme();
  const snap = useSnap();
  const {x, y} = cords;
  
  const rect = snap.rect(x - size.width/2, y - size.height/2, size.width, size.height);
  rect.attr({
      fill: theme.fill, 
      stroke: theme.stroke,
      filter: "url(#dropshadow)"
    });

  const lable = snap.text(x-size.width/2, y, node.content);
  lable.attr({
    stroke: theme.stroke    
  });

  const group: Snap.Paper = snap.group(rect, lable);
  if(clock) {
    const icon = {x: x+size.width/2-10, y: y-8};
    const use = snap.el("use", {
      "xlink:href": "#AccessTime", 
      transform: `translate(${icon.x}, ${icon.y})`,
      fill: theme.fill, 
      stroke: theme.stroke});
    group.add(use);
  }
  
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

  return null;
}

export default TaskShape;
