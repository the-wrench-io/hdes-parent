import * as React from "react";
import { DemoService } from './InMemoryService';
import Backend from './Backend';

type ResourceContextType = {
  service: Backend.Service
  updates?: Date
}

const ResourceContext = React.createContext<ResourceContextType>({service: new DemoService() });

type ResourceProviderProps = {
  config: {},
  children: React.ReactNode
};

const ResourceProvider: React.FC<ResourceProviderProps> = ({ config, children }) => {
  const [service, setService] = React.useState<Backend.Service>(new DemoService());
  React.useEffect(() => service.onUpdate((newService) => setService(newService)), [config, service])
  
  return (
    <ResourceContext.Provider value={{ service }}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };