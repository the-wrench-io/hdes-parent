import React from 'react';

import { Backend, Session } from '.././Resources';
import ConfigureProject from './ConfigureProject';


class TabData {
  private _project: Backend.ProjectBuilder;
  private _activeStep: number;
  
  constructor(project: Backend.ProjectBuilder, activeStep: number) {
    this._project = project;
    this._activeStep = activeStep;
  }
  get activeStep(): number {
    return this._activeStep;
  }
  get project(): Backend.ProjectBuilder {
    return this._project;
  }  
  withActiveStep(activeStep: number) {
    return new TabData(this._project, activeStep);
  }
  withProject(project: Backend.ProjectBuilder) {
    return new TabData(project, this._activeStep);
  }
}

const ConfigureProjectInTab = (
  setData: (id: string, updateCommand: (oldData: any) => any) => void, 
  defaultId: string, 
  project: Backend.ProjectBuilder, 
  activeStep?: number): Session.Tab => {
  
  const id: string = project.id ? project.id : defaultId;
  const label: string = project.id ? project.name + '' : 'add project';
  const init = new TabData(project, activeStep ? activeStep : 0);

  const panel = (session: Session.Instance) => {
    const getProject = (): Backend.ProjectBuilder => {
      const data = session.getTabData(id) as TabData;
      return data.project;
    };  
    
    const setProject = (project: Backend.ProjectBuilder): void => {
      setData(id, (oldData: TabData) => oldData.withProject(project));
    };
    
    const getActiveStep = (): number => {
      const data = session.getTabData(id) as TabData;
      return data.activeStep;
    }  
  
    const setActiveStep = (command: (old: number) => number): void => {
      setData(id, (oldData: TabData) => oldData.withActiveStep(command(oldData.activeStep)));
    };

    
    return (<ConfigureProject 
      getActiveStep={getActiveStep} 
      setActiveStep={setActiveStep} 
      setProject={setProject} 
      getProject={getProject} />);
  }
  return {id, label, panel, data: init};
};

export default ConfigureProjectInTab;


