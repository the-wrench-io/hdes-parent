import React from 'react';

import { Resources } from '../Resources';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import ListSubheader from '@material-ui/core/ListSubheader';
import List from '@material-ui/core/List';
import CircularProgress from '@material-ui/core/CircularProgress';
import AssetsTreeNodeView from './AssetsTreeNodeView';
import Paper from '@material-ui/core/Paper';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      height: '100%',
      backgroundColor: theme.palette.background.default,
    },
    subheader: {
      paddingLeft: theme.spacing(1),
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

  const workspace = session.workspace;
  if(!workspace) {
    return <div className={classes.loader}><CircularProgress color="secondary" /></div>
  }

  const snapshot = workspace.snapshot;
  
  return (<Paper className={classes.root} elevation={3}>
    <List dense component="nav">
      
      {Object.values(snapshot.blobs).map((blob, index) => (
        <AssetsTreeNodeView key={index} asset={blob} snapshot={snapshot} />) )}
    </List>

  </Paper>);
}

export default AssetsTreeView;
