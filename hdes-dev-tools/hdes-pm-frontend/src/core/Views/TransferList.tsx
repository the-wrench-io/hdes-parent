import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import List from '@material-ui/core/List';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Checkbox from '@material-ui/core/Checkbox';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';

function not(a: string[], b: string[]) {
  return a.filter((value) => b.indexOf(value) === -1);
}

function intersection(a: string[], b: string[]) {
  return a.filter((value) => b.indexOf(value) !== -1);
}

function union(a: string[], b: string[]) {
  return [...a, ...not(b, a)];
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {

    },
    cardHeader: {
      padding: theme.spacing(1, 2),
    },
    card: {
      backgroundColor: 'transparent',
    },
    list: {
      width: 200,
      overflow: 'auto'
    },
    button: {
      margin: theme.spacing(0.5, 0),
    },
  }),
);


interface TransferListProps {
  list: {
    available: {header: string, values: string[]};
    selected: {header: string, values: string[]};
    onRender: (id: string) => React.ReactNode;
  };
  onChange: (newSelection: string[]) => void;
};

const TransferList: React.FC<TransferListProps> = ({list, onChange}) => {
  
  const classes = useStyles();
  const [checked, setChecked] = React.useState<string[]>([]);
  const [left, setLeft] = React.useState<string[]>(list.available.values);
  const [right, setRight] = React.useState<string[]>(list.selected.values);

  const leftChecked = intersection(checked, left);
  const rightChecked = intersection(checked, right);
  
  const handleToggle = (value: string) => () => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    onChange(newChecked)
    setChecked(newChecked);
  };

  const numberOfChecked = (items: string[]) => intersection(checked, items).length;

  const handleToggleAll = (items: string[]) => () => {
    if (numberOfChecked(items) === items.length) {
      setChecked(not(checked, items));
    } else {
      setChecked(union(checked, items));
    }
  };

  const handleCheckedRight = () => {
    setRight(right.concat(leftChecked));
    setLeft(not(left, leftChecked));
    setChecked(not(checked, leftChecked));
  };

  const handleCheckedLeft = () => {
    setLeft(left.concat(rightChecked));
    setRight(not(right, rightChecked));
    setChecked(not(checked, rightChecked));
  };

  const customList = (title: React.ReactNode, items: string[]) => (
    <Card raised={false} elevation={0} className={classes.card}>
    
      <CardHeader className={classes.cardHeader} title={title}
        subheader={`${numberOfChecked(items)}/${items.length} selected`}
        avatar={
          <Checkbox
            onClick={handleToggleAll(items)}
            checked={numberOfChecked(items) === items.length && items.length !== 0}
            indeterminate={numberOfChecked(items) !== items.length && numberOfChecked(items) !== 0}
            disabled={items.length === 0}
            inputProps={{ 'aria-label': 'all items selected' }}
          />
        } />
      
      <Divider />
      <List className={classes.list} dense component="div" role="list">
        {items.map((value: string) => {
          const labelId = `transfer-list-all-item-${value}-label`;
          return (
            <ListItem key={value} role="listitem" button onClick={handleToggle(value)}>
              <ListItemIcon>
                <Checkbox disableRipple tabIndex={-1}
                  checked={checked.indexOf(value) !== -1}
                  inputProps={{ 'aria-labelledby': labelId }} />
              </ListItemIcon>
              <ListItemText id={labelId} primary={list.onRender(value)} />
            </ListItem>
          );
        })}
        <ListItem />
      </List>
    </Card>
  );

  
  return (<Grid container spacing={2} justify="center" alignItems="flex-start" className={classes.root}>
    <Grid item>{customList(list.available.header, left)}</Grid>
    <Grid item>
      <Grid container direction="column" alignItems="center" >
        <Button variant="outlined" size="small" aria-label="move selected right" className={classes.button}
          onClick={handleCheckedRight} 
          disabled={leftChecked.length === 0}>&gt;</Button>
        <Button variant="outlined" size="small" aria-label="move selected left" className={classes.button}
          onClick={handleCheckedLeft}
          disabled={rightChecked.length === 0}>&lt;</Button>
      </Grid>
    </Grid>
    <Grid item>{customList(list.selected.header, right)}</Grid>
  </Grid>);
  
}

export default TransferList;


