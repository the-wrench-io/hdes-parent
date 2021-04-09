import React from 'react';

import { Resources } from '../Resources';

import { makeStyles, Theme, createStyles, useTheme } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import CircularProgress from '@material-ui/core/CircularProgress';
import TreeNodeView from './TreeNodeView';
import Paper from '@material-ui/core/Paper';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';
import Select from '@material-ui/core/Select';
import Chip from '@material-ui/core/Chip';


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
  
    filters: {
      paddingLeft: theme.spacing(1),
      paddingTop: theme.spacing(2),
      paddingBottom: theme.spacing(2),
    },
    avatar: {
      width: theme.spacing(3),
      height: theme.spacing(3),
      fontSize: 'unset',
      margin: 2,
    }
  }),
);

function getStyles(name: string, filters: string[], theme: Theme) {
  return {
    fontWeight:
      filters.indexOf(name) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}


const availableFilters: {id: string, desc: string, value: FilterType}[] = [
  {id: "1", desc: "Flows",            value: "F"},
  {id: "2", desc: "Services",         value: "S"},
  {id: "3", desc: "Decision tables",  value: "D"},
  {id: "4", desc: "Groups",           value: "G"}
];
type FilterType = "G" | "F" | "S" | "D";


interface TreeViewProps {

};

const TreeView: React.FC<TreeViewProps> = () => {
  const classes = useStyles();
  const theme = useTheme();
  const { session } = React.useContext(Resources.Context);
  const [filters, setFilters] = React.useState<FilterType[]>(["G", "F", "S", "D"]);
  
  const workspace = session.workspace;
  if (!workspace) {
    return <div className={classes.loader}><CircularProgress color="secondary" /></div>
  }

  const snapshot = workspace.snapshot;
  const handleChange = (value: FilterType) => {
    //if(filters.includes(value)) {
      
    //}
    setFilters(filters);
  };
  
  return (
    
    <Paper className={classes.root} elevation={3}>
    <List dense disablePadding component="nav">
      <ListItem className={classes.filters}>

        
        {availableFilters.map((filter) => (
          <Avatar key={filter.id} className={classes.avatar} style={getStyles(filter.id, filters, theme)} onClick={() => handleChange(filter.value)}>
            {filter.value}
          </Avatar>
        ))}
        
      </ListItem>
    
      {Object.values(snapshot.blobs).map((blob, index) => (
        <TreeNodeView key={index} asset={blob} snapshot={snapshot} filters={filters}/>))}
    </List>
  </Paper>);
}

export { TreeView };
