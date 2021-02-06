import React from 'react';
import TextField from '@material-ui/core/TextField';
import { Resources } from '.././Resources';

interface FieldProps {
  defaultValue?: string;
  onChange: (fieldValue: string) => void; 
}

interface ConfigureProjectBasicProps {
  name: FieldProps;
};

const ConfigureProjectBasic: React.FC<ConfigureProjectBasicProps> = ({name}) => {
  
  const { session } = React.useContext(Resources.Context);
  
  const nameNotUnique = session.data.projects.filter(g => g.project.name === name.defaultValue).length > 0;
  const nameMustBeDefine = (name.defaultValue ? name.defaultValue.trim(): '').length === 0;
  
  let helperText = undefined;
  if(nameNotUnique) {
    helperText = "Project name is not unique!";
  } else if(nameMustBeDefine) {
    helperText = "Project name must defined!";
  }
  
  const isError = nameNotUnique || nameMustBeDefine;

  
  return (<React.Fragment>
    <TextField autoFocus margin="dense" id="name" label="Project name" type="text" fullWidth
      error={isError} helperText={helperText}  
      onChange={({target}) => name.onChange(target.value)}
      defaultValue={name.defaultValue ? name.defaultValue : ''}/>
      
  </React.Fragment>);
  
}

export default ConfigureProjectBasic;