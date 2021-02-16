import React from 'react';

import { createStyles, Theme, withStyles, WithStyles, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import Divider from '@material-ui/core/Divider';
import MuiDialogTitle from '@material-ui/core/DialogTitle';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import Typography from '@material-ui/core/Typography';
import DialogContentText from '@material-ui/core/DialogContentText';
import { Backend } from '../Resources';
import { Resources, Mapper } from '../Resources';


const styles = (theme: Theme) =>
  createStyles({
    root: {
      margin: 0,
      padding: theme.spacing(2),
    },
    button: {
      color: theme.palette.grey[500],
    },
    buttons: {
      position: 'absolute',
      right: theme.spacing(1),
      top: theme.spacing(1),
    },
  });

export interface DialogTitleProps extends WithStyles<typeof styles> {
  id: string;
  children: React.ReactNode;
  onClose: () => void;
}

const DialogTitle = withStyles(styles)((props: DialogTitleProps) => {
  const { children, classes, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      <span className={classes.buttons}>
        <IconButton aria-label="close" onClick={onClose} className={classes.button}>
          <CloseIcon />
        </IconButton>
      </span>
    </MuiDialogTitle>
  );
});

const DialogContent = withStyles((theme: Theme) => ({
  root: {
    padding: theme.spacing(2),
  },
}))(MuiDialogContent);

const DialogActions = withStyles((theme: Theme) => ({
  root: {
    margin: 0,
    padding: theme.spacing(1),
  },
}))(MuiDialogActions);

type DeleteDialogProps = {
  onClose: () => void;
  resource?: Backend.AnyResource
};

type View = {
  name: string;
  type: string;
  users?: string;
  groups?: string; 
  projects?: string;
}


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
    },
    dialog: {
      
    },
    text: {
      padding: theme.spacing(1),
    },
    textNested: {
      paddingLeft: theme.spacing(1),
    },
    button: {
      marginRight: theme.spacing(1),
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
  }),
);


const DeleteNewDialog: React.FC<DeleteDialogProps> = ({onClose, resource}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(resource !== undefined);
  
  React.useEffect(() => setOpen(resource !== undefined), [resource])
  
  const handleClose = () => {
    setOpen(false);
    onClose();
  }
    
  if(!resource) {
    return null;
  }
  
  const view = new Mapper.Resource<View>(resource)
    .project(project => ({
      name: project.project.name,
      type: "project",
      groups: Object.values(project.groups).map(p => p.name).join(", "),
      users: Object.values(project.users).map(p => p.name).join(", ")
    }))
    .group(group => ({
      name: group.group.name,
      type: "group",
      projects: Object.values(group.projects).map(p => p.name).join(", "),
      users: Object.values(group.users).map(p => p.name).join(", ")
    }))
    .user(user => ({
      name: user.user.name,
      type: "user",
      groups: Object.values(user.groups).map(p => p.name).join(", "),
      projects: Object.values(user.projects).map(p => p.name).join(", ")
    })).map();
    
  const handleDelete = () => {
    new Mapper.Resource<Backend.ServiceCallback<Backend.AnyResource>>(resource)
      .project(project => service.projects.delete(project))
      .group(group => service.groups.delete(group))
      .user(user => service.users.delete(user))
      .map().onSuccess(onClose)
  }    
    
  const connection = view.groups || view.projects || view.users ? (<span className={classes.text}>
    <DialogContentText>
      Following relations will <b><i>NOT</i></b> be deleted:
    </DialogContentText>
    <DialogContentText className={classes.textNested}>
      {view.projects ? (<><b>Projects</b>: {view.projects}<br /></>) : ""}
      {view.groups ? (<><b>Groups</b>: {view.groups}<br /></>) : ""}
      {view.users ? (<><b>Users</b>: {view.users}<br /></>) : ""}
    </DialogContentText>
    </span>) : (
    <DialogContentText className={classes.text}>
      There are no relations associated with this {view.type}
    </DialogContentText>);
  
  return (    
    <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title" maxWidth="sm" fullWidth>
      <DialogTitle id="form-dialog-title" onClose={onClose}>Delete {view.type}: <i>{view.name}</i></DialogTitle>
      <DialogContent className={classes.dialog}>
        <DialogContentText id="alert-dialog-slide-description" className={classes.text}>
          Your are about to delete a <i><b>{view.type}</b></i> with name <i><b>{view.name}</b></i>
        </DialogContentText>
        <Divider />
        {connection}
        <Divider />
        <DialogContentText id="alert-dialog-slide-description" align="right" className={classes.text}>
          Are you sure?
        </DialogContentText>
      </DialogContent> 
      <DialogActions>
        <Button onClick={handleClose} color="secondary">Cancel</Button>
        <Button onClick={handleDelete} color="primary">Delete</Button>
      </DialogActions>
    </Dialog>
    );
}

export default DeleteNewDialog;


