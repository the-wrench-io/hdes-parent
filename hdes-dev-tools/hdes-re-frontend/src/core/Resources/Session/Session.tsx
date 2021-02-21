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
    history: History;
    dialogId?: string;
    search?: string;
    data: Data;

    tabs: readonly Tab<any>[];
    saved: readonly Backend.Commit[];
    deleted: readonly Backend.Commit[];
    errors: readonly Backend.ServerError[];
    
    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string): any;
  }
  
  interface InstanceMutator extends Instance {
    withErrors(newError: Backend.ServerError): InstanceMutator;
    withSaved(newResource: Backend.Commit): InstanceMutator;
    withDeleted(deletedResource: Backend.Commit): InstanceMutator;
    
    withData(newData: DataInit): InstanceMutator;
    withSearch(keyword: string): InstanceMutator;
    withDialog(dialogId?: string): InstanceMutator;
    withTabData(tabId: string, updateCommand: (oldData: any) => any): InstanceMutator;
    withTab(newTabOrTabIndex: Tab<any> | number): InstanceMutator;
    deleteTab(tabId: string): Session.InstanceMutator;
  }
}

export type { Session };