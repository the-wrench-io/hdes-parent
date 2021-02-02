import React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles, Theme } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import { Mapper, Resources } from './../Resources';


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
  id: string;
  name: string
}

interface NotificationSavedProps {
}

const NotificationSaved: React.FC<NotificationSavedProps> = () => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const [ resource, setResource ] = React.useState<{ open: boolean, total: number, view?: View }>({total: 0, open: false});

  React.useEffect(() => {
    if(session.saved.length === resource.total) {
      return;
    }
    const lastResource = new Mapper.Resource<View>(session.saved[session.saved.length - 1])
      .project(resource => ({ id: resource.project.id, name: resource.project.name }))
      .group(resource => ({ id: resource.group.id, name: resource.group.name }))
      .user(resource => ({ id: resource.user.id, name: resource.user.name }))
      .map();

    setResource({total: session.saved.length, open: true, view: lastResource});

  }, [session, resource, setResource]);


  if(!resource.open) {
    return null;
  }
  
  const handleClose = (_event?: React.SyntheticEvent, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setResource({total: resource.total, open: false, view: resource.view});
  };
  
  return (
    <div className={classes.root}>
      <Snackbar open={resource.open} autoHideDuration={3000} onClose={handleClose}>
       <div className={classes.grid}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Paper className={classes.paper}>{`${resource.view?.name} Saved Successfully!`}</Paper>
            </Grid>
          </Grid>
        </div>
      </Snackbar>
    </div>
  );
}

export default NotificationSaved;



