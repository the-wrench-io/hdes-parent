import React from 'react';
import { makeStyles, withStyles, createStyles, Theme } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Grid from '@material-ui/core/Grid';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import Badge from '@material-ui/core/Badge';
import Avatar from '@material-ui/core/Avatar';
import { LinksApi, ImmutableLinks } from './LinksApi';
import { Resources } from '../../Resources';


const useStyles = makeStyles(() => ({
  root: {
    flexGrow: 1,
    height: '100%',
  },
  icons: {
    //paddingLeft: 9,
    //maxWidth: drawer ? '25% !important' : '100% !important'
  }
}));


interface LinksViewProps {
  open: boolean;
  children: LinksApi.Link[];
  setOpen: (open: boolean) => void
};

const SmallAvatar = withStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "20px",
      height: "20px",
      fontSize: "unset",
      //color: theme.palette.primary.main,
      //background: "transparent",
      border: `2px solid ${theme.palette.background.paper}`,
    },
  }),
)(Avatar);

const LinksView: React.FC<LinksViewProps> = ({children, open, setOpen}) => {
  const classes = useStyles();
  const { session, actions } = Resources.useContext();
  const [ active, setActive ] = React.useState<string|undefined>();
  const [ view, setView ] = React.useState<React.ReactNode|undefined>();
  
  const activeView = { id: active, view };
  const links = new ImmutableLinks({session, actions, values: children});

  React.useEffect(() => {
    if(!session.linkId) {
      return;
    }
    const link = links.find(session.linkId);
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
    
  }, [active, setView, setActive, session, children, setOpen, links]) 

  return (<Grid container className={classes.root} spacing={0}>
      <Grid item xs={3} className={classes.icons}>
        <List dense={true} disablePadding>
          { children.map((item, index) => (
            <Tooltip title={item.label} key={index}>
              <ListItem>
                
                <IconButton disabled={item.enabled === false} 
                  color={links.color(item, activeView)} 
                  onClick={() => links.handle(item, activeView)}>
                  
                  { item.badge ? 
                    (<Badge
                      anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'right',
                      }}
                      badgeContent={<SmallAvatar>{item.badge.text}</SmallAvatar>}>{item.icon}</Badge>) : 
                    (item.icon)
                  } 
                  
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

export {LinksView};
