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
  const { actions, session } = React.useContext(Resources.Context);
  
  const isWorkspace = session.workspace ? true : false;
  
  const listProjects = () => actions.handleTabAdd({id: 'static/projects', label: 'Projects' });
  const links = [
    { id: 'assets', label: 'View Assets', icon: <LibraryBooksIcon />, onClick: () => console.log("add resource"), enabled: isWorkspace },
    { id: 'add-asset', label: 'Add Asset', icon: <LibraryAddIcon />, onClick: () => console.log("add resource"), enabled: isWorkspace },
    { id: 'branchs', label: 'Set Branch', icon: <AccountTreeIcon />, onClick: () => console.log("set branch"), enabled: isWorkspace },
    { id: 'merge', label: 'Merge To Main', icon: <CallMergeIcon />, onClick: () => console.log("Merge"), enabled: isWorkspace },
    
    { id: 'static/projects', label: 'Projects', icon: <ViewQuiltIcon />, onClick: listProjects },
    { id: 'reload',     label: 'Reload', icon: <CachedIcon />, onClick: () => console.log("Merge") },
  ];

  const tabPanel = (<TabPanelRenderer plugins={[
    { id: 'static/projects', view: <ProjectsView /> },
    { view: <AssetsView />}
  ]}/>);
  
  
  return (<React.Fragment>
    <Shell 
      tabs={{items: <Tabs />, panel: tabPanel}}
      links={links}
      search={{ onChange: actions.handleSearch }}
      badges={[  ]}>
    </Shell>
  </React.Fragment>);
}

export default App;
