import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';

import LibraryBooksOutlinedIcon from '@material-ui/icons/LibraryBooksOutlined';
import GroupOutlinedIcon from '@material-ui/icons/GroupOutlined';

import LibraryAddOutlinedIcon from '@material-ui/icons/LibraryAddOutlined';
import PersonAddOutlinedIcon from '@material-ui/icons/PersonAddOutlined';

import AddUser from './AddUser';



enum OperationType { USER, PROJECT }

class OperationsState { 
  enabled: boolean;
  type?: OperationType

  constructor(enabled: boolean, type?: OperationType) {
    this.enabled = enabled;
    this.type = type;
  }
}

export const Operations = ({}) => {
  const [open, setOpen] = React.useState(new OperationsState(false));
  
  const openAdd = (type: OperationType) => {
    setOpen(new OperationsState(true, type));
  };
  const closeAdd = () => {
    setOpen(new OperationsState(false));
  };

  return (
    <div>
      <AddUser open={open.enabled && open.type === OperationType.USER} handleClose={closeAdd} /> 
    
      <ListSubheader inset>Operation</ListSubheader>
      <ListItem button onClick={() => openAdd(OperationType.USER)}>
        <ListItemIcon>
          <PersonAddOutlinedIcon />
        </ListItemIcon>
        <ListItemText primary="Add User"/>
      </ListItem>
      <ListItem button onClick={() => openAdd(OperationType.PROJECT)}>
        <ListItemIcon>
          <LibraryAddOutlinedIcon />
        </ListItemIcon>
        <ListItemText primary="Add Project" />
      </ListItem>
    </div>);
}

export const Views = ({}) => {
  return (
    <div>
      <ListSubheader inset>Views</ListSubheader>
      <ListItem button>
        <ListItemIcon>
          <GroupOutlinedIcon />
        </ListItemIcon>
        <ListItemText primary="List Users" />
      </ListItem>
      <ListItem button>
        <ListItemIcon>
          <LibraryBooksOutlinedIcon />
        </ListItemIcon>
        <ListItemText primary="List Projects" />
      </ListItem>
    </div>);
  }