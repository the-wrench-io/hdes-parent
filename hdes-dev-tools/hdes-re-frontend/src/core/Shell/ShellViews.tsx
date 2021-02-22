import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Grid from '@material-ui/core/Grid';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';


import { Resources, Session } from '../Resources';


const useStyles = makeStyles(() => ({
  root: {
    flexGrow: 1,
  },
  icons: {
    //paddingLeft: 9,
    //maxWidth: drawer ? '25% !important' : '100% !important'
  },
}));


interface ShellView {
  id: string;
  label: string; 
  icon: React.ReactNode; 
  onClick: () => React.ReactNode | void;   
}

interface ShellViewsProps {
  open: boolean;
  children: ShellView[];
};

type ActiveView = {index: number, view: ShellView} | undefined;

const findActiveView = (session: Session.Instance, views: ShellView[]): ActiveView => {
  
  const openTab = session.tabs[session.history.open];
  if(!openTab) {
    return undefined;
  }
  let index = 0;
  for(const view of views) {
    if(view.id === openTab.id) {
      return {index, view};
    }
    index++;
  }
  
  return undefined;
}

const ShellViews: React.FC<ShellViewsProps> = ({children, open}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  
  const [ active, setActive ] = React.useState<ActiveView>();
  const [ view, setView ] = React.useState<React.ReactNode|undefined>();

  React.useEffect(() => {
    if(!active) {
      setActive(findActiveView(session, children));
    }
  }, [active, session, children]) 


  const handleOnClick = (index: number, view: ShellView) => {
    const viewNode = view.onClick();
    setActive({index, view});
    setView(viewNode ? viewNode : undefined);
  }
  
  const handleColor = (item: ShellView, index: number) => {
    return active?.view.id === item.id ? "primary" : "inherit";
  }
  
  return (<Grid container className={classes.root} spacing={0}>
      <Grid item xs={3} className={classes.icons}>
        <List dense={true} >
          { children.map((item, index) => (
            <Tooltip title={item.label} key={index}>
              <ListItem>
                <IconButton color={handleColor(item, index)} onClick={() => handleOnClick(index, item)}> 
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
