import * as React from "react";
import { Backend, DemoService, ServerService } from './Backend';
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
    return new ServerService(hdesconfig);  
  }
  return new DemoService();
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
    onSave: (saved: Backend.AnyResource) => {
      sessionDispatch(sessionActions.setResourceSaved(saved))

      // refresh
      service.users.query().onSuccess(users => sessionDispatch(sessionActions.setData({users})))
      service.projects.query().onSuccess(projects => sessionDispatch(sessionActions.setData({projects})))
      service.groups.query().onSuccess(groups => sessionDispatch(sessionActions.setData({groups})))
      
    },
    onError: (error: Backend.ServerError) => {
      sessionDispatch(sessionActions.setServerError(error))
      
      // refresh
      service.users.query().onSuccess(users => sessionDispatch(sessionActions.setData({users})))
      service.projects.query().onSuccess(projects => sessionDispatch(sessionActions.setData({projects})))
      service.groups.query().onSuccess(groups => sessionDispatch(sessionActions.setData({groups})))
    },
    onDelete: (deleted: Backend.AnyResource) => {
      sessionDispatch(sessionActions.setResourceDeleted(deleted))
      
      // refresh
      service.users.query().onSuccess(users => sessionDispatch(sessionActions.setData({users})))
      service.projects.query().onSuccess(projects => sessionDispatch(sessionActions.setData({projects})))
      service.groups.query().onSuccess(groups => sessionDispatch(sessionActions.setData({groups})))
    }
  }));

  React.useEffect(() => {
    service.users.query().onSuccess(users => sessionDispatch(sessionActions.setData({users})))
    service.projects.query().onSuccess(projects => sessionDispatch(sessionActions.setData({projects})))
    service.groups.query().onSuccess(groups => sessionDispatch(sessionActions.setData({groups})))
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