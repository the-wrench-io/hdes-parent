import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

import User from './User';
import ConfigureUserBasic from './ConfigureUserBasic';
import ConfigureUserProjects from './ConfigureUserProjects';
import ConfigureUserGroups from './ConfigureUserGroups';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%'
    },
    button: {
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    stepper: {
      marginBottom: theme.spacing(3),
      backgroundColor: theme.palette.background.default
    }
  }),
);


interface ConfigureUserProps {
  user: User,
  activeStep: number
};

const ConfigureUser: React.FC<ConfigureUserProps> = (props) => {
  const classes = useStyles();
  const [user, setUser] = React.useState(new User().from(props.user));
  
  const [activeStep, setActiveStep] = React.useState(props.activeStep);
  const handleNext = () => setActiveStep((prevActiveStep) => prevActiveStep + 1)
  const handleBack = () => setActiveStep((prevActiveStep) => prevActiveStep - 1)
  const handleReset = () => setActiveStep(0);
  
  const steps = [
    <ConfigureUserBasic 
        name={{defaultValue: user.name, onChange: (newValue) => setUser(user.withName(newValue))}}
        externalId={{defaultValue: user.externalId, onChange: (newValue) => setUser(user.withExternalId(newValue))}} />,

    <ConfigureUserProjects 
        projects={{ all: [], selected: []}} 
        onChange={(newSelection) => setUser(user.withProjects(newSelection))} />,
        
    <ConfigureUserGroups
        groups={{ all: [], selected: []}} 
        onChange={(newSelection) => setUser(user.withGroups(newSelection))} />    
  ];
  
  return (<div className={classes.root}>
    <Stepper alternativeLabel activeStep={activeStep} className={classes.stepper}>
      <Step><StepLabel>User Info</StepLabel></Step>
      <Step><StepLabel>User Projects</StepLabel></Step>
      <Step><StepLabel>Add Groups</StepLabel></Step>
    </Stepper>   

    <form noValidate autoComplete="off">
    
      
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
          <Grid container justify="flex-end">
            <Button color="secondary" disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
            <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>
              {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
            </Button>
          </Grid>
        )}

      {steps[activeStep]}
    </form>
    </div>
  );
}

export default ConfigureUser;