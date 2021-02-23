import { Session } from './Session';
import { Backend } from '../Backend'; 


class GenericData implements Session.Data {
  private _projects: Backend.ProjectResource[];
  private _heads: Backend.HeadResource[];
  private _snapshot: Backend.Snapshot; 
  
  constructor(projects?: Backend.ProjectResource[], snapshot?: Backend.Snapshot, heads?: Backend.HeadResource[]) {
    this._snapshot = snapshot ? snapshot : {};
    this._projects = projects ? projects : [];
    this._heads = heads ? heads : [];
  }
  get heads() : Backend.HeadResource[] {
    return this._heads;
  }
  get snapshot() : Backend.Snapshot {
    return this._snapshot;
  }
  get projects(): readonly Backend.ProjectResource[] {
    return this._projects;
  }
}


class GenericInstance implements Session.InstanceMutator {  
  private _tabs: Session.Tab<any>[];
  private _history: Session.History;
  private _dialogId?: string;
  private _linkId?: string;
  private _search;
  private _data: Session.Data;
  private _saved: Backend.AnyResource[];
  private _deleted: Backend.AnyResource[];
  private _errors: Backend.ServerError[];
  private _workspace?: Session.Workspace;
  private _listeners: Session.SessionListeners;
  
  constructor(
    listeners: Session.SessionListeners,
    tabs?: Session.Tab<any>[], 
    history?: Session.History, 
    dialogId?: string,
    linkId?: string,
    search?: string, 
    data?: Session.Data,
    saved?: Backend.AnyResource[],
    deleted?: Backend.AnyResource[],
    errors?: Backend.ServerError[],
    workspace?: Session.Workspace) {
    
    this._listeners = listeners;
    this._tabs = tabs ? tabs : [];
    this._history = history ? history : { open: 0 };
    this._dialogId = dialogId;
    this._linkId = linkId;
    this._search = search ? search : '';
    this._data = data ? data : new GenericData();
    this._saved = saved ? saved : [];
    this._deleted = deleted ? deleted : [];
    this._errors = errors ? errors : [];
    this._workspace = workspace;
  }
  get listeners() {
    return this._listeners;
  }
  get linkId() {
    return this._linkId;
  }
  get data() {
    return this._data;
  }
  get search() {
    return this._search;
  }
  get tabs(): readonly Session.Tab<any>[] {
    return this._tabs;
  }
  get history() {
    return this._history;
  }
  get dialogId() {
    return this._dialogId;
  }
  get saved(): readonly Backend.AnyResource[] {
    return this._saved;
  }
  get deleted(): readonly Backend.AnyResource[] {
    return this._deleted;
  }
  get errors(): readonly Backend.ServerError[] {
    return this._errors;
  }
  get workspace(): Session.Workspace | undefined {
    return this._workspace; 
  }
  private next(history: Session.History, tabs?: Session.Tab<any>[]): Session.InstanceMutator {
    const newTabs = tabs ? tabs : this.tabs;
    return new GenericInstance(this._listeners, [...newTabs], history, this.dialogId, this._linkId, this._search, this._data, this._saved, this._deleted, this._errors, this._workspace);
  }
  withWorkspace(workspace: Session.Workspace) {
    this.listeners.onWorkspace(workspace);
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, this._data, this._saved, this._deleted, this._errors, workspace);
  }
  withListeners(listeners: Session.SessionListeners) {
    return new GenericInstance(listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, this._data, this._saved, this._deleted, this._errors, this._workspace);
  }
  withErrors(newError: Backend.ServerError): Session.InstanceMutator {
    const errors = this._errors;
    errors.push(newError);
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, this._data, this._saved, this._deleted, errors, this._workspace);
  }
  withSaved(newResource: Backend.AnyResource): Session.InstanceMutator {
    const saved = [...this._saved];
    saved.push(newResource);
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, this._data, saved, this._deleted, this._errors, this._workspace);
  }
  withDeleted(deletedResource: Backend.AnyResource): Session.InstanceMutator {
    const deleted = [...this._deleted];
    deleted.push(deletedResource);
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, this._data, this._saved, deleted, this._errors, this._workspace);
  }
  withData(init: Session.DataInit): Session.InstanceMutator {
    const snapshot = init.snapshot ? init.snapshot : this._data.snapshot;
    const projects = init.projects ? init.projects : this._data.projects;
    const heads = init.heads ? init.heads : this._data.heads;
    const newData: Session.Data = new GenericData([...projects], snapshot, [...heads]);
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, this._search, newData, this._saved, this._deleted, this._errors, this._workspace);
  }
  withSearch(search?: string): Session.InstanceMutator {
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, this._linkId, search, this._data, this._saved, this._deleted, this._errors, this._workspace);
  }
  withDialog(dialogId?: string): Session.InstanceMutator {
    return new GenericInstance(this._listeners, this._tabs, this._history, dialogId, this._linkId, this._search, this._data, this._saved, this._deleted, this._errors, this._workspace);
  }  
  withLink(linkId?: string): Session.InstanceMutator {
    return new GenericInstance(this._listeners, this._tabs, this._history, this._dialogId, linkId, this._search, this._data, this._saved, this._deleted, this._errors, this._workspace);
  }  
  withTabData(tabId: string, updateCommand: (oldData: any) => any): Session.InstanceMutator {
    const tabs: Session.Tab<any>[] = [];
    for(const tab of this.tabs) {
      if(tabId === tab.id) {
        tabs.push({id: tab.id, label: tab.label, data: updateCommand(tab.data)});
      } else {
        tabs.push(tab);
      }
    }
    return this.next(this.history, tabs);
  }
  withTab(newTabOrTabIndex: Session.Tab<any> | number): Session.InstanceMutator {
    if(typeof newTabOrTabIndex === 'number') {
      const tabIndex = newTabOrTabIndex as number;
      return this.next({ previous: this.history, open: tabIndex });
    }
    
    const newTab = newTabOrTabIndex as Session.Tab<any>;
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
  getTabData<T>(tabId: string): T {
    const tabIndex = this.findTab(tabId);
    if(tabIndex) {
      return this.tabs[tabIndex].data;
    }
    console.error(this);
    throw new Error (`cant find tab: '${tabId}'`);
  }
  deleteTab(tabId: string): Session.InstanceMutator {
    const tabs: Session.Tab<any>[] = [];
    for(const tab of this.tabs) {
      if(tabId !== tab.id) {
        tabs.push(tab);
      }
    }
    return this.next(this.history, tabs).withTab(tabs.length - 1);
  }
}

const createSession = () => new GenericInstance({
  onWorkspace: () => {console.log("selected workspace")}
});
export { createSession };
