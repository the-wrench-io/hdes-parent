import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Grid from '@material-ui/core/Grid';


const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  icons: {
    paddingLeft: 9,
  },
}));


interface ShellView {
  label: string, 
  icon: React.ReactNode, 
  onClick: () => React.ReactNode | void,   
}

interface ShellViewsProps {
  open: boolean,
  children: ShellView[]
};


const ShellViews: React.FC<ShellViewsProps> = ({children, open}) => {
  const classes = useStyles();
  
  const [ active, setActive ] = React.useState<ShellView|undefined>();
  const [ view, setView ] = React.useState<React.ReactNode|undefined>();

  const handleOnClick = (view: ShellView) => {
    const viewNode = view.onClick();
    setActive(view);
    setView(viewNode ? viewNode : undefined);
  }
  
  //React.useEffect(() => {}, [open])
  
  return (<Grid container className={classes.root} spacing={0}>
      <Grid item xs={3} className={classes.icons}>
        <List>
          { children.map((item, index) => (
              <ListItem button key={index} onClick={() => handleOnClick(item)}>
                {item.icon}
              </ListItem>)
            )
          }
        </List>
      </Grid>
      <Grid item xs={9}>{open ? view : null}</Grid>
   </Grid> 
  );
}

export default ShellViews;
