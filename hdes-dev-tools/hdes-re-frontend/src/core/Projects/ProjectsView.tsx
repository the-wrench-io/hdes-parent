import React from 'react';
import List from '@material-ui/core/List';

import { Resources, Backend } from '../Resources';
import ProjectView from './ProjectView';


interface ProjectsViewProps {
  setWorkspace: (head: Backend.Head) => void
};


const ProjectsView: React.FC<ProjectsViewProps> = ({setWorkspace}) => {
  const { session } = React.useContext(Resources.Context);

  const projects = session.data.projects.map(r => <ProjectView key={r.project.id} project={r} setWorkspace={setWorkspace}/>);

  return (<div>
    <List dense={true} disablePadding>{projects}</List>
  </div>);
}

export default ProjectsView;
