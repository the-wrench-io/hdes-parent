import * as React from "react";
import { Backend, DemoService } from './Backend';
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
  session: startSession,
  setSession: (current) => current 
});

type ResourceProviderProps = {
  config: {},
  children: React.ReactNode
};


const ResourceProvider: React.FC<ResourceProviderProps> = ({ config, children }) => {
  const [service, setService] = React.useState<Backend.Service>(demoService);
  const [session, setSession] = React.useState<Session.Instance>(startSession);
  
  React.useEffect(() => service.onUpdate((newService: Backend.Service) => setService(newService)), [config, service])
  
  return (
    <ResourceContext.Provider value={{
      service, session, 
      setSession: (command: (session: Session.Instance) => Session.Instance) => setSession((prev) => command(prev))
    }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };