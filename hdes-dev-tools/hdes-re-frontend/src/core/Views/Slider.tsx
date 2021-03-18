import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';


const useStyles = makeStyles((theme) => ({
  left: {
  },
  right: {
  },
  root: {
    transform: (props: Box) => props.open ? `translateX(0px)` : `translateX(-50%)`,
    transition: theme.transitions.create('all', {
      easing: theme.transitions.easing.easeInOut,
      duration: 1000,
    }),
  }
}));


interface Box {
  open: boolean;
}

const BoxComponent = React.memo((props: { box: Box, left: React.ReactChild, right: React.ReactChild }) => {
  const classes = useStyles(props.box);
  return (
    <Grid container spacing={0} className={classes.root}>
      <Grid item xs={6} className={classes.left}>{props.left}</Grid>
      <Grid item xs={6} className={classes.right}>{props.right}</Grid>
    </Grid>);
});

const Slider: React.FC<{ children: React.ReactChild[], open: boolean }> = ({ children, open }) => {
  if (children.length !== 2) {
    throw new Error(`Slider expects: 2 children but got: '${children.length}'!`)
  }
  return <BoxComponent box={{open}} left={children[0]} right={children[1]}/>
}

export default Slider;