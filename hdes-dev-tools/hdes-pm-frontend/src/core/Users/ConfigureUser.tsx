import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

import { Resources, Backend } from '.././Resources';
import ConfigureUserBasic from './ConfigureUserBasic';
import ConfigureUserProjects from './ConfigureUserProjects';
import ConfigureUserGroups from './ConfigureUserGroups';
import ConfigureUserSummary from './ConfigureUserSummary';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      maxWidth: '600px',
      padding: theme.spacing(1, 2),
    },
    button: {},
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    stepper: {
      backgroundColor: theme.palette.background.default,
      marginTop: theme.spacing(3),
      marginBottom: theme.spacing(3),
      padding: 0
    }
  }),
);


interface ConfigureUserProps {
  getUser: () => Backend.UserBuilder,
  setUser: (user: Backend.UserBuilder) => void;
  getActiveStep: () => number;
  setActiveStep: (command: (old: number) => number) => void;
  onConfirm: (resource: Backend.UserResource) => void;
};

const ConfigureUser: React.FC<ConfigureUserProps> = (props) => {
  const classes = useStyles();
  const { service, session } = React.useContext(Resources.Context);
  const { groups, projects } = session.data;

 
  const user = props.getUser();
  const setUser = props.setUser;
  
  const activeStep = props.getActiveStep();
  const handleNext = () => props.setActiveStep((prevActiveStep) => prevActiveStep + 1)
  const handleBack = () => props.setActiveStep((prevActiveStep) => prevActiveStep - 1)
  const handleReset = () => props.setActiveStep(() => 0);
  
  const handleFinish = () => {
    service.users.save(user).onSuccess(resource => {
      props.onConfirm(resource);
    });
  };
  
  const steps = [
    <ConfigureUserBasic 
        token={{defaultValue: user.token, onChange: (newValue) => setUser(user.withToken(newValue))}}
        email={{defaultValue: user.email, onChange: (newValue) => setUser(user.withEmail(newValue))}}
        name={{defaultValue: user.name, onChange: (newValue) => setUser(user.withName(newValue))}}
        externalId={{defaultValue: user.externalId, onChange: (newValue) => setUser(user.withExternalId(newValue))}} />,

    <ConfigureUserProjects 
        projects={{all: projects, selected: user.projects}}
        onChange={(newSelection) => setUser(user.withProjects(newSelection))} />,
        
    <ConfigureUserGroups
        groups={{ all: groups, selected: user.groups}}
        onChange={(newSelection) => setUser(user.withGroups(newSelection))} />    
  ];
  
  return (<div className={classes.root}>
    <Stepper alternativeLabel activeStep={activeStep} className={classes.stepper}>
      <Step><StepLabel>User Info</StepLabel></Step>
      <Step><StepLabel>User Projects</StepLabel></Step>
      <Step><StepLabel>Add Groups</StepLabel></Step>
    </Stepper>
    {steps[activeStep]}
    {activeStep === steps.length ? (<ConfigureUserSummary user={user} projects={projects} groups={groups} />): null}
    <form noValidate autoComplete="off">
      {activeStep === steps.length ? (
        <Grid container>
          <Grid container item justify="center">
            <Typography className={classes.instructions}>All steps completed</Typography>  
          </Grid>
          
          <Grid container item justify="center">
            <Button color="secondary" onClick={handleReset} className={classes.button}>Reset</Button>
            <Button variant="contained" color="primary" onClick={handleFinish} className={classes.button}>Confirm</Button>
          </Grid>
        </Grid>
      ) : (
        <Grid container justify="flex-end">
          <Button color="secondary" disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
          <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>Next</Button>
        </Grid>
      )}
    </form>
    </div>
  );
}

export default ConfigureUser;