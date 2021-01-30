import * as React from "react";
import { Backend, DemoService, ServerService } from './Backend';
import { Session, createSession, sessionReducer, sessionActions, SessionAction } from './Session';

type ResourceContextType = {
  service: Backend.Service;
  session: Session.Instance;
  setSession: (command: (mutator: typeof sessionActions) => SessionAction) => void;
  updates?: Date;
}

const createService = (hdesconfig: Backend.ServerConfig | undefined) : Backend.Service => {
  
  if(!hdesconfig?.ctx) {
    /* testing */
    hdesconfig = {
      ctx: "http://localhost:8080/hdes/projects-services",
      users: "http://localhost:8080/hdes/projects-services/users",
      groups: "http://localhost:8080/hdes/projects-services/groups",
      projects: "http://localhost:8080/hdes/projects-services/projects",
      headers: {}
    };
    /* */
  }
  
  if(hdesconfig?.ctx) {
    return new ServerService(hdesconfig);  
  }
  return new DemoService();
}

const service = createService(window.hdesconfig);
const startSession = createSession();

const ResourceContext = React.createContext<ResourceContextType>({
  service: service,
  session: startSession,
  setSession: (command) => console.log(command) 
});

type ResourceProviderProps = {
  children: React.ReactNode
};


const ResourceProvider: React.FC<ResourceProviderProps> = ({ children }) => {

  const [session, sessionDispatch] = React.useReducer(sessionReducer, startSession);
  
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