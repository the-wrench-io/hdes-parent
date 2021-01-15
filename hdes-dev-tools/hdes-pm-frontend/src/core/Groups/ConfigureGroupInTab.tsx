import React from 'react';

import { Backend, Session } from '.././Resources';
import ConfigureGroup from './ConfigureGroup';



class TabData {
  private _group: Backend.GroupBuilder;
  private _activeStep: number;
  
  constructor(group: Backend.GroupBuilder, activeStep: number) {
    this._group = group;
    this._activeStep = activeStep;
  }
  get activeStep(): number {
    return this._activeStep;
  }
  get group(): Backend.GroupBuilder {
    return this._group;
  }  
  withActiveStep(activeStep: number) {
    return new TabData(this._group, activeStep);
  }
  withGroup(group: Backend.GroupBuilder) {
    return new TabData(group, this._activeStep);
  }
}

const ConfigureGroupInTab = (
  setData: (id: string, updateCommand: (oldData: any) => any) => void,
  onConfirm: (tabId: string, group: Backend.GroupResource) => void,
  defaultId: string, 
  group: Backend.GroupBuilder, 
  activeStep?: number): Session.Tab => {
  
  const id: string = group.id ? group.id : defaultId;
  const label: string = group.id ? group.name + '' : 'add group';
  const init = new TabData(group, activeStep ? activeStep : 0);
      
  const panel = (session: Session.Instance) => {
    const getGroup = (): Backend.GroupBuilder => {
      const data = session.getTabData(id) as TabData;
      return data.group;
    };  
    
    const setGroup = (group: Backend.GroupBuilder): void => {
      setData(id, (oldData: TabData) => oldData.withGroup(group));
    };
    
    const getActiveStep = (): number => {
      const data = session.getTabData(id) as TabData;
      return data.activeStep;
    }  
  
    const setActiveStep = (command: (old: number) => number): void => {
      setData(id, (oldData: TabData) => oldData.withActiveStep(command(oldData.activeStep)));
    };

    return (<ConfigureGroup
      onConfirm={(resource) => onConfirm(id, resource)}
      getActiveStep={getActiveStep} 
      setActiveStep={setActiveStep} 
      setGroup={setGroup} 
      getGroup={getGroup} />);
  };
  return {id, label, panel, data: init};
};

export default ConfigureGroupInTab;


