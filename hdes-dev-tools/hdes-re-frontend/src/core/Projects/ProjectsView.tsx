import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import FolderIcon from '@material-ui/icons/Folder';
import DeleteIcon from '@material-ui/icons/Delete';
import GetAppIcon from '@material-ui/icons/GetApp';
import Tooltip from '@material-ui/core/Tooltip';

import { Resources } from '../Resources';
import ProjectItemView from './ProjectItemView';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({

  }),
);

interface ProjectsViewProps {
};


const ProjectsView: React.FC<ProjectsViewProps> = ({}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);

  const projects = session.data.projects.map(r => <ProjectItemView key={r.project.id} project={r}/>);

  return (<div>
      <List dense={true} disablePadding>{projects}</List>
    </div>);
}

export default ProjectsView;
