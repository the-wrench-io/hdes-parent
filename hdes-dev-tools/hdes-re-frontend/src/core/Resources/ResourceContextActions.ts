import * as React from "react";

import { 
  SessionReducerAction, SessionReducerActionType, 
  ServiceReducerAction, ServiceReducerActionType } from './Reducers'
import { Session } from './Session'
import { Backend } from './Backend'



interface ResourceContextActions {
  handleWorkspace(newWorkspace: Session.Workspace): void;
  handleData(data: Session.DataInit): void;
  handleSearch(keyword: string): void;
  handleTabAdd(newItem: Session.Tab<any>): void;
  handleTabChange(tabIndex: number): void;
  handleTabClose(tab: Session.Tab<any>): void;
  handleResourceSaved(saved: Backend.AnyResource): void;
  handleResourceDeleted(deleted: Backend.AnyResource): void;
  handleServerError(error: Backend.ServerError): void;
}

const ServiceActionBuilder = {
  setData: (setListeners: Backend.ServiceListeners) => ({ type: ServiceReducerActionType.setListeners, setListeners }),
}

const SessionActionBuilder = {
  setWorkspace: (setWorkspace: Session.Workspace): SessionReducerAction => ({ type: SessionReducerActionType.setWorkspace, setWorkspace }),
  setData: (setData: Session.DataInit): SessionReducerAction => ({ type: SessionReducerActionType.setData, setData }),
  addTab: (addTab: Session.Tab<any>): SessionReducerAction => ({ type: SessionReducerActionType.addTab, addTab }),
  removeTab: (removeTab: string): SessionReducerAction => ({ type: SessionReducerActionType.removeTab, removeTab}),
  changeTab: (changeTab: number): SessionReducerAction => ({ type: SessionReducerActionType.addTab, changeTab}),
  
  setResourceSaved: (setResourceSaved: Backend.AnyResource): SessionReducerAction => ({ type: SessionReducerActionType.setResourceSaved, setResourceSaved }),
  setResourceDeleted: (setResourceDeleted: Backend.AnyResource): SessionReducerAction => ({ type: SessionReducerActionType.setResourceDeleted, setResourceDeleted }), 
  setServerError: (setServerError: Backend.ServerError): SessionReducerAction => ({ type: SessionReducerActionType.setServerError, setServerError }),
  
  setTabData: (id: string, updateCommand: (oldData: any) => any): SessionReducerAction => ({
    type: SessionReducerActionType.setTabData, 
    setTabData: {id, updateCommand}
  }),
 
  setDialog: (setDialog?: string): SessionReducerAction => ({ type: SessionReducerActionType.setDialog, setDialog}),
  setSearch: (keyword: string, tab?: Session.Tab<any>): SessionReducerAction => ({ type: SessionReducerActionType.setSearch, setSearch: { keyword, tab }}),
}



class GenericResourceContextActions implements ResourceContextActions {

  private _sessionDispatch: React.Dispatch<SessionReducerAction>;
  constructor(session: React.Dispatch<SessionReducerAction>) {
    this._sessionDispatch = session;
  }
  handleWorkspace(newWorkspace: Session.Workspace) {
    this._sessionDispatch(SessionActionBuilder.setWorkspace(newWorkspace)) 
  }  
  handleData(data: Session.DataInit) {
    this._sessionDispatch(SessionActionBuilder.setData(data)) 
  }  
  handleSearch(keyword: string) {
    this._sessionDispatch(SessionActionBuilder.setSearch(keyword)) 
  }
  handleTabAdd(newItem: Session.Tab<any>) {
    this._sessionDispatch(SessionActionBuilder.addTab(newItem)); 
  }
  handleTabChange(tabIndex: number) {
    this._sessionDispatch(SessionActionBuilder.changeTab(tabIndex))
  }
  handleTabClose(tab: Session.Tab<any>) {
    this._sessionDispatch(SessionActionBuilder.removeTab(tab.id));
  }
  handleResourceSaved(saved: Backend.AnyResource) {
    this._sessionDispatch(SessionActionBuilder.setResourceSaved(saved));
  }
  handleResourceDeleted(deleted: Backend.AnyResource) {
    this._sessionDispatch(SessionActionBuilder.setResourceSaved(deleted));
  }
  handleServerError(error: Backend.ServerError) {
    this._sessionDispatch(SessionActionBuilder.setServerError(error));
  }
}

export type { ResourceContextActions}
export { GenericResourceContextActions };