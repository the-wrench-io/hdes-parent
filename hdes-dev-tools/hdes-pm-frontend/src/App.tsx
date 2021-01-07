import React from 'react';

import clsx from 'clsx';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';

import LibraryAddOutlinedIcon from '@material-ui/icons/LibraryAddOutlined';
import PersonAddOutlinedIcon from '@material-ui/icons/PersonAddOutlined';

import AppsOutlinedIcon from '@material-ui/icons/AppsOutlined';
import GroupAddOutlinedIcon from '@material-ui/icons/GroupAddOutlined';
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import PersonOutlineOutlinedIcon from '@material-ui/icons/PersonOutlineOutlined';

import { Backend } from './core/Resources';

import Shell from './core/Shell';
import { AddUser, ConfigureUser, UsersView } from './core/Users';
import { AddProject, ProjectsView } from './core/Projects';
import { AddGroup, GroupsView } from './core/Groups';


const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    display: 'flex',
    overflow: 'auto',
    flexDirection: 'column',
  },
  fixedHeight: {
    height: 340,
  },
}));

interface SessionTab {
  label: string;
  unique: boolean;
  panel: React.ReactNode;  
}

interface SessionHistory {
  previous?: SessionHistory;
  open: number;
}

interface Session {  
  tabs: SessionTab[];
  history: SessionHistory;
}

const findTab = (session: Session, newItem: SessionTab): number | undefined => {
  
  if(!newItem.unique) {
    return undefined;  
  }
  
  let index = 0; 
  for(let tab of session.tabs) {
    if(tab.label === newItem.label) {
      return index;
    }
    index++
  }
  
  return undefined;
}

function App() {
  const classes = useStyles();
  const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);
  
  const startSession: Session = { tabs: [], history: { open: 0 } };
  const [session, setSession] = React.useState(startSession);

  const changeTab = (index: number) => {
    const history: SessionHistory = { previous: session.history, open: index };
    setSession({tabs: session.tabs, history: history});
  };
  
  const addSessionItem = (newItem: SessionTab, session: Session) => {
    const alreadyOpen = findTab(session, newItem);
    if(alreadyOpen !== undefined) {
     return changeTab(alreadyOpen);
    }
    const next = session.tabs.length;
    const history: SessionHistory = { previous: session.history, open: next };
    setSession({tabs: session.tabs.concat(newItem), history: history});
  };
  
  const confNewUser = (session: Session, activeStep: number, user: Backend.UserBuilder) => {
    addSessionItem({unique: false, label: 'creating new user', panel: <ConfigureUser user={user} activeStep={activeStep} /> }, session);
  };
  
  const operations = [
    { label: 'Add User', icon: <PersonAddOutlinedIcon />, 
      dialog: (open: boolean, handleClose: () => void) => <AddUser open={open} 
        handleClose={handleClose} 
        handleConf={(activeStep, user) => confNewUser(session, activeStep, user)} />},
      
    { label: 'Add Project', icon: <LibraryAddOutlinedIcon />,
      dialog: (open: boolean, handleClose: () => void) => <AddProject open={open} handleClose={handleClose} />},
      
    { label: 'Add Group', icon: <GroupAddOutlinedIcon />,
      dialog: (open: boolean, handleClose: () => void) => <AddGroup open={open} handleClose={handleClose} />}];

  const onUserEdit = (user: Backend.UserBuilder) => confNewUser(session, 0, user);
  
  const listDashboard = () => addSessionItem({unique: true, label: 'Dashboard', panel: <React.Fragment>{projects}{users}</React.Fragment>}, session);
  const listGroups = () => addSessionItem({unique: true, label: 'Groups', panel: <GroupsView />}, session);
  const listProjects = () => addSessionItem({unique: true, label: 'Projects', panel: <ProjectsView />}, session);
  const listUsers = () => addSessionItem({unique: true, label: 'User', panel: <UsersView onEdit={onUserEdit}/>}, session);

  const projects = (<Grid key="1" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                <ProjectsView top={4} seeMore={listProjects}/>
              </Paper>
            </Grid>)

  const users = (<Grid key="2" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                <UsersView top={4} seeMore={listUsers} onEdit={onUserEdit}/>
              </Paper>
            </Grid>)
  

  const views = [
    { label: 'Dashboard', icon: <AppsOutlinedIcon />, onClick: listDashboard},
    { label: 'List Groups', icon: <GroupOutlinedIcon />, onClick: listGroups},
    { label: 'List Users', icon: <PersonOutlineOutlinedIcon />, onClick: listUsers},
    { label: 'List Projects', icon: <LibraryBooksOutlinedIcon />, onClick: listProjects}
  ]
  
  return (<Shell init={0} operations={operations} views={views}
      tabs={{entries: session.tabs, open: session.history.open, handleOpen: changeTab }}
    />);
}

export default App;
