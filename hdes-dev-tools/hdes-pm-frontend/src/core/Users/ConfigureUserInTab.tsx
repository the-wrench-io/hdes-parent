import React from 'react';

import { Backend, Session } from '.././Resources';
import ConfigureUser from './ConfigureUser';


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
  withActiveStep(activeStep: number) {console.log("active step")
    return new TabData(this._user, activeStep);
  }
  withUser(user: Backend.UserBuilder) {
    return new TabData(user, this._activeStep);
  }
}

const ConfigureUserInTab = (setData: (id: string, updateCommand: (oldData: any) => any) => void, defaultId: string, user: Backend.UserBuilder, activeStep?: number): Session.Tab => {
  const id: string = user.id ? user.id : defaultId;
  const label: string = user.id ? user.name + '' : 'add user';
  const init = new TabData(user, activeStep ? activeStep : 0);

  const panel = (session: Session.Instance) => {
    const getUser = (): Backend.UserBuilder => {
      const data = session.getTabData(id) as TabData;
      return data.user;
    };  
    
    const setUser = (user: Backend.UserBuilder): void => {
      setData(id, (oldData: TabData) => oldData.withUser(user));
    };
    
    const getActiveStep = (): number => {
      const data = session.getTabData(id) as TabData;
      return data.activeStep;
    }  
  
    const setActiveStep = (command: (old: number) => number): void => {
      setData(id, (oldData: TabData) => oldData.withActiveStep(command(oldData.activeStep)));
    };
    
    return (<ConfigureUser 
      getActiveStep={getActiveStep} 
      setActiveStep={setActiveStep} 
      setUser={setUser} 
      getUser={getUser} />);
  };
    
  return {id, label, panel, data: init};
};

export default ConfigureUserInTab;