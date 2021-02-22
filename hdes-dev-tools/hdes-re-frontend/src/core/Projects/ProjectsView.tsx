import React from 'react';
import List from '@material-ui/core/List';

import { Resources } from '../Resources';
import ProjectView from './ProjectView';


interface ProjectsViewProps {
};


const ProjectsView: React.FC<ProjectsViewProps> = () => {
  const { session } = React.useContext(Resources.Context);

  const projects = session.data.projects.map(r => <ProjectView key={r.project.id} project={r}/>);

  return (<div><List dense={true} disablePadding>{projects}</List></div>);
}

export default ProjectsView;
