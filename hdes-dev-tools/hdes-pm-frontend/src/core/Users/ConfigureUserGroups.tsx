import React from 'react';

import { TransferList } from '.././Views';
import { Backend } from '.././Resources';


interface ConfigureUserGroupsProps {
  groups: {
    all: Backend.GroupResource[];
    selected: string[];
  };
  onChange: (newSelection: string[]) => void;
};

const ConfigureUserGroups: React.FC<ConfigureUserGroupsProps> = ({groups, onChange}) => {

  const records: Record<string, Backend.GroupResource> = {};
  groups.all.forEach(r => records[r.group.id] = r);
  
  return (<TransferList onChange={onChange}
    list={{
     available: { header: "Available Groups", values: groups.all.map(p => p.group.id) },
     selected: { header: "Selected Groups",  values: groups.selected },
     onRender: (id) => records[id].group.name
    }} />);
  
}

export default ConfigureUserGroups;

