import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import Grid from '@material-ui/core/Grid';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';


const useStyles = (drawer: boolean ) => makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  icons: {
    //paddingLeft: 9,
    //maxWidth: drawer ? '25% !important' : '100% !important'
  },
}))();


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
  const classes = useStyles(open);
  
  const [ active, setActive ] = React.useState<{index: number, view: ShellView}|undefined>();
  const [ view, setView ] = React.useState<React.ReactNode|undefined>();

  const handleOnClick = (index: number, view: ShellView) => {
    const viewNode = view.onClick();
    setActive({index, view});
    setView(viewNode ? viewNode : undefined);
  }
  const handleColor = (index: number) => {
    return active?.index === index && open ? "primary" : "inherit";
  }
  
  //React.useEffect(() => {}, [open])
  
  
  
  return (<Grid container className={classes.root} spacing={0}>
      <Grid item xs={3} className={classes.icons}>
        <List dense={true} >
          { children.map((item, index) => (
            <Tooltip title={item.label} key={index}>
              <ListItem>
                <IconButton color={handleColor(index)} onClick={() => handleOnClick(index, item)}> 
                  {item.icon}
                </IconButton>
              </ListItem>
            </Tooltip>)
          )}
        </List>
      </Grid>
      <Grid item xs={9}>
        {open ? (<>{view}</>) : null}
      </Grid>
   </Grid> 
  );
}

export default ShellViews;
