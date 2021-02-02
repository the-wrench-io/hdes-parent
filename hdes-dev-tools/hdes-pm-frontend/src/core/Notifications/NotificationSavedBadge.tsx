import React from 'react';

import Badge from '@material-ui/core/Badge';
import NotificationsIcon from '@material-ui/icons/Notifications';


import { makeStyles, createStyles, Theme } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import MenuList from '@material-ui/core/MenuList';

import { Resources, Mapper } from '.././Resources';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      overflow: 'hidden',
      
      padding: theme.spacing(0, 1),
    },
    paper: {
      maxWidth: 500,
      margin: `${theme.spacing(1)}px auto`,
      padding: theme.spacing(1),
      
    },
  }),
);


type View = {
  name: string;
}

interface BadgePanelProps {
}

const BadgePanel: React.FC<BadgePanelProps> = () => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  
  const handleClose = () => {};
  
  const saved = session.saved.map(r => (
    new Mapper.Resource<View>(r)
      .project(resource => ({name: resource.project.name}))
      .group(resource => ({name: resource.group.name }))
      .user(resource => ({name: resource.user.name }))
      .map()
  )).map((v, index) => (
      <MenuItem key={index} onClick={handleClose}>Saved: {v.name}</MenuItem>
    ));
  
  return (<div className={classes.root}>
    <MenuList>
      {saved}
    </MenuList>
  </div>);
}

interface BadgeButtonProps {
}

const BadgeButton: React.FC<BadgeButtonProps> = () => {
  const { session } = React.useContext(Resources.Context);
  return (<Badge badgeContent={session.saved.length} color="secondary">
    <NotificationsIcon />
  </Badge>);
}

const NotificationSavedBadge = { badge: <BadgeButton />, panel: <BadgePanel /> }
export default NotificationSavedBadge;



