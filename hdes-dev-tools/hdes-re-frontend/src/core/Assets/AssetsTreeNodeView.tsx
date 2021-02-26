import React from 'react';

import { Resources, Backend } from '../Resources';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Collapse from '@material-ui/core/Collapse';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import Avatar from '@material-ui/core/Avatar';
import { AppTheme } from '../Themes'


const useStyles = makeStyles((theme: AppTheme) =>
  createStyles({
    nested: {
      paddingLeft: theme.spacing(1),
    },
    text: {
      color: theme.palette.primary.main,
    },
    small: {
      width: theme.spacing(3),
      height: theme.spacing(3),
    },
    avatar: {
      minWidth: '30px'
    },
  }),
);

interface AssetsTreeNodeViewProps {
  asset: Backend.AssetBlob;
};

const AssetsTreeNodeView: React.FC<AssetsTreeNodeViewProps> = ({asset}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);

  const handleClick = () => {
    setOpen(!open);
  };
  

/*
    <ListItemIcon>
      <InboxIcon />
    </ListItemIcon>
*/

  

  console.log(asset);

  return (<React.Fragment><ListItem button onClick={handleClick}>
    <ListItemText secondary={asset.name}/>
    {open ? <ExpandLess /> : <ExpandMore />}
    </ListItem>
    <Collapse in={open} timeout="auto" unmountOnExit>
      <List component="div" disablePadding dense>
        {asset.ast.map((name, index) => (
          <ListItem key={index} button className={classes.nested}>
              <ListItemIcon className={classes.avatar}>
              </ListItemIcon>
            <ListItemText primary={name} className={classes.text}/>
          </ListItem>))}      
      </List>
    </Collapse>
    </React.Fragment>);    

}

export default AssetsTreeNodeView;
