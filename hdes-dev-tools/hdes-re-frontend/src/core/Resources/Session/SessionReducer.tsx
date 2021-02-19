import { Backend } from '../Backend';
import { Session } from './Session';

enum SessionActionType {
  addTab, removeTab, changeTab,
  setTabData, setDialog, setSearch, setData,
   
  setResourceSaved, setResourceDeleted, setServerError
}

interface SessionAction {
  type: SessionActionType;
  
  setData?: Session.DataInit;
  setDialog?: string;
  setSearch?: { keyword?: string, tab?: Session.Tab<any> };
  addTab?: Session.Tab<any>;
  removeTab?: string;
  changeTab?: number;
  setServerError?: Backend.ServerError;
  setResourceSaved?: Backend.Commit;
  setResourceDeleted?: Backend.Commit;
  setTabData?: {id: string, updateCommand: (oldData: any) => any};
}

const sessionActions = {
  setData: (setData: Session.DataInit) => ({ type: SessionActionType.setData, setData }),
  addTab: (addTab: Session.Tab<any>) => ({ type: SessionActionType.addTab, addTab }),
  removeTab: (removeTab: string) => ({ type: SessionActionType.removeTab, removeTab}),
  changeTab: (changeTab: number) => ({ type: SessionActionType.addTab, changeTab}),
  
  setResourceSaved: (setResourceSaved: Backend.Commit) => ({ type: SessionActionType.setResourceSaved, setResourceSaved }),
  setResourceDeleted: (setResourceDeleted: Backend.Commit) => ({ type: SessionActionType.setResourceDeleted, setResourceDeleted }), 
  setServerError: (setServerError: Backend.ServerError) => ({ type: SessionActionType.setServerError, setServerError }),
   
  
  setTabData: (id: string, updateCommand: (oldData: any) => any): SessionAction => ({
    type: SessionActionType.setTabData, 
    setTabData: {id, updateCommand}
  }),
 
  setDialog: (setDialog?: string) => ({ type: SessionActionType.setDialog, setDialog}),
  setSearch: (keyword: string, tab?: Session.Tab<any>) => ({ type: SessionActionType.setSearch, setSearch: { keyword, tab }}),
}


const sessionReducer = (state: Session.Instance, action: SessionAction): Session.Instance => {
  switch (action.type) {
    case SessionActionType.setData: {
      if(!action.setData) {
        console.error("Action data error", action);
        return state;
      }
      return state.withData(action.setData);
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
    
    case SessionActionType.setResourceSaved: {
      if(!action.setResourceSaved) {
        console.error("Action data error", action);
        return state;
      }
      return state.withSaved(action.setResourceSaved)    
    }
    
    case SessionActionType.setResourceDeleted: {
      if(!action.setResourceDeleted) {
        console.error("Action data error", action);
        return state;
      }
      return state.withDeleted(action.setResourceDeleted)    
    }
    
    case SessionActionType.setServerError: {
      if(!action.setServerError) {
        console.error("Action data error", action);
        return state;
      }
      return state.withErrors(action.setServerError)    
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
