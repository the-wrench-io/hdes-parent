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
import { Resources } from '../Resources';


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

interface DialogTitleProps extends WithStyles<typeof styles> {
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


interface DeleteDialogProps {
  open: boolean;
  onClose: () => void;
  resource: { 
    project: Backend.ProjectResource, 
    head: Backend.Head
  }
};

const DeleteNewDialog: React.FC<DeleteDialogProps> = ({open, onClose, resource}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  
  if(!open) {
    return null;
  }

  const handleDelete = () => service.heads.delete(resource.head).onSuccess(onClose)
  
  return (    
    <Dialog open={open} onClose={onClose} aria-labelledby="form-dialog-title" maxWidth="sm" fullWidth>
      <DialogTitle id="form-dialog-title" onClose={onClose}>Delete branch: '<i>{resource.head.name}</i>'</DialogTitle>
      <DialogContent className={classes.dialog}>
        <DialogContentText id="alert-dialog-slide-description" className={classes.text}>
          Your are about to delete branch: <b><i>{resource.head.name}</i></b> from project: <b><i>{resource.project.project.name}</i></b>
        </DialogContentText>
        
        <Divider />
        <DialogContentText id="alert-dialog-slide-description" align="right" className={classes.text}>
          Are you sure?
        </DialogContentText>
      </DialogContent> 
      <DialogActions>
        <Button onClick={onClose} color="secondary">Cancel</Button>
        <Button onClick={handleDelete} color="primary">Delete</Button>
      </DialogActions>
    </Dialog>
    );
}

export default DeleteNewDialog;


