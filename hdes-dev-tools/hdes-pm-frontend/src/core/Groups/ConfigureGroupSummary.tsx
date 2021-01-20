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
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';

import { Title } from '.././Views';
import { Backend, Resources } from '.././Resources';


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
  group: Backend.GroupBuilder;
  users?: Backend.UserResource[];
  projects?: Backend.ProjectResource[];
};

const ConfigureProjectSummary: React.FC<ConfigureProjectSummaryProps> = (props) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  
  const [openUsers, setOpenUsers] = React.useState(true);
  const [openProjects, setOpenProjects] = React.useState(true);
  const [users, setUsers] = React.useState<Backend.UserResource[] | undefined>(props.users);
  const [projects, setProjects] = React.useState<Backend.ProjectResource[] | undefined>(props.projects);

  React.useEffect(() => {
    if(!users || !projects) {
      service.users.query().onSuccess(setUsers)
      service.projects.query().onSuccess(setProjects)
    }
  }, [service, service.users, service.groups, projects, users])


  if(!users || !projects) {
    return <div>Loading...</div>;
  }

  return (<div className={classes.root}>
    <List className={classes.root} component="nav" aria-labelledby="nested-list-subheader">
      
      <Title>Group {props.group.name}</Title>
      <Divider />
      
      <ListItem button onClick={() => setOpenProjects(!openProjects)}>
        <ListItemText primary={`Projects to what there is access: (${props.group.projects.length})`} />
        {openProjects ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openProjects} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.group.projects
            .map(id => projects.filter(p => p.project.id === id)[0].project)
            .map(p => <ListItem key={p.id} button className={classes.primary}><ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenUsers(!openUsers)}>
        <ListItemText primary={`Users that are part of the group: (${props.group.users.length})`} />
        {openUsers ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openUsers} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {props.group.users
            .map(id => users.filter(p => p.user.id === id)[0].user)
            .map(p => <ListItem key={p.id} button className={classes.primary}><ListItemIcon><PersonOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureProjectSummary;