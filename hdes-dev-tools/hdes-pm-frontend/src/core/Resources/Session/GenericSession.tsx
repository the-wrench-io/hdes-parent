import { Session } from './Session';
import { Backend } from '../Backend'; 


class GenericData implements Session.Data {
  private _users: Backend.UserResource[];
  private _projects: Backend.ProjectResource[];
  private _groups: Backend.GroupResource[]; 
  
  constructor(projects?: Backend.ProjectResource[], groups?: Backend.GroupResource[], users?: Backend.UserResource[]) {
    this._users = users ? users : [];
    this._projects = projects ? projects : [];
    this._groups = groups ? groups : [];
  }
  get users() {
    return this._users;
  }
  get projects() {
    return this._projects;
  }
  get groups() {
    return this._groups;
  }
}


class GenericInstance implements Session.Instance {  
  private _tabs: Session.Tab[];
  private _history: Session.History;
  private _dialogId?: string;
  private _search;
  private _data: Session.Data;
  private _saved: Backend.AnyResource[];
  private _errors: Backend.ServerError[];
  
  constructor(
    tabs?: Session.Tab[], 
    history?: Session.History, 
    dialogId?: string, 
    search?: string, 
    data?: Session.Data,
    saved?: Backend.AnyResource[],
    errors?: Backend.ServerError[]) {
      
    this._tabs = tabs ? tabs : [];
    this._history = history ? history : { open: 0 };
    this._dialogId = dialogId;
    this._search = search ? search : '';
    this._data = data ? data : new GenericData();
    this._saved = saved ? saved : [];
    this._errors = errors ? errors : [];
  }
  get data() {
    return this._data;
  }
  get search() {
    return this._search;
  }
  get tabs() {
    return this._tabs;
  }
  get history() {
    return this._history;
  }
  get dialogId() {
    return this._dialogId;
  }
  get saved() {
    return this._saved;
  }
  get errors() {
    return this._errors;
  }
  private next(history: Session.History, tabs?: Session.Tab[]): Session.Instance {
    return new GenericInstance(tabs ? tabs : this.tabs, history, this.dialogId, this._search, this._data, this._saved, this._errors);
  }
  withErrors(newError: Backend.ServerError): Session.Instance {
    const errors = this._errors;
    errors.push(newError);
    return new GenericInstance(this._tabs, this._history, this._dialogId, this._search, this._data, this._saved, errors);
  }
  withSaved(newResource: Backend.AnyResource): Session.Instance {
    const saved = [...this._saved];
    saved.push(newResource);
    return new GenericInstance(this._tabs, this._history, this._dialogId, this._search, this._data, saved, this._errors);
  }
  withData(init: Session.DataInit): Session.Instance {
    const users = init.users ? init.users : this._data.users;
    const groups = init.groups ? init.groups : this._data.groups;
    const projects = init.projects ? init.projects : this._data.projects;
    const newData: Session.Data = new GenericData(projects, groups, users);
    return new GenericInstance(this._tabs, this._history, this._dialogId, this._search, newData, this._saved, this._errors);
  }
  withSearch(search?: string): Session.Instance {
    return new GenericInstance(this._tabs, this._history, this._dialogId, search, this._data, this._saved, this._errors);
  }
  withDialog(dialogId?: string): Session.Instance {
    return new GenericInstance(this._tabs, this._history, dialogId, this._search, this._data, this._saved, this._errors);
  }  
  withTabData(tabId: string, updateCommand: (oldData: any) => any): Session.Instance {
    const tabs: Session.Tab[] = [];
    for(const tab of this.tabs) {
      if(tabId === tab.id) {
        tabs.push({id: tab.id, label: tab.label, panel: tab.panel, data: updateCommand(tab.data)});
      } else {
        tabs.push(tab);
      }
    }
    return this.next(this.history, tabs);
  }
  withTab(newTabOrTabIndex: Session.Tab | number): Session.Instance {
    if(typeof newTabOrTabIndex === 'number') {
      const tabIndex = newTabOrTabIndex as number;
      return this.next({ previous: this.history, open: tabIndex });
    }
    
    const newTab = newTabOrTabIndex as Session.Tab;
    const alreadyOpen = this.findTab(newTab.id);
    if(alreadyOpen !== undefined) {      
      const editModeChange = this.tabs[alreadyOpen].edit !== newTab.edit;
      if(editModeChange && newTab.edit === true) {
        return this.deleteTab(newTab.id).withTab(newTab);
      }      
      return this.next({ previous: this.history, open: alreadyOpen });
    }

    return this.next({ previous: this.history, open: this.tabs.length}, this.tabs.concat(newTab));
  }
  findTab(newTabId: string): number | undefined {
    let index = 0; 
    for(let tab of this.tabs) {
      if(tab.id === newTabId) {
        return index;
      }
      index++
    }
    return undefined;
  }
  getTabData(tabId: string): any {
    const tabIndex = this.findTab(tabId);
    if(tabIndex) {
      return this.tabs[tabIndex].data;
    }
    console.error(this);
    throw new Error (`cant find tab: '${tabId}'`);
  }
  deleteTab(tabId: string): Session.Instance {
    const tabs: Session.Tab[] = [];
    for(const tab of this.tabs) {
      if(tabId !== tab.id) {
        tabs.push(tab);
      }
    }
    return this.next(this.history, tabs).withTab(tabs.length - 1);
  }
}

const createSession = () => new GenericInstance();
export { createSession };
