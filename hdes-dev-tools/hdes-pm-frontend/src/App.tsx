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
import ThumbUpIcon from '@material-ui/icons/ThumbUp';

import { Resources, Backend, Session, Mapper } from './core/Resources';
import { AddUser, ConfigureUserInTab, UsersView, ApproveView } from './core/Users';
import { AddProject, ConfigureProjectInTab, ProjectsView } from './core/Projects';
import { AddGroup, ConfigureGroupInTab, GroupsView } from './core/Groups';
import { NotificationSaved, NotificationSavedBadge } from './core/Notifications';
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

const useDialogs = () => {
  return {
    user:     { id: 'add-user', label: 'Add User', icon: <PersonAddOutlinedIcon />},
    project:  { id: 'add-project', label: 'Add Project', icon: <LibraryAddOutlinedIcon /> },
    group:    { id: 'add-group', label: 'Add Group', icon: <GroupAddOutlinedIcon />}
  }
}

function App() {
  const classes = useStyles();
  const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);
      
  const dialogs = useDialogs();
  const { session, setSession } = React.useContext(Resources.Context);

  const handleSearchFor = (keyword: string) => {
    if(keyword.length > 0) {
      listSearch();
      setSession((session) => session.setSearch(keyword))
    }
  };
  
  const handleDialogOpen = (id: string) => setSession((action) => {
    const index = session.findTab(id);
    return index ? action.changeTab(index) : action.setDialog(id);
  });
  const handleDialogClose = () => setSession((session) => session.setDialog());
  const changeTab = (index: number) => setSession((session) => session.changeTab(index));
  const addTab = (newItem: Session.Tab) => setSession((session) => session.addTab(newItem));
  const openInTab = (props: {builder: Backend.AnyBuilder, edit?: boolean, activeStep?: number}) => {
    const tab: Session.Tab = new Mapper.Builder<Session.Tab>(props.builder)
      .project(project => ConfigureProjectInTab(dialogs.project.id, project, props.edit, props.activeStep))
      .group(group => ConfigureGroupInTab(dialogs.group.id, group, props.edit, props.activeStep))
      .user(user => ConfigureUserInTab(dialogs.user.id, user, props.edit, props.activeStep))
      .map();
    addTab(tab);
  }
  
  const listDashboard = () => addTab({id: 'dashboard', label: 'Dashboard', panel: <React.Fragment>{projects}{users}</React.Fragment>});
  const listGroups    = () => addTab({id: 'groups', label: 'Groups', panel: <GroupsView onSelect={openInTab}/>});
  const listProjects  = () => addTab({id: 'projects', label: 'Projects', panel: <ProjectsView onSelect={openInTab}/>});
  const listUsers     = () => addTab({id: 'users', label: 'Users', panel: <UsersView onSelect={openInTab}/>});
  const listSearch    = () => addTab({id: 'search', label: 'Search...', panel: <SearchView onSelect={openInTab} />});
  const listApprovals = () => addTab({id: 'approvals', label: 'Approvals', panel: <ApproveView onSelect={openInTab} />});

  const projects = (<Grid key="1" item xs={12} md={8} lg={9}>
      <Paper className={fixedHeightPaper}>
        <ProjectsView top={4} seeMore={listProjects} onSelect={openInTab}/>
      </Paper>
    </Grid>);

  const users = (<Grid key="2" item xs={12} md={8} lg={9}>
      <Paper className={fixedHeightPaper}>
        <UsersView top={4} seeMore={listUsers} onSelect={openInTab}/>
      </Paper>
    </Grid>);
  
  return (<React.Fragment>
    <NotificationSaved />
    
    <AddUser open={session.dialogId === dialogs.user.id} handleClose={handleDialogClose} handleConf={openInTab} />
    <AddProject open={session.dialogId === dialogs.project.id} handleClose={handleDialogClose} handleConf={openInTab}/>
    <AddGroup open={session.dialogId === dialogs.group.id} handleClose={handleDialogClose} handleConf={openInTab}/>
  
    <Shell init={0} 
      views={[
        { label: 'Dashboard', icon: <AppsOutlinedIcon />, onClick: listDashboard},
        { label: 'List Approvals', icon: <ThumbUpIcon />, onClick: listApprovals},
        { label: 'List Groups', icon: <GroupOutlinedIcon />, onClick: listGroups},
        { label: 'List Users', icon: <PersonOutlineOutlinedIcon />, onClick: listUsers},
        { label: 'List Projects', icon: <LibraryBooksOutlinedIcon />, onClick: listProjects} ]}
      dialogs={{items: [dialogs.group, dialogs.user, dialogs.project], onClick: handleDialogOpen}} 
      search={{ onChange: handleSearchFor }}
      tabs={{items: session.tabs, active: session.history.open, onClick: changeTab}}
      badges={[ NotificationSavedBadge ]}>
      
    </Shell>
  </React.Fragment>);
}

export default App;
