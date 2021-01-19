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

import { Resources, Backend, Session } from './core/Resources';
import { AddUser, ConfigureUserInTab, UsersView } from './core/Users';
import { AddProject, ConfigureProjectInTab, ProjectsView } from './core/Projects';
import { AddGroup, ConfigureGroupInTab, GroupsView } from './core/Groups';
import { ResourceSaved } from './core/Views';
import { SearchView } from './core/Search';

import Shell from './core/Shell';


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

const makeDialogs = () => {
  return {
    user:     { id: 'add-user', label: 'Add User', icon: <PersonAddOutlinedIcon />},
    project:  { id: 'add-project', label: 'Add Project', icon: <LibraryAddOutlinedIcon /> },
    group:    { id: 'add-group', label: 'Add Group', icon: <GroupAddOutlinedIcon />}
  }
}


function App() {
  const { service, session, setSession } = React.useContext(Resources.Context);
  const [ resourceSaved, setResourceSaved ] = React.useState<undefined | Backend.AnyResource>();

  const classes = useStyles();
  const dialogs = makeDialogs();
  const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);  

  const handleSearchFor = (keyword: string) => {
    if(keyword.length > 0) {
      listSearch();
      setSession((session) => session.withSearch(keyword))
    }
  };
  const handleDialogOpen = (id: string) => setSession((session) => {
    const index = session.findTab(id);
    return index ? session.withTab(index) : session.withDialog(id);
  });
  
  const onConfirm = (
    tabId: string, resource: Backend.AnyResource) => {
    setResourceSaved(resource);
    setSession((session) => session.deleteTab(tabId));
  };
  
  
  const handleDialogClose = () => setSession((session) => session.withDialog());
  const changeTab = (index: number) => setSession((session) => session.withTab(index));
  const addTab = (newItem: Session.Tab) => setSession((session) => session.withTab(newItem));
  const setTabData = (id: string, updateCommand: (oldData: any) => any) => setSession((session) => session.withTabData(id, updateCommand))

  const confProjectInTab = (project: Backend.ProjectBuilder, activeStep?: number) => addTab(ConfigureProjectInTab(setTabData, onConfirm, dialogs.project.id, project, activeStep));
  const confUserInTab = (user: Backend.UserBuilder, activeStep?: number) => addTab(ConfigureUserInTab(setTabData, onConfirm, dialogs.user.id, user, activeStep));
  const confGroupInTab = (group: Backend.GroupBuilder, activeStep?: number) => addTab(ConfigureGroupInTab(setTabData, onConfirm, dialogs.group.id, group, activeStep));
  
  const listDashboard = () => addTab({id: 'dashboard', label: 'Dashboard', panel: () => <React.Fragment>{projects}{users}</React.Fragment>});
  const listGroups    = () => addTab({id: 'groups', label: 'Groups', panel: () => <GroupsView onEdit={confGroupInTab}/>});
  const listProjects  = () => addTab({id: 'projects', label: 'Projects', panel: () => <ProjectsView onEdit={confProjectInTab}/>});
  const listUsers     = () => addTab({id: 'users', label: 'Users', panel: () => <UsersView onEdit={confUserInTab}/>});
  const listSearch    = () => addTab({id: 'search', label: 'Search...', panel: () => <SearchView
    onProject={(r) => confProjectInTab(service.projects.builder(r))}
    onGroup={(r) => confGroupInTab(service.groups.builder(r))}
    onUser={(r) => confUserInTab(service.users.builder(r))} /> });

  const projects = (<Grid key="1" item xs={12} md={8} lg={9}>
      <Paper className={fixedHeightPaper}>
        <ProjectsView top={4} seeMore={listProjects} onEdit={confProjectInTab}/>
      </Paper>
    </Grid>);

  const users = (<Grid key="2" item xs={12} md={8} lg={9}>
      <Paper className={fixedHeightPaper}>
        <UsersView top={4} seeMore={listUsers} onEdit={confUserInTab}/>
      </Paper>
    </Grid>);
  
  const views = [
    { label: 'Dashboard', icon: <AppsOutlinedIcon />, onClick: listDashboard},
    { label: 'List Groups', icon: <GroupOutlinedIcon />, onClick: listGroups},
    { label: 'List Users', icon: <PersonOutlineOutlinedIcon />, onClick: listUsers},
    { label: 'List Projects', icon: <LibraryBooksOutlinedIcon />, onClick: listProjects}
  ];

  return (<React.Fragment>
    <ResourceSaved resource={resourceSaved} onClose={() => setResourceSaved(undefined)}/>
    <AddUser open={session.dialogId === dialogs.user.id} handleClose={handleDialogClose} handleConf={confUserInTab} />
    <AddProject open={session.dialogId === dialogs.project.id} handleClose={handleDialogClose} handleConf={confProjectInTab}/>
    <AddGroup open={session.dialogId === dialogs.group.id} handleClose={handleDialogClose} handleConf={confGroupInTab}/>
  
    <Shell init={0}
      session={session} 
      views={views}
      dialogs={{items: [dialogs.group, dialogs.user, dialogs.project], onClick: handleDialogOpen}}
      tabs={{items: session.tabs, active: session.history.open, onClick: changeTab }} 
      search={{ onChange: handleSearchFor }} />
  </React.Fragment>);
}

export default App;
