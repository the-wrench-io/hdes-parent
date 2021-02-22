import React from 'react';

import LibraryAddIcon from '@material-ui/icons/LibraryAdd';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CallMergeIcon from '@material-ui/icons/CallMerge';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import ViewQuiltIcon from '@material-ui/icons/ViewQuilt';
import CachedIcon from '@material-ui/icons/Cached';

import { Resources } from './core/Resources';
import { Tabs, TabPanelRenderer } from './core/Tabs';
import { AssetsView } from './core/Assets';
import { ProjectsView } from './core/Projects';
import Shell from './core/Shell';



function App() {
  const { actions } = React.useContext(Resources.Context);
  

  const listProjects = () => actions.handleTabAdd({id: 'static/projects', label: 'Projects' });
  const views = [
    { id: 'assets', label: 'View Assets', icon: <LibraryBooksIcon />, onClick: () => console.log("add resource") },
    { id: 'add-asset', label: 'Add Asset', icon: <LibraryAddIcon />, onClick: () => console.log("add resource") },
    { id: 'branchs', label: 'Set Branch', icon: <AccountTreeIcon />, onClick: () => console.log("set branch") },
    { id: 'merge', label: 'Merge To Main', icon: <CallMergeIcon />, onClick: () => console.log("Merge") },
    { id: 'static/projects', label: 'View Projects', icon: <ViewQuiltIcon />, onClick: () => listProjects() },
    { id: 'reload', label: 'Reload', icon: <CachedIcon />, onClick: () => console.log("Merge") },
  ];

  const tabPanel = <TabPanelRenderer plugins={[
    { id: 'static/projects', view: <ProjectsView /> },
    { view: <AssetsView />}
  ]}/>
  
  
  return (<React.Fragment>
    <Shell 
      tabs={{items: <Tabs />, panel: tabPanel}}
      views={views}
      search={{ onChange: actions.handleSearch }}
      badges={[  ]}>
    </Shell>
  </React.Fragment>);
}

export default App;
