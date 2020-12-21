import React from 'react';

import { createStyles, Theme, withStyles, WithStyles, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import MuiDialogTitle from '@material-ui/core/DialogTitle';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import TabIcon from '@material-ui/icons/Tab';
import Typography from '@material-ui/core/Typography';

import Grid from '@material-ui/core/Grid';

import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import User from './User';
import ConfigureUserBasic from './ConfigureUserBasic';
import ConfigureUserProjects from './ConfigureUserProjects';
import ConfigureUserGroups from './ConfigureUserGroups';


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



interface AddUserProps {
  open: boolean;
  handleConf: (step: number, user: User) => void;
  handleClose: () => void;
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

const AddUser: React.FC<AddUserProps> = ({open, handleClose, handleConf}) => {
  const classes = useStyles();
  
  const [user, setUser] = React.useState(new User());
  
  
  const [activeStep, setActiveStep] = React.useState(0);
  const handleNext = () => setActiveStep((prevActiveStep) => prevActiveStep + 1)
  const handleBack = () => setActiveStep((prevActiveStep) => prevActiveStep - 1)
  const handleReset = () => setActiveStep(0);
  const tearDown = () => {
    handleClose();
    handleReset();
    setUser(new User());
  };
  
  const steps = [
    <ConfigureUserBasic 
        name={{onChange: (newValue) => setUser(user.withName(newValue))}}
        externalId={{onChange: (newValue) => setUser(user.withExternalId(newValue))}} />,

    <ConfigureUserProjects 
        projects={{ all: [], selected: []}} 
        onChange={(newSelection) => setUser(user.withProjects(newSelection))} />,
        
    <ConfigureUserGroups
        groups={{ all: [], selected: []}} 
        onChange={(newSelection) => setUser(user.withGroups(newSelection))} />    
  ];
  
  return (<Dialog open={open} onClose={tearDown} aria-labelledby="form-dialog-title" maxWidth="sm" fullWidth>
      <DialogTitle id="form-dialog-title" 
        onClose={tearDown} 
        onTab={() => {
          handleConf(activeStep, user)
          tearDown()
        }}>Add New User</DialogTitle>
      
      <DialogContent className={classes.dialog}>
        <Stepper alternativeLabel activeStep={activeStep}>
          <Step><StepLabel>User Info</StepLabel></Step>
          <Step><StepLabel>User Projects</StepLabel></Step>
          <Step><StepLabel>Add Groups</StepLabel></Step>
        </Stepper>   
        {steps[activeStep]}   
      </DialogContent>
      
      <DialogActions>
        {activeStep === steps.length ? (
          <Grid container>
            <Grid container item justify="center">
              <Typography className={classes.instructions}>All steps completed</Typography>  
            </Grid>
            
            <Grid container item justify="center">
              <Button color="secondary" onClick={handleReset} className={classes.button}>Reset</Button>
              <Button variant="contained" color="primary" onClick={handleReset} className={classes.button}>Confirm And Create New User</Button>
            </Grid>
          </Grid>
        ) : (
          <div>
            <Button disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
            <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>
              {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
            </Button>
          </div>
        )}
        
      </DialogActions>
    </Dialog>);
}

export default AddUser;