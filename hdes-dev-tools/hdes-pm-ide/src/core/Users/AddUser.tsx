import React from 'react';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

type AddUserProps = {
  open: boolean,
  handleClose: () => void
};

const AddUser: React.FC<AddUserProps> = ({open, handleClose}) => {
  return <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Add New User</DialogTitle>
        <DialogContent>
          <DialogContentText>
            To add new user to project(s), please enter name and external id(optional).
          </DialogContentText>
          <TextField autoFocus margin="dense" id="name" label="User name" type="text" fullWidth/>
          <TextField margin="dense" id="externalId" label="External ID" type="text" fullWidth />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">Cancel</Button>
          <Button onClick={handleClose} color="primary">Add</Button>
          <Button onClick={handleClose} color="primary">Configure</Button>
        </DialogActions>
      </Dialog>
}

export default AddUser;