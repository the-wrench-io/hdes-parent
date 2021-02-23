import { Backend } from '../Backend';
import { Session } from '../Session';

enum SessionReducerActionType {
  addTab, removeTab, changeTab,
  setTabData, setDialog, setLink, setSearch, setData,
   
  setResourceSaved, setResourceDeleted, setServerError,
  setWorkspace, setListeners
}

interface SessionReducerAction {
  type: SessionReducerActionType;
  
  setListeners?: Session.SessionListeners,
  setWorkspace?: Session.Workspace;
  setData?: Session.DataInit;
  setDialog?: string;
  setLink?: string;
  setSearch?: { keyword?: string, tab?: Session.Tab<any> };
  addTab?: Session.Tab<any>;
  removeTab?: string;
  changeTab?: number;
  setServerError?: Backend.ServerError;
  setResourceSaved?: Backend.AnyResource;
  setResourceDeleted?: Backend.AnyResource;
  setTabData?: {id: string, updateCommand: (oldData: any) => any};
}

const SessionReducer = (state: Session.InstanceMutator, action: SessionReducerAction): Session.InstanceMutator => {
  switch (action.type) {
    case SessionReducerActionType.setWorkspace: {
      if(!action.setWorkspace) {
        console.error("Action data error", action);
        return state;
      }
      return state.withWorkspace(action.setWorkspace);
    }    
    case SessionReducerActionType.setListeners: {
      if(!action.setListeners) {
        console.error("Action data error", action);
        return state;
      }
      return state.withListeners(action.setListeners);
    }    
    case SessionReducerActionType.setData: {
      if(!action.setData) {
        console.error("Action data error", action);
        return state;
      }
      return state.withData(action.setData);
    }
    case SessionReducerActionType.addTab: {
      if(action.addTab) {
        return state.withTab(action.addTab); 
      }
      if(action.changeTab !== undefined) {
        return state.withTab(action.changeTab);  
      }
      console.error("Action data error", action);
      return state;
    }
    
    case SessionReducerActionType.changeTab: {
      if(!action.changeTab) {
        console.error("Action data error", action);
        return state;
      }
      return state.withTab(action.changeTab);
    }
    
    case SessionReducerActionType.removeTab: {
      if(!action.removeTab) {
        console.error("Action data error", action);
        return state;
      }
      return state.deleteTab(action.removeTab);      
    }
    
    case SessionReducerActionType.setResourceSaved: {
      if(!action.setResourceSaved) {
        console.error("Action data error", action);
        return state;
      }
      return state.withSaved(action.setResourceSaved)    
    }
    
    case SessionReducerActionType.setResourceDeleted: {
      if(!action.setResourceDeleted) {
        console.error("Action data error", action);
        return state;
      }
      return state.withDeleted(action.setResourceDeleted)    
    }
    
    case SessionReducerActionType.setServerError: {
      if(!action.setServerError) {
        console.error("Action data error", action);
        return state;
      }
      return state.withErrors(action.setServerError)    
    }    
    case SessionReducerActionType.setTabData: {
      if(!action.setTabData) {
        console.error("Action data error", action);
        return state;
      }
      return state.withTabData(action.setTabData.id, action.setTabData.updateCommand);
    }
    
    case SessionReducerActionType.setDialog: {
      return state.withDialog(action.setDialog)
    }
    case SessionReducerActionType.setLink: {
      return state.withLink(action.setLink)
    }
    case SessionReducerActionType.setSearch: {
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

export type { SessionReducerAction }
export { SessionReducer, SessionReducerActionType };
