import React from 'react';
import Typography from '@material-ui/core/Typography';


export default function Title(props: { 
  children: React.ReactNode, 
  secondary?: boolean,
  disabled?: boolean }) {
    
  let color: 'primary' | 'secondary' | 'textSecondary' = props.secondary? "secondary" : "primary";
    
  if(props.disabled) {
    color = "textSecondary";
  }
  return (
    <Typography component="h2" variant="h6" color={color} gutterBottom>
      {props.children}
    </Typography>
  );
}
