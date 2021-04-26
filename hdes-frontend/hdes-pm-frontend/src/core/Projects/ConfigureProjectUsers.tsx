import React from 'react';

import { TransferList } from '.././Views';
import { Backend } from '.././Resources';


interface ConfigureProjectUsersProps {
  users: {
    all: readonly Backend.UserResource[];
    selected: string[];
  };
  onChange: (newSelection: string[]) => void;
};

const ConfigureProjectUsers: React.FC<ConfigureProjectUsersProps> = ({users, onChange}) => {

  const records: Record<string, Backend.UserResource> = {};
  users.all.forEach(r => records[r.user.id] = r);
  
  const onRender = (id: string) => records[id].user.name;
  
  return (<TransferList onChange={onChange}
    list={{
     available: { header: "Available Users", values: users.all.map(p => p.user.id) },
     selected: { header: "Selected Users",  values: users.selected },
     onRender: onRender
    }} />);
  
}

export default ConfigureProjectUsers;

