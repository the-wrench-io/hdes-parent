import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

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
import { Title } from '.././Views';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%'
    },
    primary: {
      paddingLeft: theme.spacing(4),
    },
    secondary: {
      paddingLeft: theme.spacing(6),
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

  return (<div className={classes.root}>
    <List className={classes.root} component="nav" aria-labelledby="nested-list-subheader">
      <Title>Project {props.project.name}</Title>
      <Divider />
      
      <ListItem button onClick={() => setOpenGroups(!openGroups)}>
        <ListItemText primary={`Groups that have access: (${props.project.groups.length})`} />
        {openGroups ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      
      <Collapse in={openGroups} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.project.groups
            .map(id => groups.filter(p => p.group.id === id)[0])
            .map(p => (<React.Fragment>              
                <ListItem key={p.group.id} button className={classes.primary}>
                  <ListItemIcon><GroupOutlinedIcon /></ListItemIcon>
                  <ListItemText primary={p.group.name} />
                </ListItem>
                { Object.values(p.users).map((u, key) => (
                  <ListItem key={`${key}-${u.id}`} button className={classes.secondary}>
                    <ListItemIcon><PersonOutlinedIcon /></ListItemIcon>
                    <ListItemText secondary={u.name} />
                  </ListItem>
                )) }
              </React.Fragment>))
          }
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenUsers(!openUsers)}>
        <ListItemText primary={`Users that have access: (${props.project.users.length})`} />
        {openUsers ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openUsers} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.project.users
            .map(id => users.filter(p => p.user.id === id)[0].user)
            .map(p => <ListItem key={p.id} button className={classes.primary}><ListItemIcon><PersonOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureProjectSummary;