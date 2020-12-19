import React from 'react';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

type AddProjectProps = {
  open: boolean,
  handleClose: () => void
};

const AddProject: React.FC<AddProjectProps> = ({open, handleClose}) => {
  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
      <DialogTitle id="form-dialog-title">Add New Project</DialogTitle>
      <DialogContent>
        <DialogContentText>
          To add new project, please enter the project name (required).
        </DialogContentText>
        <TextField autoFocus margin="dense" id="name" label="Project name" type="text" fullWidth/>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">Cancel</Button>
        <Button onClick={handleClose} color="primary">Add</Button>
        <Button onClick={handleClose} color="primary">Configure</Button>
      </DialogActions>
    </Dialog>);
}

export default AddProject;