import React from 'react';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Avatar from '@material-ui/core/Avatar';


import { Title, DateFormat } from '.././Views';
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
    progress: {
      display: 'flex',
      '& > * + *': {
        marginLeft: theme.spacing(2),
      },
    },
  }),
);

interface UserSummaryProps {
  resource: Backend.UserResource
};

const UserSummary: React.FC<UserSummaryProps> = ({resource}) => {
  const classes = useStyles();
  
  let groups = Object.values(resource.groups).map(p => p.name).join(", ");

  let projects = Object.values(resource.projects).map(p => p.name).join(", ");

  return (<div className={classes.root}>
      
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item>
            <Avatar>U</Avatar>
          </Grid>
          <Grid item xs>
            <Typography>
              <Title secondary>{resource.user.name} / {resource.user.externalId} / <DateFormat>{resource.user.created}</DateFormat></Title>
            </Typography>
          </Grid>
        </Grid>
      </Paper>
      
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item>
            <Avatar>P</Avatar>
          </Grid>
          <Grid item xs>
            <Typography>{projects}</Typography>
          </Grid>
        </Grid>
      </Paper>
      <Paper className={classes.paper}>
        <Grid container wrap="nowrap" spacing={2}>
          <Grid item>
            <Avatar>G</Avatar>
          </Grid>
          <Grid item xs>
            <Typography>{groups}</Typography>
          </Grid>
        </Grid>
      </Paper>
    </div>
  );
}

export default UserSummary;

