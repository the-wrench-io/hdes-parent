import * as React from "react";
import { Backend, InMemoryService } from './Backend';
import { Session, createSession, 
  sessionReducer, sessionActions, SessionAction,
  serviceReducer, //serviceActions, ServiceAction
 } from './Session';


type ResourceContextType = {
  service: Backend.Service;
  session: Session.Instance;
  setSession: (command: (mutator: typeof sessionActions) => SessionAction) => void;
  updates?: Date;
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
  service: startService,
  session: startSession,
  setSession: (command) => console.log(command) 
});

type ResourceProviderProps = {
  children: React.ReactNode
};


const ResourceProvider: React.FC<ResourceProviderProps> = ({ children }) => {
  const [session, sessionDispatch] = React.useReducer(sessionReducer, startSession);  
  
  const [service] = React.useReducer(serviceReducer, startService.withListeners({
    onSave: (saved: Backend.Commit) => {
      sessionDispatch(sessionActions.setResourceSaved(saved))
    },
    onError: (error: Backend.ServerError) => {
      sessionDispatch(sessionActions.setServerError(error))
    },
    onDelete: (deleted: Backend.Commit) => {
      sessionDispatch(sessionActions.setResourceDeleted(deleted))
    }
  }));

  React.useEffect(() => {
    service.projects.query().onSuccess(projects => sessionDispatch(sessionActions.setData({projects})))
    
  }, [sessionDispatch, service])
  
  return (
    <ResourceContext.Provider value={{
      service: service, 
      session, setSession: (command) => sessionDispatch(command(sessionActions))
    }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };