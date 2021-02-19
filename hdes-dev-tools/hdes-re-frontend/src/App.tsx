import React from 'react';

import { makeStyles } from '@material-ui/core/styles';

import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';

import { Resources, Backend, Session } from './core/Resources';

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


  const views = [{ id: 'add-resource', label: 'Add Resource', icon: <AddCircleOutlineIcon />, 
    onClick: () => console.log("add") 
  }];
  
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
