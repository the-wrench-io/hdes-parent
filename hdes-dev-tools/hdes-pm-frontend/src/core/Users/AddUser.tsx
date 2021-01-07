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

import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import { Resources, Backend } from '.././Resources';

import ConfigureUserBasic from './ConfigureUserBasic';
import ConfigureUserProjects from './ConfigureUserProjects';
import ConfigureUserGroups from './ConfigureUserGroups';
import ConfigureUserSummary from './ConfigureUserSummary';


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
  handleConf: (step: number, user: Backend.UserBuilder) => void;
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
  
  const { service } = React.useContext(Resources.Context);
  
  const [projects, setProjects] = React.useState<Backend.ProjectResource[]>([]);
  const [groups, setGroups] = React.useState<Backend.GroupResource[]>([]);
  React.useEffect(() => {  
    service.projects.query().onSuccess(setProjects)
    service.groups.query().onSuccess(setGroups)
  }, [service, service.projects, service.groups])
  

  const [user, setUser] = React.useState(service.users.builder());
  const [activeStep, setActiveStep] = React.useState(0);
  
  const handleNext = () => setActiveStep((prevActiveStep) => prevActiveStep + 1);
  const handleBack = () => setActiveStep((prevActiveStep) => prevActiveStep - 1);
  const handleReset = () => setActiveStep(0);
  
  const tearDown = () => {
    handleClose();
    handleReset();
    setUser(service.users.builder());
  };

  const handleFinish = () => {
    service.users.save(user)
      .onSuccess(resource => {
        tearDown();
      });
  };
  
  const steps = [
    <ConfigureUserBasic 
        name={{defaultValue: user.name, onChange: (newValue) => setUser(user.withName(newValue))}}
        externalId={{defaultValue: user.externalId, onChange: (newValue) => setUser(user.withExternalId(newValue))}} />,

    <ConfigureUserProjects 
        projects={{all: projects, selected: user.projects}}
        onChange={(newSelection) => setUser(user.withProjects(newSelection))} />,
        
    <ConfigureUserGroups
        groups={{ all: groups, selected: user.groups}} 
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
        {activeStep === steps.length ? (<ConfigureUserSummary user={user} projects={projects} groups={groups} />): null}
      </DialogContent>
      <DialogActions>
        {activeStep === steps.length ? (
          <div>
            <Button color="secondary" onClick={handleReset} className={classes.button}>Reset</Button>
            <Button variant="contained" color="primary" onClick={handleFinish} className={classes.button}>Confirm</Button>
          </div>
        ) : (
          <div>
            <Button color="secondary" disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
            <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>Next</Button>
          </div>
        )}
      </DialogActions>
    </Dialog>);
}

export default AddUser;