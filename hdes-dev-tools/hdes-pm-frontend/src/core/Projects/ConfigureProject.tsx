import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

import { Resources, Backend } from '.././Resources';
import ConfigureProjectBasic from './ConfigureProjectBasic';
import ConfigureProjectUsers from './ConfigureProjectUsers';
import ConfigureProjectGroups from './ConfigureProjectGroups';
import ConfigureProjectSummary from './ConfigureProjectSummary';


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


interface ConfigureProjectProps {
  getProject: () => Backend.ProjectBuilder,
  setProject: (user: Backend.ProjectBuilder) => void;
  getActiveStep: () => number;
  setActiveStep: (command: (old: number) => number) => void;
};

const ConfigureProject: React.FC<ConfigureProjectProps> = (props) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
 
  const [users, setUsers] = React.useState<Backend.UserResource[]>([]);
  const [groups, setGroups] = React.useState<Backend.GroupResource[]>([]);

  React.useEffect(() => {
    service.users.query().onSuccess(setUsers)
    service.groups.query().onSuccess(setGroups)
  }, [service, service.projects, service.groups])
 
  const project = props.getProject();
  const setProject = props.setProject;
  
  const activeStep = props.getActiveStep();
  const handleNext = () => props.setActiveStep((prevActiveStep) => prevActiveStep + 1)
  const handleBack = () => props.setActiveStep((prevActiveStep) => prevActiveStep - 1)
  const handleReset = () => props.setActiveStep(() => 0);
  
  const handleFinish = () => {
    service.projects.save(project).onSuccess(resource => {
      
    });
  };
  
  const steps = [
    <ConfigureProjectBasic 
        name={{defaultValue: project.name, onChange: (newValue) => setProject(project.withName(newValue))}} />,

    <ConfigureProjectUsers 
        users={{all: users, selected: project.users}}
        onChange={(newSelection) => setProject(project.withUsers(newSelection))} />,
        
    <ConfigureProjectGroups
        groups={{ all: groups, selected: project.groups}}
        onChange={(newSelection) => setProject(project.withGroups(newSelection))} />    
  ];
  
  return (<div className={classes.root}>
    <Stepper alternativeLabel activeStep={activeStep} className={classes.stepper}>
      <Step><StepLabel>Project Info</StepLabel></Step>
      <Step><StepLabel>Add Users</StepLabel></Step>
      <Step><StepLabel>Add Groups</StepLabel></Step>
    </Stepper>
    {steps[activeStep]}
    {activeStep === steps.length ? (<ConfigureProjectSummary project={project} users={users} groups={groups} />): null}
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

export default ConfigureProject;
