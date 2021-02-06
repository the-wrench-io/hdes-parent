import React from 'react';

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

import { Resources, Backend } from '.././Resources';
import ConfigureGroupBasic from './ConfigureGroupBasic';
import ConfigureGroupUsers from './ConfigureGroupUsers';
import ConfigureGroupProjects from './ConfigureGroupProjects';
import ConfigureGroupSummary from './ConfigureGroupSummary';


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


interface ConfigureGroupProps {
  getGroup: () => Backend.GroupBuilder,
  setGroup: (group: Backend.GroupBuilder) => void;
  getActiveStep: () => number;
  setActiveStep: (command: (old: number) => number) => void;
  onConfirm: (resource: Backend.GroupResource) => void;
};

const ConfigureGroup: React.FC<ConfigureGroupProps> = (props) => {
  const classes = useStyles();
 
  const { service, session } = React.useContext(Resources.Context);
  const { users, projects } = session.data;

  const group = props.getGroup();
  const setGroup = props.setGroup;
  
  const activeStep = props.getActiveStep();
  const handleNext = () => props.setActiveStep((prevActiveStep) => prevActiveStep + 1)
  const handleBack = () => props.setActiveStep((prevActiveStep) => prevActiveStep - 1)
  const handleReset = () => props.setActiveStep(() => 0);
  
  const handleFinish = () => {
    service.groups.save(group).onSuccess(resource => {
      props.onConfirm(resource);
    });
  };
  
  const steps = [
    <ConfigureGroupBasic 
        id={group.id}
        name={{defaultValue: group.name, onChange: (newValue) => setGroup(group.withName(newValue))}} 
        matcher={{defaultValue: group.matcher, onChange: (newValue) => setGroup(group.withMatcher(newValue))}}
        type={{defaultValue: group.type, onChange: (newValue) => setGroup(group.withType(newValue))}}/>,
        
    <ConfigureGroupUsers 
        users={{all: users, selected: group.users}}
        onChange={(newSelection) => setGroup(group.withUsers(newSelection))} />,

    <ConfigureGroupProjects
        adminGroup={group.type === "ADMIN"}
        projects={{ all: projects, selected: group.projects}}
        onChange={(newSelection) => setGroup(group.withProjects(newSelection))} /> 
  ];
  
  return (<div className={classes.root}>
    <Stepper alternativeLabel activeStep={activeStep} className={classes.stepper}>
      <Step><StepLabel>Group Info</StepLabel></Step>
      <Step><StepLabel>Add Users</StepLabel></Step>
      <Step><StepLabel>Add Projects</StepLabel></Step>
    </Stepper>
    {steps[activeStep]}
    {activeStep === steps.length ? (users.length === 0 && group.users.length > 0 ? '...loading' : <ConfigureGroupSummary group={group} users={users} projects={projects} />): null}
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

export default ConfigureGroup;

