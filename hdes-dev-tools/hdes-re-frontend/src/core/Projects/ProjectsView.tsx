import React from 'react';
import Grid from '@material-ui/core/Grid';

import { Resources } from '../Resources';

interface ProjectsViewProps {
};

const ProjectsView: React.FC<ProjectsViewProps> = ({}) => {
  const { session } = React.useContext(Resources.Context);
  
  return (<div>PROJECTS</div>);
}

export default ProjectsView;
