import React from 'react';

import clsx from 'clsx';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';

import Shell from './core/Shell';
import Projects from './core/Projects';
import MenuItems from './core/MenuItems';


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
  
  return (<Shell title="Hdes Project Manager" 
    menu={[
      <MenuItems.Operations />, 
      <MenuItems.Views />]}
    content={[projects, users]}
    />);
}

export default App;
