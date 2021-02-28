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
    heads: readonly Backend.HeadResource[];
    snapshot?: Backend.SnapshotResource; 
  }
  
  interface DataInit { 
    projects?: Backend.ProjectResource[], 
    heads?: Backend.HeadResource[], 
    snapshot?: Backend.SnapshotResource;
  }
  
  interface Workspace {
    snapshot: Backend.SnapshotResource;
  }
  
  interface SessionListeners {
    onWorkspace: (workspace: Workspace) => void;
  }
  
  interface Instance {  
    history: History;
    dialogId?: string;
    search?: string;
    data: Data;
    workspace?: Workspace;
    linkId?: string;

    tabs: readonly Tab<any>[];
    saved: readonly Backend.AnyResource[];
    deleted: readonly Backend.AnyResource[];
    errors: readonly Backend.ServerError[];
    
    findTab(newTabId: string): number | undefined;
    getTabData(tabId: string): any;
  }
  
  interface InstanceMutator extends Instance {
    withListeners: (listeners: SessionListeners) => InstanceMutator;
    withErrors(newError: Backend.ServerError): InstanceMutator;
    withSaved(newResource: Backend.AnyResource): InstanceMutator;
    withDeleted(deletedResource: Backend.AnyResource): InstanceMutator;
    
    withWorkspace(newWorkspace: Workspace): InstanceMutator;
    withData(newData: DataInit): InstanceMutator;
    withSearch(keyword: string): InstanceMutator;
    withDialog(dialogId?: string): InstanceMutator;
    withLink(id?: string): InstanceMutator;
    withTabData(tabId: string, updateCommand: (oldData: any) => any): InstanceMutator;
    withTab(newTabOrTabIndex: Tab<any> | number): InstanceMutator;
    deleteTab(tabId: string): Session.InstanceMutator;
  }
}

export type { Session };