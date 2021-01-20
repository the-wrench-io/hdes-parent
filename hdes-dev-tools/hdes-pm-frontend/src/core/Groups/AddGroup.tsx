import React from 'react';

import { createStyles, Theme, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import { Resources, Backend } from '.././Resources';

import { CreateNewDialog } from './../Views';
import ConfigureGroupBasic from './ConfigureGroupBasic';
import ConfigureGroupUsers from './ConfigureGroupUsers';
import ConfigureGroupProjects from './ConfigureGroupProjects';
import ConfigureGroupSummary from './ConfigureGroupSummary';


type AddGroupProps = {
  open: boolean;
  handleConf: (conf: {builder: Backend.GroupBuilder, activeStep: number, edit: true}) => void;
  handleClose: () => void;
};

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginRight: theme.spacing(1),
    }
  }),
);


const AddGroup: React.FC<AddGroupProps> = ({open, handleClose, handleConf}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  
  const [users, setUsers] = React.useState<Backend.UserResource[]>([]);
  const [projects, setProjects] = React.useState<Backend.ProjectResource[]>([]);
  React.useEffect(() => {  
    service.users.query().onSuccess(setUsers)
    service.projects.query().onSuccess(setProjects)
  }, [service, service.users, service.projects])
  

  const [group, setGroup] = React.useState(service.groups.builder());
  const [activeStep, setActiveStep] = React.useState(0);
  
  const handleNext = () => setActiveStep((prevActiveStep) => prevActiveStep + 1);
  const handleBack = () => setActiveStep((prevActiveStep) => prevActiveStep - 1);
  const handleReset = () => setActiveStep(0);
  
  const onClose = () => {
    handleClose();
    handleReset();
    setGroup(service.groups.builder());
  };
  const onTab = () => {
    onClose()
    handleConf({builder: group, activeStep, edit: true})
  }

  const handleFinish = () => {
    service.groups.save(group)
      .onSuccess(resource => {
        onClose();
      });
  };
  
  const steps = [
    <ConfigureGroupBasic 
        name={{defaultValue: group.name, onChange: (newValue) => setGroup(group.withName(newValue))}}/>,

    <ConfigureGroupUsers 
        users={{all: users, selected: group.users}}
        onChange={(newSelection) => setGroup(group.withUsers(newSelection))} />,
        
    <ConfigureGroupProjects
        projects={{ all: projects, selected: group.projects}} 
        onChange={(newSelection) => setGroup(group.withProjects(newSelection))} />   
  ];

  const content = <React.Fragment>
      <Stepper alternativeLabel activeStep={activeStep}>
        <Step><StepLabel>Group Info</StepLabel></Step>
        <Step><StepLabel>Add Users</StepLabel></Step>
        <Step><StepLabel>Add Project</StepLabel></Step>
      </Stepper>   
      {steps[activeStep]}   
      {activeStep === steps.length ? (<ConfigureGroupSummary group={group} users={users} projects={projects} />): null}
    </React.Fragment>
  const actions = <React.Fragment>{activeStep === steps.length ? 
    ( <div>
        <Button color="secondary" onClick={handleReset} className={classes.button}>Reset</Button>
        <Button variant="contained" color="primary" onClick={handleFinish} className={classes.button}>Confirm</Button>
      </div> ) : (
      <div>
        <Button color="secondary" disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
        <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>Next</Button>
      </div>)}
    </React.Fragment>
  
  return (<CreateNewDialog title="Add New Group"
      open={open}
      onClose={onClose}
      onTab={onTab}
      content={content}
      actions={actions}
    /> );
}

export default AddGroup;