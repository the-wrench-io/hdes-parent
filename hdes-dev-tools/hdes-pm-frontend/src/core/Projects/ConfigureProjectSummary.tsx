import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import ListSubheader from '@material-ui/core/ListSubheader';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import Divider from '@material-ui/core/Divider';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import PersonOutlinedIcon from '@material-ui/icons/PersonOutlined';
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';


import { Backend } from '.././Resources';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      backgroundColor: theme.palette.background.paper,
    },
    nested: {
      paddingLeft: theme.spacing(4),
    },
  }),
);


interface ConfigureProjectSummaryProps {
  project: Backend.ProjectBuilder;
  users: Backend.UserResource[];
  groups: Backend.GroupResource[];
};

const ConfigureProjectSummary: React.FC<ConfigureProjectSummaryProps> = (props) => {
  const classes = useStyles();
  
  const groupUsers: React.ReactNode[] = [];
  let groupUsersTotal = 0;
  for(const group of props.groups) {
    const children: Backend.Project[] = Object.values(group.users);
    groupUsersTotal += children.length;
    children.forEach((p, key) => groupUsers.push(<ListItem key={`${key}-${group.group.id}`} button className={classes.nested}>
        <ListItemIcon><GroupOutlinedIcon /></ListItemIcon>
        <ListItemText primary={`${p.name} - inherited: ${group.group.name}`} />
      </ListItem>));
  }
     
  const [openUsers, setOpenUsers] = React.useState(true);
  const [openGroups, setOpenGroups] = React.useState(true);

  return (<div className={classes.root}>
    <List className={classes.root} component="nav" 
      aria-labelledby="nested-list-subheader"
      subheader={<ListSubheader component="div" id="nested-list-subheader">User summary</ListSubheader>}>
      <ListItem>
        <ListItemIcon><PersonOutlinedIcon /></ListItemIcon>
        <ListItemText primary={props.project.name} />
      </ListItem>
      <Divider />
      
      <ListItem button onClick={() => setOpenGroups(!openGroups)}>
        <ListItemText primary={`Groups to join: (${props.groups.length})`} />
        {openGroups ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openGroups} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.groups.map(p => <ListItem key={p.group.id} button className={classes.nested}><ListItemIcon><GroupOutlinedIcon /></ListItemIcon><ListItemText primary={p.group.name} /></ListItem>)}
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenUsers(!openUsers)}>
        <ListItemText primary={`Users to join: (${props.users.length + groupUsersTotal})`} />
        {openUsers ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openUsers} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.users.map(u => u.user).map(p => <ListItem key={p.id} button className={classes.nested}><ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
          {groupUsers}
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureProjectSummary;