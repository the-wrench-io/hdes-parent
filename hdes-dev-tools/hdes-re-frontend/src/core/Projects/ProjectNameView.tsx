import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';
import FolderIcon from '@material-ui/icons/Folder';
import Tooltip from '@material-ui/core/Tooltip';
import Link from '@material-ui/core/Link';
import Collapse from '@material-ui/core/Collapse';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import ListSubheader from '@material-ui/core/ListSubheader';
import Typography from '@material-ui/core/Typography';
import ListItemIcon from '@material-ui/core/ListItemIcon';

import { Resources, Backend } from '../Resources';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {

    },
    list: {
      paddingLeft: 0,
      marginLeft: 0
    },
    small: {
      width: theme.spacing(4),
      height: theme.spacing(4),
    },
  }),
);

interface ProjectNameViewProps {
};


const ProjectNameView: React.FC<ProjectNameViewProps> = (props) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const workspace = session.workspace;
  
  if(!workspace) {
    return null;
  }
  
  //
  
  return (
    <div className={classes.root}>
    <List dense disablePadding className={classes.list}>
      <ListItem button className={classes.list}>
        <ListItemIcon>
          <Avatar className={classes.small}><FolderIcon/></Avatar>
        </ListItemIcon>
        <ListItemText primary={<span>{workspace.snapshot.project.name} / {workspace.snapshot.head.name}</span>}/>
      </ListItem>
    </List>
      
    </div>
  );
}

export default ProjectNameView;
