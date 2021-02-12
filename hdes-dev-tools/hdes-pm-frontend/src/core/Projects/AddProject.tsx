import React from 'react';

import { createStyles, Theme, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';

import { Resources, Backend } from '.././Resources';

import { CreateNewDialog } from './../Views';
import ConfigureProjectBasic from './ConfigureProjectBasic';
import ConfigureProjectUsers from './ConfigureProjectUsers';
import ConfigureProjectGroups from './ConfigureProjectGroups';
import ConfigureProjectSummary from './ConfigureProjectSummary';


type AddProjectProps = {
  open: boolean;
  handleConf: (conf: {builder: Backend.ProjectBuilder, activeStep: number, edit: true}) => void;
  handleClose: () => void;
};

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginRight: theme.spacing(1),
    }
  }),
);


const AddProject: React.FC<AddProjectProps> = ({open, handleClose, handleConf}) => {
  const classes = useStyles();
  const { service, session } = React.useContext(Resources.Context);
  const { users, groups } = session.data;


  const [project, setProject] = React.useState(service.projects.builder());
  const [activeStep, setActiveStep] = React.useState(0);
  
  const handleNext = () => setActiveStep((prevActiveStep) => prevActiveStep + 1);
  const handleBack = () => setActiveStep((prevActiveStep) => prevActiveStep - 1);
  const handleReset = () => setActiveStep(0);
  
  const onClose = () => {
    handleClose();
    handleReset();
    setProject(service.projects.builder());
  };
  const onTab = () => {
    onClose()
    handleConf({builder: project, activeStep, edit: true})
  }

  const handleFinish = () => {
    service.projects.save(project)
      .onSuccess(resource => {
        onClose();
      });
  };
  
  const steps = [
    <ConfigureProjectBasic
        id={project.id}
        name={{defaultValue: project.name, onChange: (newValue) => setProject(project.withName(newValue))}}/>,

    <ConfigureProjectUsers 
        users={{all: users, selected: project.users}}
        onChange={(newSelection) => setProject(project.withUsers(newSelection))} />,
        
    <ConfigureProjectGroups
        groups={{ all: groups, selected: project.groups}} 
        onChange={(newSelection) => setProject(project.withGroups(newSelection))} />   
  ];

  const content = <React.Fragment>
      <Stepper alternativeLabel activeStep={activeStep}>
        <Step><StepLabel>Project Info</StepLabel></Step>
        <Step><StepLabel>Add Users</StepLabel></Step>
        <Step><StepLabel>Add Groups</StepLabel></Step>
      </Stepper>   
      {steps[activeStep]}   
      {activeStep === steps.length ? (<ConfigureProjectSummary project={project} users={users} groups={groups} />): null}
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
  
  return (<CreateNewDialog title="Add New Project"
      open={open}
      onClose={onClose}
      onTab={onTab}
      content={content}
      actions={actions}
    /> );
}

export default AddProject;