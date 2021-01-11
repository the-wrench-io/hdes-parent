
declare namespace Session {
  interface Tab {
    id: string;
    label: string;
    panel: React.ReactNode;
    data?: any;
  }
  
  interface History {
    previous?: History;
    open: number;
  }
  
  interface Instance {  
    tabs: Tab[];
    history: History;
    dialogId?: string;
    
    withDialog(dialogId?: string): Instance;
  
    withTabData(tabId: string, data: any): Instance;
    withTab(newTabOrTabIndex: Tab | number): Instance;
    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string, defaultData: any): any;
  }
}



class GenericInstance implements Session.Instance {  
  tabs: Session.Tab[];
  history: Session.History;
  dialogId?: string;
  
  constructor() {
    this.tabs = [];
    this.history = { open: 0 };
  }
  
  next(history: Session.History, tabs?: Session.Tab[]): Session.Instance {
    const result = new GenericInstance();
    result.tabs = tabs ? tabs : this.tabs;
    result.history = history;
    return result;
  }
  withDialog(dialogId?: string): Session.Instance {
    const result = new GenericInstance();
    result.tabs = this.tabs;
    result.history = this.history;
    result.dialogId = dialogId;
    return result;
  }
  withTabData(tabId: string, data: any): Session.Instance {
    const tabs: Session.Tab[] = [];
    for(const tab of this.tabs) {
      if(tabId === tab.id) {
        tabs.push({id: tab.id, label: tab.label, panel: tab.panel, data});
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
  getTabData(tabId: string, defaultData: any): any {
    const tabIndex = this.findTab(tabId);
    if(tabIndex) {
      return this.tabs[tabIndex].data;
    }
    return defaultData;
  }
}

const createSession = () => new GenericInstance();

export type { Session };
export { createSession };
