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
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';

import { Title } from '.././Views';
import { Backend, Resources } from '.././Resources';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
    },
    primary: {
      paddingLeft: theme.spacing(4),
    },
    secondary: {
      paddingLeft: theme.spacing(6),
    },
  }),
);


interface ConfigureUserSummaryProps {
  user: Backend.UserBuilder;
  projects?: readonly Backend.ProjectResource[];
  groups?: readonly Backend.GroupResource[];
};

const ConfigureUserSummary: React.FC<ConfigureUserSummaryProps> = (props) => {
  const classes = useStyles();

  const { session } = React.useContext(Resources.Context);
  const { groups, projects } = session.data;

  const [openProject, setOpenProjects] = React.useState(true);
  const [openGroups, setOpenGroups] = React.useState(true);


  return (<div className={classes.root}>
    <List className={classes.root} component="nav" aria-labelledby="nested-list-subheader">
      <Title>User {props.user.name} / {props.user.status}</Title>
      <Divider />
      
      <ListItem button onClick={() => setOpenGroups(!openGroups)}>
        <ListItemText primary={`Groups that user belongs to: (${groups.length})`} />
        {openGroups ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openGroups} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          { groups.map((p, index) => (
            <React.Fragment key={index}>
              <ListItem key={p.group.id} button className={classes.primary}>
                <ListItemIcon><GroupOutlinedIcon /></ListItemIcon>
                <ListItemText primary={p.group.name} />
              </ListItem>
              { Object.values(p.projects).map((u, key) => (
                <ListItem key={`${key}-${u.id}`} button className={classes.secondary}>
                  <ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon>
                  <ListItemText secondary={u.name} />
                </ListItem>
              )) }
            </React.Fragment>
          ))}
        </List>
      </Collapse>

      <ListItem button onClick={() => setOpenProjects(!openProject)}>
        <ListItemText primary={`Projects that user has access to: (${projects.length})`} />
        {openProject ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={openProject} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          { projects.map(p => p.project).map(p => (
              <ListItem key={p.id} button className={classes.primary}>
                <ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon>
                <ListItemText primary={p.name} />
              </ListItem>)
            )
          }
        </List>
      </Collapse>
      
    </List>
  </div>);
}

export default ConfigureUserSummary;