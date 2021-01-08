import React from 'react';

import { TransferList } from '.././Views';
import { Backend } from '.././Resources';


interface ConfigureUserProjectsProps {
  projects: {
    all: Backend.ProjectResource[];
    selected: string[];
  };
  onChange: (newSelection: string[]) => void;
};

const ConfigureUserProjects: React.FC<ConfigureUserProjectsProps> = ({projects, onChange}) => {

  const records: Record<string, Backend.ProjectResource> = {};
  projects.all.forEach(r => records[r.project.id] = r);
  
  const onRender = (id: string) => records[id].project.name;
  
  return (<TransferList onChange={onChange}
    list={{
     available: { header: "Available Projects", values: projects.all.map(p => p.project.id) },
     selected: { header: "Selected Projects",  values: projects.selected },
     onRender: onRender
    }} />);
  
}

export default ConfigureUserProjects;

