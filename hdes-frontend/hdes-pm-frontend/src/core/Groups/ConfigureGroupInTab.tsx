import React from 'react';

import { Backend, Session, Resources } from '.././Resources';
import ConfigureGroup from './ConfigureGroup';
import ConfigureGroupSummary from './ConfigureGroupSummary';


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

interface PanelProps {}

const ConfigureGroupInTab = (
  defaultId: string, 
  group: Backend.GroupBuilder,
  edit?: boolean,  
  activeStep?: number): Session.Tab => {
  
  const id: string = group.id ? group.id : defaultId;
  const label: string = group.id ? group.name + '' : 'add group';
  const init = new TabData(group, activeStep ? activeStep : 0);
      
  const Panel: React.FC<PanelProps> = () => {
    const { session, setSession } = React.useContext(Resources.Context);
    const setData = (c: (oldData: any) => any) => setSession((session) => session.setTabData(id, c))

    if(!edit) {
      return (<ConfigureGroupSummary group={group} />);
    }

    const getGroup = (): Backend.GroupBuilder => {
      const data = session.getTabData(id) as TabData;
      return data.group;
    };  
    
    const setGroup = (group: Backend.GroupBuilder): void => {
      setData((oldData: TabData) => oldData.withGroup(group));
    };
    
    const getActiveStep = (): number => {
      const data = session.getTabData(id) as TabData;
      return data.activeStep;
    }  
  
    const setActiveStep = (command: (old: number) => number): void => {
      setData((oldData: TabData) => oldData.withActiveStep(command(oldData.activeStep)));
    };

    return (<ConfigureGroup
      onConfirm={(resource) => setSession((session) => session.onConfirm(id, resource))}
      getActiveStep={getActiveStep} 
      setActiveStep={setActiveStep} 
      setGroup={setGroup} 
      getGroup={getGroup} />);
  };
  return {id, label, panel: <Panel />, data: init};
};

export default ConfigureGroupInTab;


