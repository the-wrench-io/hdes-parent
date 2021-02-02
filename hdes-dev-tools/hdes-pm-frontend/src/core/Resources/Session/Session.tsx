import {Backend} from '../Backend';

declare namespace Session {
  interface Tab {
    id: string;
    label: string;
    panel: React.ReactNode;
    data?: any;
    edit?: boolean;
  }
  
  interface History {
    previous?: History;
    open: number;
  }
  
  interface Data {
    users: Backend.UserResource[];
    projects: Backend.ProjectResource[];
    groups: Backend.GroupResource[]; 
  }
  
  interface DataInit {
    users?: Backend.UserResource[]; 
    projects?: Backend.ProjectResource[], 
    groups?: Backend.GroupResource[];
  }
  
  interface Instance {  
    tabs: Tab[];
    history: History;
    dialogId?: string;
    search?: string;
    data: Data;

    saved: Backend.AnyResource[];
    errors: Backend.ServerError[];

    withErrors(newError: Backend.ServerError): Instance;
    withSaved(newResource: Backend.AnyResource): Instance;
    
    withData(newData: DataInit): Instance;
    withSearch(keyword: string): Instance;
    withDialog(dialogId?: string): Instance;
    withTabData(tabId: string, updateCommand: (oldData: any) => any): Instance;
    withTab(newTabOrTabIndex: Tab | number): Instance;

    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string): any;
    deleteTab(tabId: string): Session.Instance;
  }
}

export type { Session };