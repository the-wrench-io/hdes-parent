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
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';


import { Resources, Backend } from '.././Resources';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%'
    },
    nested: {
      paddingLeft: theme.spacing(4),
    },
  }),
);


interface ConfigureProjectSummaryProps {
  project: Backend.ProjectBuilder;
  users?: Backend.UserResource[];
  groups?: Backend.GroupResource[];
};

const ConfigureProjectSummary: React.FC<ConfigureProjectSummaryProps> = (props) => {
  const classes = useStyles();
  
  const { service } = React.useContext(Resources.Context);
  const [openUsers, setOpenUsers] = React.useState(true);
  const [openGroups, setOpenGroups] = React.useState(true);
  const [users, setUsers] = React.useState<Backend.UserResource[] | undefined>(props.users);
  const [groups, setGroups] = React.useState<Backend.GroupResource[] | undefined>(props.groups);

  React.useEffect(() => {
    if(!users || !groups) {
      service.users.query().onSuccess(setUsers)
      service.groups.query().onSuccess(setGroups)
    }
  }, [service, service.users, service.groups, groups, users])

  if(!users || !groups) {
    return <div>Loading...</div>;
  }
  
  const groupUsers: React.ReactNode[] = [];
  let groupUsersTotal = 0;
  for(const id of props.project.groups) {
    const group = groups.filter(p => p.group.id === id)[0];
    const children: Backend.Project[] = Object.values(group.users);
    groupUsersTotal += children.length;
    children.forEach((p, key) => groupUsers.push(<ListItem key={`${key}-${group.group.id}`} button className={classes.nested}>
        <ListItemIcon><PersonOutlinedIcon /></ListItemIcon>
        <ListItemText primary={`${p.name} - from group access: ${group.group.name}`} />
      </ListItem>));
  }

  return (<div className={classes.root}>
    <List className={classes.root} component="nav" 
      aria-labelledby="nested-list-subheader"
      subheader={<ListSubheader component="div" id="nested-list-subheader">{`Project '${props.project.name}' summary`}</ListSubheader>}>

      <Divider />
      
      <ListItem button onClick={() => setOpenGroups(!openGroups)}>
        <ListItemText primary={`Groups that have access: (${props.project.groups.length})`} />
        {openGroups ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openGroups} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.project.groups
            .map(id => groups.filter(p => p.group.id === id)[0].group)
            .map(p => <ListItem key={p.id} button className={classes.nested}><ListItemIcon><GroupOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenUsers(!openUsers)}>
        <ListItemText primary={`Users that have access: (${props.project.users.length + groupUsersTotal})`} />
        {openUsers ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openUsers} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.project.users
            .map(id => users.filter(p => p.user.id === id)[0].user)
            .map(p => <ListItem key={p.id} button className={classes.nested}><ListItemIcon><PersonOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
          {groupUsers}
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureProjectSummary;