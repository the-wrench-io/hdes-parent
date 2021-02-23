import * as React from "react";
import { Backend, InMemoryService } from './Backend';
import { Session, createSession } from './Session';
import { SessionReducer, ServiceReducer } from './Reducers';
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

const startService = createService(window.hdesconfig);
const startSession = createSession();

const ResourceContext = React.createContext<ResourceContextType>({
  session: startSession,
  service: startService,
  actions: {} as ResourceContextActions,
});

type ResourceProviderProps = {
  children: React.ReactNode
};

const ResourceProvider: React.FC<ResourceProviderProps> = ({ children }) => {
  console.log("Context provider init");
  
  const serviceListeners = {
    onSave: (saved: Backend.AnyResource) => actions.handleResourceSaved(saved),
    onDelete: (deleted: Backend.AnyResource) => {
      actions.handleResourceDeleted(deleted);
      service.projects.query().onSuccess(projects => actions.handleData({projects}))
      service.heads.query().onSuccess(heads => actions.handleData({heads}))
    },
    onError: (error: Backend.ServerError) => actions.handleServerError(error),
  };
  const sessionListeners: Session.SessionListeners = {
    onWorkspace: (workspace: Session.Workspace) => {
      //console.log("load workspace assets");
    }
  }
  
  const [session, sessionDispatch] = React.useReducer(SessionReducer, startSession.withListeners(sessionListeners));
  const [service] = React.useReducer(ServiceReducer, startService.withListeners(serviceListeners));
  const actions: ResourceContextActions = React.useMemo(() => new GenericResourceContextActions(sessionDispatch), [sessionDispatch]);

  React.useEffect(() => {
    console.log("context effect")
    service.projects.query().onSuccess(projects => actions.handleData({projects}))
    service.heads.query().onSuccess(heads => actions.handleData({heads}))
  }, [actions, service])
  
  return (
    <ResourceContext.Provider value={{ session, actions, service }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };