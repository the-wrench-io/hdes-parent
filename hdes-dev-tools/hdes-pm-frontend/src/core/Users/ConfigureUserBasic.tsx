import React from 'react';
import TextField from '@material-ui/core/TextField';
import { Resources } from '.././Resources';

interface FieldProps {
  defaultValue?: string;
  onChange: (fieldValue: string) => void; 
}

interface ConfigureUserBasicProps {
  id?: string;
  name: FieldProps;
  token: FieldProps;
  externalId: FieldProps;
  email: FieldProps;
};

const ConfigureUserBasic: React.FC<ConfigureUserBasicProps> = ({id, name, token, externalId, email}) => {
  const { session } = React.useContext(Resources.Context);
  
  const nameNotUnique = session.data.users
    .filter(g => g.user.id !== id)
    .filter(g => g.user.name === name.defaultValue).length > 0;
  const nameMustBeDefine = (name.defaultValue ? name.defaultValue.trim(): '').length === 0;
  
  let helperText = undefined;
  if(nameNotUnique) {
    helperText = "User name is not unique!";
  } else if(nameMustBeDefine) {
    helperText = "User name must defined!";
  }
  
  const isError = nameNotUnique || nameMustBeDefine;
  
  return (<React.Fragment>
  
    <TextField autoFocus margin="dense" id="name" label="User name" type="text" fullWidth
      error={isError} helperText={helperText}
      onChange={({target}) => name.onChange(target.value)}
      defaultValue={name.defaultValue ? name.defaultValue : ''}/>

    <TextField margin="dense" id="email" label="Email" type="text" fullWidth 
      onChange={({target}) => email.onChange(target.value)}
      defaultValue={email.defaultValue ? email.defaultValue : ''}/>
      
    <TextField margin="dense" id="externalId" label="External ID" type="text" fullWidth 
      onChange={({target}) => externalId.onChange(target.value)}
      defaultValue={externalId.defaultValue ? externalId.defaultValue : ''}/>

{ token.defaultValue ? 
    (<TextField margin="dense" id="token" label="Token" type="password" fullWidth
      onChange={({target}) => token.onChange(target.value)}
      defaultValue={token.defaultValue ? token.defaultValue : ''}/>) : null
}
  </React.Fragment>);
  
}

export default ConfigureUserBasic;