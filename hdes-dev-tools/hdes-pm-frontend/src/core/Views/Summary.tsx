import React from 'react';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Avatar from '@material-ui/core/Avatar';

import DateFormat from './DateFormat';
import { ResourceMapper } from './ResourceMapper';
import { Backend } from '.././Resources';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      overflow: 'hidden',
      padding: theme.spacing(0, 1),
    },
    paper: {
      maxWidth: 500,
      margin: `${theme.spacing(1)}px auto`,
      padding: theme.spacing(1),
      
    },
    title: {
      background: theme.palette.primary.main
    },
    users: {
      background: theme.palette.secondary.dark
    },
    groups: {
      background: theme.palette.secondary.dark
    },
    projects: {
      background: theme.palette.secondary.dark
    },
    progress: {
      display: 'flex',
      '& > * + *': {
        marginLeft: theme.spacing(2),
      },
    },
  }),
);


type View = {
  name: string;
  created: Date;
  letter: string;
  users?: string;
  groups?: string; 
  projects?: string;
}

interface SummaryProps {
  resource: Backend.ProjectResource | Backend.UserResource | Backend.GroupResource;
};

const Summary: React.FC<SummaryProps> = ({resource}) => {
  const classes = useStyles();
  const view = new ResourceMapper<View>(resource)
    .project(resource => ({
      name: resource.project.name,
      created: resource.project.created,
      letter: "P",
      groups: Object.values(resource.groups).map(p => p.name).join(", "),
      users: Object.values(resource.users).map(p => p.name).join(", ")
    }))
    .group(resource => ({
      name: resource.group.name,
      created: resource.group.created,
      letter: "G",    
      projects: Object.values(resource.projects).map(p => p.name).join(", "),
      users: Object.values(resource.users).map(p => p.name).join(", ")
    }))
    .user(resource => ({
      name: resource.user.name,
      created: resource.user.created,
      letter: "U",
      groups: Object.values(resource.groups).map(p => p.name).join(", "),
      projects: Object.values(resource.projects).map(p => p.name).join(", ")
    })).map();
    
  return (<div className={classes.root}>
    <Paper className={classes.paper}>
      <Grid container wrap="nowrap" spacing={2}>
        <Grid item>
          <Avatar className={classes.title}>{view.letter}</Avatar>
        </Grid>
        <Grid item xs>
          <Typography component="h2" variant="h6" gutterBottom>
            {view.name} / <DateFormat>{view.created}</DateFormat>
          </Typography>
        </Grid>
      </Grid>
    </Paper>
    
    {!view.users ? null : (
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item><Avatar className={classes.users}>U</Avatar></Grid>
          <Grid item xs><Typography>{view.users}</Typography></Grid>
        </Grid>
      </Paper>)}

    {!view.groups ? null : (
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item><Avatar className={classes.groups}>G</Avatar></Grid>
          <Grid item xs><Typography>{view.groups}</Typography></Grid>
        </Grid>
      </Paper>)}

    {!view.projects ? null : (
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item><Avatar className={classes.projects}>P</Avatar></Grid>
          <Grid item xs><Typography>{view.projects}</Typography></Grid>
        </Grid>
      </Paper>)}      
    </div>
  );
}

export default Summary;

