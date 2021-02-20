import React from 'react';

import { makeStyles } from '@material-ui/core/styles';

import LibraryAddIcon from '@material-ui/icons/LibraryAdd';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CallMergeIcon from '@material-ui/icons/CallMerge';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import ViewQuiltIcon from '@material-ui/icons/ViewQuilt';
import CachedIcon from '@material-ui/icons/Cached';

import { Resources, Backend, Session } from './core/Resources';
import { Tabs, TabPanelRenderer } from './core/Tabs';
import { AssetsView } from './core/Assets';
import { ProjectsView } from './core/Projects';
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

function App() {
  const classes = useStyles();

  const { session, setSession } = React.useContext(Resources.Context);
  const handleSearch = (keyword: string) => setSession((session) => session.setSearch(keyword))
  const handleAddTab = (newItem: Session.Tab<any>) => setSession((session) => session.addTab(newItem));
  
  const listProjects = () => handleAddTab({id: 'static/projects', label: 'Projects' });
  
  const views = [
    { id: 'assets', label: 'View Assets', icon: <LibraryBooksIcon />, onClick: () => console.log("add resource") },
    { id: 'add-asset', label: 'Add Asset', icon: <LibraryAddIcon />, onClick: () => console.log("add resource") },
    { id: 'branchs', label: 'Set Branch', icon: <AccountTreeIcon />, onClick: () => console.log("set branch") },
    { id: 'merge', label: 'Merge To Main', icon: <CallMergeIcon />, onClick: () => console.log("Merge") },
    { id: 'projects', label: 'View Projects', icon: <ViewQuiltIcon />, onClick: () => listProjects() },
    { id: 'reload', label: 'Reload', icon: <CachedIcon />, onClick: () => console.log("Merge") },
  ];
  
  const view = (<div>sdfjksdfkhsdkfhsdhfksdhfksdhkfjh</div>)
  
  
  const tabPanel = <TabPanelRenderer plugins={[
    { id: 'static/projects', view: <ProjectsView /> },
    { view: <AssetsView />}
  ]}/>
  
  return (<React.Fragment>
    <Shell 
      tabs={{items: <Tabs />, panel: tabPanel}}
      views={views}
      search={{ onChange: handleSearch }}
      badges={[  ]}>
    </Shell>
  </React.Fragment>);
}

export default App;
