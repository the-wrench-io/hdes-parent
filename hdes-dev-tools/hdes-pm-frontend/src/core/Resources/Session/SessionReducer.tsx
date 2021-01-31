import { Backend } from '../Backend';
import { Session } from './Session';

enum SessionActionType {
  addTab, removeTab, changeTab, savedTab, onConfirm,
  setTabData, setDialog, setSearch,
}

interface SessionAction {
  type: SessionActionType;
  
  setDialog?: string;
  setSearch?: { keyword?: string, tab?: Session.Tab };
  addTab?: Session.Tab;
  removeTab?: string;
  changeTab?: number;
  savedTab?: Backend.AnyResource;
  onConfirm?: {tabId: string, resource: Backend.AnyResource};
  setTabData?: {id: string, updateCommand: (oldData: any) => any};
}

const sessionActions = {
  addTab: (addTab: Session.Tab) => ({ type: SessionActionType.addTab, addTab }),
  removeTab: (removeTab: string) => ({ type: SessionActionType.removeTab, removeTab}),
  changeTab: (changeTab: number) => ({ type: SessionActionType.addTab, changeTab}),
  savedTab: (savedTab: Backend.AnyResource) => ({ type: SessionActionType.savedTab, savedTab }), 
  
  setTabData: (id: string, updateCommand: (oldData: any) => any): SessionAction => ({
    type: SessionActionType.setTabData, 
    setTabData: {id, updateCommand}
  }),
 
  onConfirm: (tabId: string, resource: Backend.AnyResource) => ({
    type: SessionActionType.onConfirm, 
    onConfirm: { tabId, resource }
  }),
 
  setDialog: (setDialog?: string) => ({ type: SessionActionType.setDialog, setDialog}),
  setSearch: (keyword: string, tab?: Session.Tab) => ({ type: SessionActionType.setSearch, setSearch: { keyword, tab }}),
}


const sessionReducer = (state: Session.Instance, action: SessionAction): Session.Instance => {
  switch (action.type) {
    
    case SessionActionType.onConfirm: {
      if(!action.onConfirm) {
        console.error("Action data error", action);
        return state;
      }

      return state
        //setResourceSaved(resource);
        .deleteTab(action.onConfirm.tabId);
    
    }
    
    case SessionActionType.addTab: {
      if(action.addTab) {
        return state.withTab(action.addTab); 
      }
      if(action.changeTab !== undefined) {
        return state.withTab(action.changeTab);  
      }
      console.error("Action data error", action);
      return state;
    }
    
    case SessionActionType.changeTab: {
      if(!action.changeTab) {
        console.error("Action data error", action);
        return state;
      }
      return state.withTab(action.changeTab);
    }
    
    case SessionActionType.removeTab: {
      if(!action.removeTab) {
        console.error("Action data error", action);
        return state;
      }
      return state.deleteTab(action.removeTab);      
    }
    
    case SessionActionType.savedTab: {
      if(!action.savedTab) {
        console.error("Action data error", action);
        return state;
      }
              //setResourceSaved(resource);
      return state      
    }
    
    case SessionActionType.setTabData: {
      if(!action.setTabData) {
        console.error("Action data error", action);
        return state;
      }
      return state.withTabData(action.setTabData.id, action.setTabData.updateCommand);
    }
    
    case SessionActionType.setDialog: {
      return state.withDialog(action.setDialog)
    }
    
    case SessionActionType.setSearch: {
      const search = action.setSearch;
      if(!search) {
        console.error("Action data error", action);
        return state;
      }
      
      const newState = state.withSearch(search.keyword ? search.keyword : "")
      if(search.tab) {
        return newState.withTab(search.tab);        
      }
      return newState;

    }
  }
}

export type { SessionAction }
export { sessionActions, sessionReducer };
