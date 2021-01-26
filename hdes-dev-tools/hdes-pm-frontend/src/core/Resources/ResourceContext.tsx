import * as React from "react";
import { Backend, DemoService } from './Backend';
import { Session, createSession, sessionReducer, sessionActions, SessionAction } from './Session';

type ResourceContextType = {
  service: Backend.Service;
  session: Session.Instance;
  setSession: (command: (mutator: typeof sessionActions) => SessionAction) => void;
  updates?: Date;
}

const demoService = new DemoService();
const startSession = createSession();

const ResourceContext = React.createContext<ResourceContextType>({
  service: demoService,
  session: startSession,
  setSession: (command) => console.log(command) 
});

type ResourceProviderProps = {
  config: {},
  children: React.ReactNode
};


const ResourceProvider: React.FC<ResourceProviderProps> = ({ config, children }) => {

  const [session, sessionDispatch] = React.useReducer(sessionReducer, startSession);
  
  return (
    <ResourceContext.Provider value={{
      service: demoService, 
      session, setSession: (command) => sessionDispatch(command(sessionActions))
    }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };