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
    height: '100%',
    paddingBottom: '64px'
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
  enabled?: boolean, 
  onClick: () => React.ReactNode | void;
}

interface ShellLinksProps {
  open: boolean;
  children: ShellView[];
  setOpen: (open: boolean) => void
};

const findActiveLink = (session: Session.Instance, views: ShellView[]): ShellView | undefined => {
  for(const view of views) {
    if(view.id === session.linkId) {
      return view;
    }
  }
  return undefined;
}

const ShellLinks: React.FC<ShellLinksProps> = ({children, open, setOpen}) => {
  const classes = useStyles();
  const { session, actions } = React.useContext(Resources.Context);
  const [ active, setActive ] = React.useState<string|undefined>();
  const [ view, setView ] = React.useState<React.ReactNode|undefined>();

  React.useEffect(() => {
    if(!session.linkId) {
      return;
    }
    const link = findActiveLink(session, children);
    if(!link) {
      return;
    }

    const alreadyOpen = session.linkId === active;
    if(alreadyOpen) {
      return;
    }
    setActive(session.linkId);
    const linkView = link.onClick();
    if(linkView) {
      setView(linkView);
      setOpen(true);
    }
    
  }, [active, setView, setActive, session, children, setOpen]) 

  const handleOnClick = (link: ShellView) => {
    const alreadyOpen = session.linkId === active;    
    if(alreadyOpen && !view) {
      const viewInTab = session.findTab(link.id);
      if(viewInTab === undefined) {
        actions.handleLink();
      }
    }  
    actions.handleLink(link.id); 
  }
  
  const handleColor = (item: ShellView, index: number) => {
    return active === item.id && view ? "primary" : "inherit";
  }
  
  return (<Grid container className={classes.root} spacing={0}>
      <Grid item xs={3} className={classes.icons}>
        <List dense={true} >
          { children.map((item, index) => (
            <Tooltip title={item.label} key={index}>
              <ListItem>
                <IconButton disabled={item.enabled === false} color={handleColor(item, index)} onClick={() => handleOnClick(item)}> 
                  {item.icon}
                </IconButton>
              </ListItem>
            </Tooltip>)
          )}
        </List>
      </Grid>
      <Grid item xs={9}>
        {open && view ? (<>{view}</>) : null}
      </Grid>
   </Grid> 
  );
}

export default ShellLinks;
