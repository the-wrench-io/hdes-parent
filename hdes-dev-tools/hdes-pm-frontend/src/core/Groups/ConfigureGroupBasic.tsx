import React from 'react';
import TextField from '@material-ui/core/TextField';

interface FieldProps {
  defaultValue?: string;
  onChange: (fieldValue: string) => void; 
}

interface ConfigureGroupBasicProps {
  name: FieldProps;
};

const ConfigureGroupBasic: React.FC<ConfigureGroupBasicProps> = ({name}) => {
  return (<React.Fragment>
    <TextField autoFocus margin="dense" id="name" label="Group name" type="text" fullWidth
      onChange={({target}) => name.onChange(target.value)}
      defaultValue={name.defaultValue ? name.defaultValue : ''}/>
      
  </React.Fragment>);
  
}

export default ConfigureGroupBasic;