import * as React from "react";
import { Backend, InMemoryService } from './Backend';
import { Session, createSession } from './Session';
import { SessionReducer, ServiceReducer, SessionReducerActionType, ServiceReducerActionType } from './Reducers';
import { ResourceContextActions, GenericResourceContextActions } from './ResourceContextActions'


type ResourceContextType = {
  session: Session.Instance;
  service: Backend.Service;
  actions: ResourceContextActions;
}

const createService = (hdesconfig: Backend.ServerConfig | undefined) : Backend.Service => {
  if(hdesconfig?.ctx) {
    //return new ServerService(hdesconfig);  
  }
  return new InMemoryService();
}

const init = {
  session: createSession(),
  service: createService(window.hdesconfig),
}

const ResourceContext = React.createContext<ResourceContextType>({
  session: init.session,
  service: init.service,
  actions: {} as ResourceContextActions,
});

type ResourceProviderProps = {
  children: React.ReactNode
};

const ResourceProvider: React.FC<ResourceProviderProps> = ({ children }) => {
  const [session, sessionDispatch] = React.useReducer(SessionReducer, init.session);
  const [service, serviceDispatch] = React.useReducer(ServiceReducer, init.service);
  const actions: ResourceContextActions = React.useMemo(() => new GenericResourceContextActions(service, sessionDispatch), [service, sessionDispatch]);
  
  React.useEffect(() => {
    console.log("init service listeners");
    const listeners: Backend.ServiceListeners = {
      id: "context-listeners",
      onSave: (saved: Backend.AnyResource) => actions.handleResourceSaved(saved),
      onDelete: (deleted: Backend.AnyResource) => {
        actions.handleResourceDeleted(deleted);
        service.projects.query().onSuccess(projects => actions.handleData({projects}))
        service.heads.query().onSuccess(heads => actions.handleData({heads}))
      },
      onError: (error: Backend.ServerError) => actions.handleServerError(error),
    };
    
    serviceDispatch({type: ServiceReducerActionType.setListeners, setListeners: listeners});
    service.projects.query().onSuccess(projects => actions.handleData({projects}))
    service.heads.query().onSuccess(heads => actions.handleData({heads}))
  }, [actions, service, serviceDispatch]);
    
  React.useEffect(() => {
    console.log("init session listeners");
    const listeners: Session.SessionListeners = {
      onWorkspace: (workspace: Session.Workspace) => {
        
      }
    }
    sessionDispatch({type: SessionReducerActionType.setListeners, setListeners: listeners});
  }, [actions, service, serviceDispatch]);
  
  
  return (
    <ResourceContext.Provider value={{ session, actions, service }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };