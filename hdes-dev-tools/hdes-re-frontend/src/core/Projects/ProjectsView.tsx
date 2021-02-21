import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';

import { Resources } from '../Resources';
import ProjectView from './ProjectView';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({

  }),
);

interface ProjectsViewProps {
};


const ProjectsView: React.FC<ProjectsViewProps> = ({}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);

  const projects = session.data.projects.map(r => <ProjectView key={r.project.id} project={r}/>);

  return (<div><List dense={true} disablePadding>{projects}</List></div>);
}

export default ProjectsView;
