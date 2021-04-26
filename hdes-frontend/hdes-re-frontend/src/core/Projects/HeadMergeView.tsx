import React from 'react';

import { createStyles, Theme, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';

import DialogContentText from '@material-ui/core/DialogContentText';
import CallMergeIcon from '@material-ui/icons/CallMerge';

import { Backend, Resources } from '../Resources';
import { IconButtonDialog } from '../Views';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    text: {
      padding: theme.spacing(1),
    }
  }),
);


interface HeadMergeViewProps {
  project: Backend.ProjectResource, 
  head: Backend.Head
};

const HeadMergeView: React.FC<HeadMergeViewProps> = ({project, head}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);

  const handleClose = () => setOpen(false);
  const handleMerge = () => service.merge.save(head).onSuccess(handleClose)

  const headState = project.states[head.name];  
  const disabled = headState.type !== 'ahead';


  const button = { icon: (<CallMergeIcon />), tooltip: disabled ? "Can't merge branch that is behind of main": "Merge this branch to main" };  
  const dialog = {
    title: (<span>Merge branch: '<i>{head.name}</i>' to <i>'main'</i></span>),
    content: (<React.Fragment>
      <DialogContentText id="alert-dialog-slide-description" className={classes.text}>
          Your are about to merge branch: <b><i>{head.name}</i></b> to <b><i>main</i></b>
      </DialogContentText>
      <Divider />
      <DialogContentText id="alert-dialog-slide-description" align="right" className={classes.text}>
          Are you sure?
      </DialogContentText>
    </React.Fragment>),
    actions: (<Button onClick={handleMerge} color="primary">Merge</Button>)
  }
  
  return (<IconButtonDialog disabled={disabled} button={button} dialog={dialog} state={{open, setOpen}}/>)
}

export default HeadMergeView;


