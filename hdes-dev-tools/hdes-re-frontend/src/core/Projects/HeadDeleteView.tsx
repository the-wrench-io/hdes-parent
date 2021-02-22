import React from 'react';

import { createStyles, Theme, withStyles, WithStyles, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

import Divider from '@material-ui/core/Divider';
import DialogContentText from '@material-ui/core/DialogContentText';
import DeleteForeverIcon from '@material-ui/icons/DeleteForever';

import { Backend } from '../Resources';
import { Resources } from '../Resources';

import { IconButtonDialog } from '../Views';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    text: {
      padding: theme.spacing(1),
    }
  }),
);


interface HeadDeleteViewProps {
  project: Backend.ProjectResource, 
  head: Backend.Head
};

const HeadDeleteView: React.FC<HeadDeleteViewProps> = ({project, head}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);

  const handleClose = () => setOpen(false);
  const handleDelete = () => service.heads.delete(head).onSuccess(handleClose)  
  
  const button = { icon: (<DeleteForeverIcon />), tooltip: "Delete This Branch" };  
  const dialog = {
    title: (<span>Delete branch: '<i>{head.name}</i>'</span>),
    content: (<React.Fragment>
      <DialogContentText id="alert-dialog-slide-description" className={classes.text}>
          Your are about to delete branch: <b><i>{head.name}</i></b> from project: <b><i>{project.project.name}</i></b>
      </DialogContentText>
      <Divider />
      <DialogContentText id="alert-dialog-slide-description" align="right" className={classes.text}>
          Are you sure?
      </DialogContentText>
    </React.Fragment>),
    actions: (<Button onClick={handleDelete} color="primary">Delete</Button>)
  }
  
  return (<IconButtonDialog disabled={false} button={button} dialog={dialog} state={{open, setOpen}}/>)
}

export default HeadDeleteView;


