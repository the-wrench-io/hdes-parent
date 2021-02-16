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
  users?: readonly Backend.UserResource[];
  projects?: readonly Backend.ProjectResource[];
};

const ConfigureProjectSummary: React.FC<ConfigureProjectSummaryProps> = (props) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const { users, projects } = session.data;
  
  const [openUsers, setOpenUsers] = React.useState(true);
  const [openProjects, setOpenProjects] = React.useState(true);
  
  return (<div className={classes.root}>
    <List className={classes.root} component="nav" aria-labelledby="nested-list-subheader">
      
      <Title>{props.group.name}</Title>
      
      <ListItem button>
        <ListItemText primary={`Group Matcher: (${props.group.matcher})`} />
      </ListItem>

      <ListItem button>
        <ListItemText primary={`Group Type: (${props.group.type})`} />
      </ListItem>
      
      <Divider />
      
      { props.group.type === 'ADMIN' ? 
        (<ListItem button>
          <ListItemText primary={`Group will have access to all of the PROJECTS`} />
        </ListItem>) 
        : 
        (<><ListItem button onClick={() => setOpenProjects(!openProjects)}>
          <ListItemText primary={`Projects to what there is access: (${props.group.projects.length})`} />
          {openProjects ? <ExpandLess /> : <ExpandMore />}
        </ListItem>
        <Collapse in={openProjects} timeout="auto" unmountOnExit>
          <List component="div" disablePadding>
            {props.group.projects
              .map(id => projects.filter(p => p.project.id === id)[0].project)
              .map(p => <ListItem key={p.id} button className={classes.primary}><ListItemIcon><LibraryBooksOutlinedIcon /></ListItemIcon><ListItemText primary={p.name} /></ListItem>)}
          </List>
        </Collapse></>)
      }

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