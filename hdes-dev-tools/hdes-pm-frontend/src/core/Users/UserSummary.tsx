import React from 'react';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';

import Typography from '@material-ui/core/Typography';

import { Title, DateFormat } from '.././Views';
import { Backend } from '.././Resources';




const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      maxWidth: 1000,
    },
    
    progress: {
      display: 'flex',
      '& > * + *': {
        marginLeft: theme.spacing(2),
      },
    },
  }),
);

interface UserSummaryProps {
  resource: Backend.UserResource
};

const UserSummary: React.FC<UserSummaryProps> = ({resource}) => {
  const classes = useStyles();
  
  let groups = Object.values(resource.groups).map(p => p.name).join(", ");

  let projects = Object.values(resource.projects).map(p => p.name).join(", ");

  return (
    <div className={classes.root}>
      <Title secondary>{resource.user.name} / {resource.user.externalId} / <DateFormat>{resource.user.created}</DateFormat></Title>
      <div>
        <Typography variant="h6" gutterBottom>Projects</Typography>
        <Typography variant="body1" gutterBottom>{projects}</Typography>
        
        <Typography variant="h6" gutterBottom>Groups</Typography>
        <Typography variant="body1" gutterBottom>{groups}</Typography>
      </div>
    </div>
  );
}

export default UserSummary;

