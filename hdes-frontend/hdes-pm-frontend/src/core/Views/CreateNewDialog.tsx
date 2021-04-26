import React from 'react';

import { createStyles, Theme, withStyles, WithStyles, makeStyles } from '@material-ui/core/styles';
import Dialog from '@material-ui/core/Dialog';
import MuiDialogTitle from '@material-ui/core/DialogTitle';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import TabIcon from '@material-ui/icons/Tab';
import Typography from '@material-ui/core/Typography';


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
  onTab: () => void;
}

const DialogTitle = withStyles(styles)((props: DialogTitleProps) => {
  const { children, classes, onClose, onTab, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      <span className={classes.buttons}>
        <IconButton aria-label="edit in tab" onClick={onTab} className={classes.button}>
          <TabIcon />
        </IconButton>
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

type CreateNewDialogProps = {
  open: boolean;
  title: string;
  content: React.ReactNode;
  actions: React.ReactNode;
  onClose: () => void;
  onTab: () => void;
};

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
    },
    dialog: {
      height: '500px',
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


const CreateNewDialog: React.FC<CreateNewDialogProps> = ({open, onClose, onTab, title, content, actions}) => {
  const classes = useStyles();
  return (    
      <Dialog open={open} onClose={onClose} aria-labelledby="form-dialog-title" maxWidth="sm" fullWidth>
      <DialogTitle id="form-dialog-title" onClose={onClose} onTab={onTab}>{title}</DialogTitle>
      <DialogContent className={classes.dialog}>{content}</DialogContent> 
      <DialogActions>{actions}</DialogActions>
    </Dialog>
    );
}

export default CreateNewDialog;


