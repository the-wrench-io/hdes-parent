import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import FolderIcon from '@material-ui/icons/Folder';

import ListItemIcon from '@material-ui/core/ListItemIcon';

import { Resources } from '../Resources';


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
