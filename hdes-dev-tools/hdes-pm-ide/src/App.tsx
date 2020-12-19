import React from 'react';

import clsx from 'clsx';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';

import InputOutlinedIcon from '@material-ui/icons/InputOutlined';
import LibraryAddOutlinedIcon from '@material-ui/icons/LibraryAddOutlined';
import PersonAddOutlinedIcon from '@material-ui/icons/PersonAddOutlined';

import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';
import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import PersonOutlineOutlinedIcon from '@material-ui/icons/PersonOutlineOutlined';


import Shell from './core/Shell';
import { AddUser } from './core/Users';
import { Projects, AddProject } from './core/Projects';
import { AddGroup } from './core/Groups';

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
  const fixedHeightPaper = clsx(classes.paper, classes.fixedHeight);
  
  const projects = (<Grid key="1" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                <Projects />
              </Paper>
            </Grid>)
  const users = (<Grid key="2" item xs={12} md={8} lg={9}>
              <Paper className={fixedHeightPaper}>
                Users
              </Paper>
            </Grid>)
  
  const operations = [
    { label: 'Add User', icon: <PersonAddOutlinedIcon />, 
      dialog: (open: boolean, handleClose: () => void) => <AddUser open={open} handleClose={handleClose} />},
      
    { label: 'Add Project', icon: <InputOutlinedIcon />,
      dialog: (open: boolean, handleClose: () => void) => <AddProject open={open} handleClose={handleClose} />},
      
    { label: 'Add Group', icon: <LibraryAddOutlinedIcon />,
      dialog: (open: boolean, handleClose: () => void) => <AddGroup open={open} handleClose={handleClose} />}];
  
  const tabs = [
      { label: 'Dashboard', panel: <React.Fragment>{projects}{users}</React.Fragment> }
    ];

  const views = [
    { label: 'List Groups', icon: <GroupOutlinedIcon />},
    { label: 'List Users', icon: <PersonOutlineOutlinedIcon />},
    { label: 'List Projects', icon: <LibraryBooksOutlinedIcon />}
  ]
  
  return (<Shell operations={operations} views={views} tabs={tabs} />);
}

export default App;
