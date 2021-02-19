import {Backend} from '../Backend';

declare namespace Session {
  interface Tab<T> {
    id: string;
    label: string;
    data?: T;
    edit?: boolean;
  }
  
  interface History {
    previous?: History;
    open: number;
  }
  
  interface Data {
    projects: readonly Backend.ProjectResource[];
    snapshot: Backend.Snapshot; 
  }
  
  interface DataInit { 
    projects?: Backend.ProjectResource[], 
    snapshot?: Backend.Snapshot;
  }
  
  interface Instance {  
    tabs: readonly Tab<any>[];
    history: History;
    dialogId?: string;
    search?: string;
    data: Data;

    saved: readonly Backend.Commit[];
    deleted: readonly Backend.Commit[];
    errors: readonly Backend.ServerError[];

    withErrors(newError: Backend.ServerError): Instance;
    withSaved(newResource: Backend.Commit): Instance;
    withDeleted(deletedResource: Backend.Commit): Instance;
    
    withData(newData: DataInit): Instance;
    withSearch(keyword: string): Instance;
    withDialog(dialogId?: string): Instance;
    withTabData(tabId: string, updateCommand: (oldData: any) => any): Instance;
    withTab(newTabOrTabIndex: Tab<any> | number): Instance;

    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string): any;
    deleteTab(tabId: string): Session.Instance;
  }
}

export type { Session };