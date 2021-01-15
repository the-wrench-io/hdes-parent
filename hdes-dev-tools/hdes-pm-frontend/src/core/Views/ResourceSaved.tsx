import React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles, Theme } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import { Backend } from './../Resources';
import { ResourceMapper } from './ResourceMapper';


const useStyles = makeStyles((theme: Theme) => ({
  root: {
    width: '100%',
    '& > * + *': {
      marginTop: theme.spacing(2),
    },
  },
  grid: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: 'center',
    color: theme.palette.text.primary,
    border: `1px solid ${theme.palette.primary.light}`
  },
}));

type View = {
  name: string
}

interface ResourceSavedProps {
  resource: Backend.ProjectResource | Backend.UserResource | Backend.GroupResource | undefined;
  onClose: () => void
}

const ResourceSaved: React.FC<ResourceSavedProps> = ({resource, onClose}) => {
  const classes = useStyles();

  const open = resource !== undefined;
  if(!open) {
    return null;
  }
  
  const handleClose = (event?: React.SyntheticEvent, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    onClose();
  };
  
  const view = new ResourceMapper<View>(resource)
    .project(resource => ({ name: resource.project.name }))
    .group(resource => ({ name: resource.group.name }))
    .user(resource => ({ name: resource.user.name }))
    .map();

  return (
    <div className={classes.root}>
      <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
       <div className={classes.grid}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Paper className={classes.paper}>{`${view.name} Saved Successfully!`}</Paper>
            </Grid>
          </Grid>
        </div>
      </Snackbar>
    </div>
  );
}

export default ResourceSaved;