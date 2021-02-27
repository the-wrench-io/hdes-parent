import React from 'react';

import LibraryAddIcon from '@material-ui/icons/LibraryAdd';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CallMergeIcon from '@material-ui/icons/CallMerge';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import ViewQuiltIcon from '@material-ui/icons/ViewQuilt';
import CachedIcon from '@material-ui/icons/Cached';

import { Resources, Backend } from './core/Resources';
import { Tabs, TabPanel } from './core/Tabs';
import { AssetsView, AssetsTreeView } from './core/Assets';
import { ProjectsView } from './core/Projects';
import Shell from './core/Shell';


const projectsId = 'static/projects';
const assetsId = 'static/assets';


function App() {
  const { actions, session, service } = React.useContext(Resources.Context);
  
  const setWorkspace = (head: Backend.Head) => {
    const projectsTab = session.findTab(projectsId);
    if(projectsTab !== undefined) {
      actions.handleTabClose(session.tabs[projectsTab]);
    }
    actions.handleWorkspace(head)
    actions.handleLink(assetsId);
  }
  
  const isWorkspace = session.workspace ? true : false;
  const listProjects = () => actions.handleTabAdd({id: projectsId, label: 'Projects' });

  const links = [
    { id: assetsId, label: 'View Assets', icon: <LibraryBooksIcon />, onClick: () => (<AssetsTreeView />), enabled: isWorkspace },
    { id: 'add-asset', label: 'Add Asset', icon: <LibraryAddIcon />, onClick: () => console.log("add resource"), enabled: isWorkspace },
    { id: 'branchs', label: 'Set Branch', icon: <AccountTreeIcon />, onClick: () => console.log("set branch"), enabled: isWorkspace },
    { id: 'merge', label: 'Merge To Main', icon: <CallMergeIcon />, onClick: () => console.log("Merge"), enabled: isWorkspace },
    
    { id: projectsId, label: 'Projects', icon: <ViewQuiltIcon />, onClick: listProjects },
    { id: 'reload',     label: 'Reload', icon: <CachedIcon />, onClick: () => console.log("Merge") },
  ];
  
  React.useEffect(() => {
    if(session.data.projects.length > 0 && !session.workspace) {
      const head = session.data.projects[0].heads['main'];
      actions.handleWorkspace(head);
      
      service.snapshots.query({head}).onSuccess(snapshot => {
        const asset = Object.values(snapshot.blobs)[0];
        actions.handleTabAdd({id: asset.id, label: asset.name});  
      });
      
    }
  }, [session, actions, service])
  
  return (
    <Shell tabs={<Tabs />} links={links}
      search={{ onChange: actions.handleSearch }}
      badges={[  ]}>
      
      <TabPanel plugins={[
        { id: projectsId, view: <ProjectsView setWorkspace={setWorkspace} /> },
      ]}>
        <AssetsView />
      </TabPanel>
      
    </Shell>);
}

export default App;
