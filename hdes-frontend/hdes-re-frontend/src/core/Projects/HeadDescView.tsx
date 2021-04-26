import React from 'react';

import { Backend } from '../Resources';


interface HeadDescViewProps {
  project: Backend.ProjectResource,
  head: Backend.Head,
}

const HeadDescView: React.FC<HeadDescViewProps> = ({project, head}) => {
  const headState = project.states[head.name];
  
  if(head.name === 'main') {
    const ahead = Object.values(project.states)
      .filter(s => s.type === 'ahead')
      .map(s => `${s.head} by ${s.commits}`);
    if(ahead.length > 0) {
      return (<span>Main branch is behind of: {ahead.join(", ")} commits</span>);
    }
  } else if(headState.type === 'same') {
    return (<span>Same assets as in main</span>);
  } else if(headState.type === 'behind') {
    return (<span>Assets are behind of main by: {headState.commits} commits</span>);
  } else {
    return (<span>Assets are ahead of main by: {headState.commits} commits</span>);
  }
  return null;
}

export default HeadDescView;
