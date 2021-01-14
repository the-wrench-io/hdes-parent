import React from 'react';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Avatar from '@material-ui/core/Avatar';

import { DateFormat } from '.././Views';
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


const isProject = (resource: any) : resource is Backend.ProjectResource => {
  return resource.project !== undefined;
}
const isUser = (resource: any) : resource is Backend.UserResource => {
  return resource.user !== undefined;
}
const isGroup = (resource: any) : resource is Backend.GroupResource => {
  return resource.group !== undefined;
}

interface SummaryProps {
  resource: Backend.ProjectResource | Backend.UserResource | Backend.GroupResource;
};

const Summary: React.FC<SummaryProps> = ({resource}) => {
  const classes = useStyles();

  let name: string;
  let created: Date;
  let letter: string
  
  let users: string | null = null;
  let groups: string | null = null; 
  let projects: string | null = null;
  
  if(isProject(resource)) {
    name = resource.project.name;
    created = resource.project.created;
    letter = "P";
    groups = Object.values(resource.groups).map(p => p.name).join(", ");
    users = Object.values(resource.users).map(p => p.name).join(", ");
  } else if(isGroup(resource)) {
    name = resource.group.name;
    created = resource.group.created;
    letter = "G";    
    projects = Object.values(resource.projects).map(p => p.name).join(", ");
    users = Object.values(resource.users).map(p => p.name).join(", ");
  } else if(isUser(resource)) {
    name = resource.user.name;
    created = resource.user.created;
    letter = "U";
    groups = Object.values(resource.groups).map(p => p.name).join(", ");
    projects = Object.values(resource.projects).map(p => p.name).join(", ");    
  } else {
    throw new Error(`Unknown resource: ${resource}!`)
  }

  return (<div className={classes.root}>
      
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item>
            <Avatar className={classes.title}>{letter}</Avatar>
          </Grid>
          <Grid item xs>
            <Typography component="h2" variant="h6" gutterBottom>
              {name} / <DateFormat>{created}</DateFormat>
            </Typography>
          </Grid>
        </Grid>
      </Paper>
      
      {!users ? null : (
        <Paper className={classes.paper}>
          <Grid container wrap="nowrap" spacing={2}>
            <Grid item><Avatar className={classes.users}>U</Avatar></Grid>
            <Grid item xs><Typography>{users}</Typography></Grid>
          </Grid>
        </Paper>)}

      {!groups ? null : (
        <Paper className={classes.paper}>
          <Grid container wrap="nowrap" spacing={2}>
            <Grid item><Avatar className={classes.groups}>G</Avatar></Grid>
            <Grid item xs><Typography>{groups}</Typography></Grid>
          </Grid>
        </Paper>)}

      {!projects ? null : (
        <Paper className={classes.paper}>
          <Grid container wrap="nowrap" spacing={2}>
            <Grid item><Avatar className={classes.projects}>P</Avatar></Grid>
            <Grid item xs><Typography>{projects}</Typography></Grid>
          </Grid>
        </Paper>)}      
    </div>
  );
}

export default Summary;

