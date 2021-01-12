import { Session } from './Session';


class GenericInstance implements Session.Instance {  
  private _tabs: Session.Tab[];
  private _history: Session.History;
  private _dialogId?: string;
  
  constructor(tabs?: Session.Tab[], history?: Session.History, dialogId?: string) {
    this._tabs = tabs ? tabs : [];
    this._history = history ? history : { open: 0 };
    this._dialogId = dialogId;
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
  private next(history: Session.History, tabs?: Session.Tab[]): Session.Instance {
    return new GenericInstance(tabs ? tabs : this.tabs, history, this.dialogId);
  }
  withDialog(dialogId?: string): Session.Instance {
    return new GenericInstance(this._tabs, this._history, dialogId);
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
}

const createSession = () => new GenericInstance();
export { createSession };
