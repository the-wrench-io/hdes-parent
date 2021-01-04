import React from 'react';
import Typography from '@material-ui/core/Typography';


export default function Title(props: { children: React.ReactNode, secondary?: boolean  }) {
  return (
    <Typography component="h2" variant="h6" color={props.secondary? "secondary" : "primary"} gutterBottom>
      {props.children}
    </Typography>
  );
}
