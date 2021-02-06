import React from 'react';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';

import { Backend, Resources } from '.././Resources';

interface FieldProps<T> {
  defaultValue?: T;
  onChange: (fieldValue: T) => void; 
}

interface ConfigureGroupBasicProps {
  id?: string; 
  name: FieldProps<string>;
  matcher: FieldProps<string>;
  type: FieldProps<Backend.GroupType>;
};

const ConfigureGroupBasic: React.FC<ConfigureGroupBasicProps> = ({id, name, matcher, type}) => {
  
  const { session } = React.useContext(Resources.Context);
  
  const handlegGroupTypeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if(event.target.checked) {
      type.onChange("ADMIN") 
    } else {
      type.onChange("USER")
    }
  };
  
  const nameNotUnique = session.data.groups.filter(g => g.group.name === name.defaultValue).length > 0;
  const nameMustBeDefine = (name.defaultValue ? name.defaultValue.trim(): '').length === 0;
  
  let helperText = undefined;
  if(nameNotUnique) {
    helperText = "Group name is not unique!";
  } else if(nameMustBeDefine) {
    helperText = "Group name must defined!";
  }
  
  const isError = nameNotUnique || nameMustBeDefine;

  return (<React.Fragment>
    <TextField autoFocus margin="dense" id="name" label="Group name" type="text" fullWidth
      error={isError} helperText={helperText}  
      onChange={({target}) => name.onChange(target.value)} 
      defaultValue={name.defaultValue ? name.defaultValue : ''}/>
    
    <TextField margin="dense" id="matcher" label="Group matcher regex - example: '.*@resys\\.io$'" type="text" fullWidth
      onChange={({target}) => name.onChange(target.value)}
      defaultValue={matcher.defaultValue ? matcher.defaultValue : ''}/>
    
    <FormControlLabel
      control={<Switch checked={type.defaultValue === "ADMIN"} onChange={handlegGroupTypeChange} name="type" />}
      label="ADMIN Group - can't be modified later" disabled={id ? true : false}/>
  </React.Fragment>);
  
}

export default ConfigureGroupBasic;