import * as React from "react";
import InMemoryService from './InMemoryService';
import Backend from './Backend';

type ResourceContextType = {
  service: Backend.Service
}

const ResourceContext = React.createContext<ResourceContextType>({service: new InMemoryService()});

type ResourceProviderProps = {
  config: {},
  children: React.ReactNode
};

const ResourceProvider: React.FC<ResourceProviderProps> = ({ config, children }) => {  
  const service = new InMemoryService();
  return (
    <ResourceContext.Provider value={{service}}>
      {children}
    </ResourceContext.Provider>
  );
};

export { ResourceProvider, ResourceContext };