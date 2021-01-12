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


interface ConfigureUserSummaryProps {
  user: Backend.UserBuilder;
  projects: Backend.ProjectResource[];
  groups: Backend.GroupResource[];
};

const ConfigureUserSummary: React.FC<ConfigureUserSummaryProps> = (props) => {
  const classes = useStyles();
  
  const groups = props.groups.filter(r => props.user.groups.includes(r.group.id));
  const groupProjects: React.ReactNode[] = [];
  let groupProjectTotal = 0;
  for(const group of groups) {
    const children: Backend.Project[] = Object.values(group.projects);
    groupProjectTotal += children.length;
    children.forEach((p, key) => groupProjects.push(<ListItem key={`${key}-${group.group.id}`} button className={classes.nested}>
        <ListItemIcon><GroupOutlinedIcon /></ListItemIcon>
        <ListItemText primary={`${p.name} - inherited: ${group.group.name}`} />
      </ListItem>));
  }
    
  const projects = props.projects.filter(r => props.user.projects.includes(r.project.id)).map(r => r.project); 
  const [openProject, setOpenProjects] = React.useState(true);
  const [openGroups, setOpenGroups] = React.useState(true);

  return (<div className={classes.root}>
    <List className={classes.root} component="nav" 
      aria-labelledby="nested-list-subheader"
      subheader={<ListSubheader component="div" id="nested-list-subheader">{`User '${props.user.name} / ${props.user.externalId}' summary`}</ListSubheader>}>

      <Divider />
      
      <ListItem button onClick={() => setOpenGroups(!openGroups)}>
        <ListItemText primary={`Groups to join: (${groups.length})`} />
        {openGroups ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openGroups} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {groups.map(p => <ListItem key={p.group.id} button className={classes.nested}><ListItemIcon><GroupOutlinedIcon /></ListItemIcon><ListItemText primary={p.group.name} /></ListItem>)}
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenProjects(!openProject)}>
        <ListItemText primary={`Projects to join: (${projects.length + groupProjectTotal})`} />
        {openProject ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openProject} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {projects.map(p => <ListItem key={p.id} button className={classes.nested}><ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
          {groupProjects}
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureUserSummary;