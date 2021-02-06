import React from 'react';

import { TransferList, Title } from '.././Views';
import { Backend } from '.././Resources';


interface ConfigureGroupProjectsProps {
  adminGroup: boolean;
  projects: {
    all: Backend.ProjectResource[];
    selected: string[];
  };
  onChange: (newSelection: string[]) => void;
};

const ConfigureGroupProjects: React.FC<ConfigureGroupProjectsProps> = ({projects, onChange, adminGroup}) => {


  if(adminGroup) {
    return (<Title secondary notification>This group is of "ADMIN" type and has access to all of the "PROJECTS"</Title>);
  }

  const records: Record<string, Backend.ProjectResource> = {};
  projects.all.forEach(r => records[r.project.id] = r);
  
  return (<TransferList onChange={onChange}
    list={{
     available: { header: "Available Projects", values: projects.all.map(p => p.project.id) },
     selected: { header: "Selected Projects",  values: projects.selected },
     onRender: (id) => records[id].project.name
    }} />);
  
}

export default ConfigureGroupProjects;

