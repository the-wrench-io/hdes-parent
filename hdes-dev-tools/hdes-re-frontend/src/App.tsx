import React from 'react';

import { makeStyles } from '@material-ui/core/styles';

import LibraryAddIcon from '@material-ui/icons/LibraryAdd';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CallMergeIcon from '@material-ui/icons/CallMerge';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import { Resources, Backend, Session } from './core/Resources';
import ViewQuiltIcon from '@material-ui/icons/ViewQuilt';

import { Tabs, TabPanel } from './core/Tabs';
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


  const views = [
    { id: 'resources', label: 'View Resources', icon: <LibraryBooksIcon />, onClick: () => console.log("add resource") },
    { id: 'add-resource', label: 'Add Resource', icon: <LibraryAddIcon />, onClick: () => console.log("add resource") },
    { id: 'branchs', label: 'Set Branch', icon: <AccountTreeIcon />, onClick: () => console.log("set branch") },
    { id: 'merge', label: 'Merge To Main', icon: <CallMergeIcon />, onClick: () => console.log("Merge") },
    { id: 'projects', label: 'View Projects', icon: <ViewQuiltIcon />, onClick: () => console.log("add resource") },
  ];
  
  const view = (<div>sdfjksdfkhsdkfhsdhfksdhfksdhkfjh</div>)
  
  return (<React.Fragment>
    <Shell 
      tabs={{items: <Tabs />, panel: <></>}}
      views={views}
      search={{ onChange: handleSearch }}
      badges={[  ]}>
    </Shell>
  </React.Fragment>);
}

export default App;
