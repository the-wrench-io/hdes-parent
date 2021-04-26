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
      paddingTop: 0,
      paddingBottom: 0,
    },
    nested: {
      paddingLeft: theme.spacing(2),
      paddingTop: 0,
      paddingBottom: 0
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

type FilterType = "G" | "F" | "S" | "D";

interface TreeNodeViewProps {
  filters: FilterType[]
  asset: Backend.AssetBlob;
  snapshot: Backend.SnapshotResource;
};

const TreeNodeView: React.FC<TreeNodeViewProps> = ({asset, snapshot, filters}) => {
  const classes = useStyles();
  const { actions } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);

  const handleClick = () => {
    setOpen(!open);
  };
  
  const openAsset = (subset: string) => actions.handleTabAdd({id: asset.id, label: asset.name});

  const all = filters.length === 0; 
  const isGroup = filters.includes("G") || all;
  const isFlow = filters.includes("F") || all;
  const isDecisionTable = filters.includes("D") || all;
  const isService = filters.includes("S") || all;
  
  const handleFilter = (name: string): boolean => new AstMapper<boolean>(snapshot.ast[name])
    .map("BODY_FL", () => isFlow)
    .map("BODY_DT", () => isDecisionTable)
    .map("BODY_SE", () => isService)
    .any(() => false)
    .build();
  
  const list = (<List component="div" disablePadding dense>
    {asset.ast.filter(handleFilter).map((name, index) => (
      <ListItem key={index} button className={classes.nested} onClick={() => openAsset(name)}>
        <ListItemText primary={name} className={new AstMapper<string>(snapshot.ast[name])
          .map("BODY_FL", () => classes.fl)
          .map("BODY_DT", () => classes.dt)
          .map("BODY_SE", () => classes.st)
          .any(() => classes.st)
          .build() }/>
      </ListItem>
      ))} 
  </List>)


  return isGroup ? 
      (<React.Fragment>
        <ListItem button onClick={handleClick} className={classes.root}>
          <ListItemText secondary={asset.name}  />
          {open ? <ExpandLess /> : <ExpandMore />}
        </ListItem>
        <Collapse in={open} timeout="auto" unmountOnExit>
          {list}    
        </Collapse>
        </React.Fragment>
      ) : 
      list;

}

export default TreeNodeView;
