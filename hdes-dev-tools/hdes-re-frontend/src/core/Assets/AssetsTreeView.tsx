import React from 'react';

import { Resources } from '../Resources';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import ListSubheader from '@material-ui/core/ListSubheader';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import InboxIcon from '@material-ui/icons/MoveToInbox';
import DraftsIcon from '@material-ui/icons/Drafts';
import SendIcon from '@material-ui/icons/Send';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import StarBorder from '@material-ui/icons/StarBorder';
import Paper from '@material-ui/core/Paper';
import CircularProgress from '@material-ui/core/CircularProgress';
import AssetsTreeNodeView from './AssetsTreeNodeView';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      height: '100%',
      backgroundColor: theme.palette.background.default,
    },
    nested: {
      paddingLeft: theme.spacing(4),
    },
    loader: {
      display: 'flex',
      '& > * + *': {
        marginLeft: theme.spacing(2),
      },
    },
  }),
);

interface AssetsTreeViewProps {
  
};

const AssetsTreeView: React.FC<AssetsTreeViewProps> = () => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(true);


  const workspace = session.workspace;
  if(!workspace) {
    return <div className={classes.loader}><CircularProgress color="secondary" /></div>
  }

  const snapshot = workspace.snapshot;
  const handleClick = () => {
    setOpen(!open);
  };
  
  
  
  
  return (<div className={classes.root}>
    <List dense component="nav" aria-labelledby="nested-list-subheader"
      subheader={
        <ListSubheader component="div" id="nested-list-subheader">
        {workspace.snapshot.project.name} / {workspace.snapshot.head.name}
        </ListSubheader>
      }>
      
      {Object.values(snapshot.blobs).map((blob, index) => (<AssetsTreeNodeView key={index} asset={blob}/>) )}
    </List>

  </div>);
}

export default AssetsTreeView;
