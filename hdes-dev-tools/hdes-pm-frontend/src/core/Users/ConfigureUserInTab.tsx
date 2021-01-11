import React from 'react';

import { Backend, Session } from '.././Resources';
import ConfigureUser from './ConfigureUser';


interface TabProps {
  setData: (id: string, command: (oldData: any) => void) => void;
  getData: (id: string, defaultData?: any) => any;  
}

class TabData {
  private _user: Backend.UserBuilder;
  private _activeStep: number;
  
  constructor(user: Backend.UserBuilder, activeStep: number) {
    this._user = user;
    this._activeStep = activeStep;
  }
  get activeStep(): number {
    return this._activeStep;
  }
  get user(): Backend.UserBuilder {
    return this._user;
  }  
  withActiveStep(activeStep: number) {
    return new TabData(this._user, activeStep);
  }
  withUser(user: Backend.UserBuilder) {
    return new TabData(user, this._activeStep);
  }
}

const ConfigureUserInTab = (tab: TabProps, defaultId: string, user: Backend.UserBuilder, activeStep?: number): Session.Tab => {
  const id: string = user.id ? user.id : defaultId;
  const label: string = user.id ? user.name + '' : 'create user';
  const init = new TabData(user, activeStep ? activeStep : 0);
      
  const getUser = (): Backend.UserBuilder => {
    const data = tab.getData(id, init) as TabData;
    return data.user;
  };  
  
  const setUser = (user: Backend.UserBuilder): void => {
    tab.setData(id, (oldData: TabData) => oldData.withUser(user));
  };
  
  const getActiveStep = (): number => {
    const data = tab.getData(id, init) as TabData;
    return data.activeStep;
  }  

  const setActiveStep = (command: (old: number) => number): void => {
    tab.setData(id, (oldData: TabData) => oldData.withActiveStep(command(oldData.activeStep)));
  };

  const panel = <ConfigureUser 
    getActiveStep={getActiveStep} 
    setActiveStep={setActiveStep} 
    setUser={setUser} 
    getUser={getUser} />;
  return {id, label, panel};
};

export default ConfigureUserInTab;