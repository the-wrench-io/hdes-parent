import * as React from "react";
import { DemoService } from './InMemoryService';
import Backend from './Backend';
import { Session, createSession } from './Session';

type ResourceContextType = {
  service: Backend.Service;
  session: Session.Instance;
  setSession: ( command: (session: Session.Instance) => Session.Instance  ) => void;
  updates?: Date;
}

const demoService = new DemoService();
const startSession = createSession();

const ResourceContext = React.createContext<ResourceContextType>({
  service: demoService,
  session: createSession(),
  setSession: (current) => current 
});

type ResourceProviderProps = {
  config: {},
  children: React.ReactNode
};


type ProviderState = {
  service: Backend.Service;
  session: Session.Instance;
}

const ResourceProvider: React.FC<ResourceProviderProps> = ({ config, children }) => {

  const [providerState, setProviderState] = React.useState<ProviderState>({
    session: startSession, service: demoService
  });

  const {service, session} = providerState;
  const setSession = (command: (session: Session.Instance) => Session.Instance  ) => setProviderState(
    prev => ({session: command(prev.session), service: prev.service})
  );
  const setService = (newService: Backend.Service) => setProviderState(
    prev => {
      return ({session: prev.session, service: newService});
    }
  );



  //React.useEffect(() => service.onUpdate((newService: Backend.Service) => setService(newService)), [config, service])
  
  return (
    <ResourceContext.Provider value={{ 
      service, session, setSession: (command) => setSession(command)
    }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };