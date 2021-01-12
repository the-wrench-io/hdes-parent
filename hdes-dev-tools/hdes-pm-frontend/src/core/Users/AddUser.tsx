import React from 'react';

import { createStyles, Theme, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import { Resources, Backend } from '.././Resources';

import { CreateNewDialog } from './../Views';
import ConfigureUserBasic from './ConfigureUserBasic';
import ConfigureUserProjects from './ConfigureUserProjects';
import ConfigureUserGroups from './ConfigureUserGroups';
import ConfigureUserSummary from './ConfigureUserSummary';


interface AddUserProps {
  open: boolean;
  handleConf: (user: Backend.UserBuilder, step: number) => void;
  handleClose: () => void;
};

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginRight: theme.spacing(1),
    }
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
  
  const onClose = () => {
    handleClose();
    handleReset();
    setUser(service.users.builder());
  };

  const handleFinish = () => {
    service.users.save(user)
      .onSuccess(resource => {
        onClose();
      });
  };
  
  const onTab = () => {
    onClose()
    handleConf(user, activeStep)
  }

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
  
  const content = (<React.Fragment>
      <Stepper alternativeLabel activeStep={activeStep}>
        <Step><StepLabel>User Info</StepLabel></Step>
        <Step><StepLabel>User Projects</StepLabel></Step>
        <Step><StepLabel>Add Groups</StepLabel></Step>
      </Stepper>   
      {steps[activeStep]}   
      {activeStep === steps.length ? (<ConfigureUserSummary user={user} projects={projects} groups={groups} />): null}
    </React.Fragment>);
  
  const actions = (<React.Fragment>{activeStep === steps.length ? 
    ( <div>
        <Button color="secondary" onClick={handleReset} className={classes.button}>Reset</Button>
        <Button variant="contained" color="primary" onClick={handleFinish} className={classes.button}>Confirm</Button>
      </div> ) : (
      <div>
        <Button color="secondary" disabled={activeStep === 0} onClick={handleBack} className={classes.button}>Back</Button>
        <Button variant="contained" color="primary" onClick={handleNext} className={classes.button}>Next</Button>
      </div> )}
    </React.Fragment>);
  
  return (<CreateNewDialog title="Add New User"
      open={open}
      onClose={onClose}
      onTab={onTab}
      content={content}
      actions={actions}
    /> );
}

export default AddUser;