import React from 'react';
import TextField from '@material-ui/core/TextField';

interface FieldProps {
  defaultValue?: string;
  onChange: (fieldValue: string) => void; 
}

interface ConfigureUserBasicProps {
  name: FieldProps;
  token: FieldProps;
  externalId: FieldProps;
};

const ConfigureUserBasic: React.FC<ConfigureUserBasicProps> = ({name, token, externalId}) => {
  return (<React.Fragment>
  
    <TextField autoFocus margin="dense" id="name" label="User name" type="text" fullWidth
      onChange={({target}) => name.onChange(target.value)}
      defaultValue={name.defaultValue ? name.defaultValue : ''}/>
      
    <TextField margin="dense" id="externalId" label="External ID" type="text" fullWidth 
      onChange={({target}) => externalId.onChange(target.value)}
      defaultValue={externalId.defaultValue ? externalId.defaultValue : ''}/>

    <TextField margin="dense" id="token" label="Token" type="password" fullWidth
      onChange={({target}) => token.onChange(target.value)}
      defaultValue={token.defaultValue ? token.defaultValue : ''}/>
      
  </React.Fragment>);
  
}

export default ConfigureUserBasic;