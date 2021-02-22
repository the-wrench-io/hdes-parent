import React from 'react';

import { createStyles, Theme, withStyles, WithStyles, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import MuiDialogTitle from '@material-ui/core/DialogTitle';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';


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

interface IconButtonDialogProps {
  disabled: boolean;
  button: { 
    tooltip: string; 
    icon: React.ReactChild;
  },
  state: {
    open: boolean;
    setOpen: (open: boolean) => void;
  },
  dialog: {
    title: React.ReactChild;
    content: React.ReactChild;
    actions: React.ReactChild;
  }
};

const IconButtonDialog: React.FC<IconButtonDialogProps> = ({disabled, button, dialog, state}) => {
  const handleOpen = () => state.setOpen(true);
  const handleClose = () => state.setOpen(false);
  
  return (<>
    <Tooltip title={button.tooltip}>
      <IconButton edge="end" aria-label={button.tooltip} onClick={handleOpen} disabled={disabled}>
        {button.icon}
      </IconButton>
    </Tooltip>
    <Dialog open={state.open} onClose={handleClose} aria-labelledby="form-dialog-title" maxWidth="sm" fullWidth>
      <DialogTitle id="form-dialog-title" onClose={handleClose}>{dialog.title}</DialogTitle>
      <DialogContent>
        {dialog.content}
      </DialogContent> 
      <DialogActions>
        <Button onClick={handleClose} color="secondary">Cancel</Button>
        {dialog.actions}
      </DialogActions>
    </Dialog>
  </>);
}

export default IconButtonDialog;