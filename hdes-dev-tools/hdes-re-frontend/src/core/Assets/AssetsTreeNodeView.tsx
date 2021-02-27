import React from 'react';

import { Resources, Backend, AstMapper } from '../Resources';

import { makeStyles, createStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import { AppTheme } from '../Themes'


const useStyles = makeStyles((theme: AppTheme) =>
  createStyles({
    root: {
      paddingLeft: theme.spacing(1),
    },
    nested: {
      paddingLeft: theme.spacing(2),
    },
    dt: {
      color: theme.palette.assets.dt,
    },
    st: {
      color: theme.palette.assets.st,
    },
    fl: {
      color: theme.palette.assets.fl,
    },
    small: {
      width: theme.spacing(3),
      height: theme.spacing(3),
    }
  }),
);

interface AssetsTreeNodeViewProps {
  asset: Backend.AssetBlob;
  snapshot: Backend.SnapshotResource;
};

const AssetsTreeNodeView: React.FC<AssetsTreeNodeViewProps> = ({asset, snapshot}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);

  const handleClick = () => {
    setOpen(!open);
  };

  return (<React.Fragment>
    <ListItem button onClick={handleClick} className={classes.root}>
      <ListItemText secondary={asset.name} />
      {open ? <ExpandLess /> : <ExpandMore />}
    </ListItem>
    <Collapse in={open} timeout="auto" unmountOnExit>
      <List component="div" disablePadding dense>
        {asset.ast.map((name, index) => (
          <ListItem key={index} button className={classes.nested}>
            <ListItemText primary={name} className={new AstMapper<string>(snapshot.ast[name])
              .map("BODY_FL", () => classes.fl)
              .map("BODY_DT", () => classes.dt)
              .map("BODY_SE", () => classes.st)
              .any(() => classes.st)
              .build() }/>
          </ListItem>
          ))}      
      </List>
    </Collapse>
    </React.Fragment>);    

}

export default AssetsTreeNodeView;
