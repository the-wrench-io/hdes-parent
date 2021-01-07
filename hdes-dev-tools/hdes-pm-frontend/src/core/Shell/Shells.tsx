import React from 'react';
import clsx from 'clsx';
import CssBaseline from '@material-ui/core/CssBaseline';
import Drawer from '@material-ui/core/Drawer';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import Badge from '@material-ui/core/Badge';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import MenuIcon from '@material-ui/icons/Menu';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import NotificationsIcon from '@material-ui/icons/Notifications';

import ShellStyles from './ShellStyles' 


interface ShellProps {
  init: number,
  operations: {
    label: string, icon: React.ReactNode,
    dialog: (open: boolean, handleClose: () => void) => React.ReactNode
  } [],
  views: { label: string, icon: React.ReactNode, onClick: () => void }[],
  tabs: {
    open?: number,
    handleOpen: (index: number) => void,
    entries: { label: string, panel: React.ReactNode }[]
  }
};

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index, ...other }) => {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`wrapped-tabpanel-${index}`}
      aria-labelledby={`wrapped-tab-${index}`}
      {...other} >
      
      {value === index && (<Grid container spacing={3}>{children}</Grid>)}
    </div>
  );
}

const Shell: React.FC<ShellProps> = ({operations, views, tabs}) => {
  
  const classes = ShellStyles();
  
  // Drawer
  const [drawerOpen, setDrawerOpen] = React.useState(true);
  const handleDrawerOpen = () => setDrawerOpen(true);
  const handleDrawerClose = () => setDrawerOpen(false);
  
  // Operations and views
  const [dialogOpen, setDialogOpen] = React.useState(-1);
  const handleDialogOpen = (index: number) => setDialogOpen(index);
  const handleDialogClose = () => setDialogOpen(-1);
  
  // external data 
  const handleTabChange = (event: React.ChangeEvent<{}>, newValue: number) => tabs.handleOpen(newValue);
  
  React.useEffect(() => {
    if(tabs.entries.length === 0) {
      views[0].onClick();
    }
  }, [tabs.entries.length, views]);

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="absolute" className={clsx(classes.appBar, drawerOpen && classes.appBarShift)}>
        <Toolbar className={classes.toolbar}>
          <IconButton edge="start" color="inherit" aria-label="open drawer" 
            className={clsx(classes.menuButton, drawerOpen && classes.menuButtonHidden)} 
            onClick={handleDrawerOpen}>
            <MenuIcon />
          </IconButton>
          <Typography component="h1" variant="h6" color="inherit" noWrap className={classes.title}>
            <Tabs value={tabs.open} onChange={handleTabChange}>
              { tabs.entries.map((tab, index) => 
                  <Tab key={index} value={index} label={tab.label} />
                )
              }
            </Tabs>
          </Typography>
          <IconButton color="inherit" className={classes.appBarIcon}>
            <Badge badgeContent={4} color="secondary">
              <NotificationsIcon />
            </Badge>
          </IconButton>
        </Toolbar>
      </AppBar>

      <Drawer variant="permanent" open={drawerOpen} 
        classes={{
          paper: clsx(classes.drawerPaper, !drawerOpen && classes.drawerPaperClose),
        }}>
        
        <div className={classes.toolbarIcon}>
          <IconButton onClick={handleDrawerClose}>
            <ChevronLeftIcon className={classes.appBarIcon}/>
          </IconButton>
        </div>
        <Divider/>
        <List>
          <ListSubheader inset>Operation</ListSubheader>
          { operations.map((item, index) => (
              <ListItem key={index} button onClick={() => handleDialogOpen(index)}>
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label}/>
              </ListItem>)
            )
          }
        </List>
        <Divider/>
        <List>
          <ListSubheader inset>Views</ListSubheader>
          { views.map((item, index) => (
              <ListItem button key={index} onClick={() => item.onClick()}>
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItem>)
            )
          }
        </List>
      </Drawer>

      <main className={classes.content}>
        <div className={classes.appBarSpacer} />
        <Container maxWidth="lg" className={classes.container}>
          { operations.map((item, index) => (
              <React.Fragment key={index}>
                {item.dialog(dialogOpen === index, handleDialogClose)}
              </React.Fragment>)
            )
          }
          { tabs.entries.map((tab, index) => (
              <TabPanel key={index} index={index} value={tabs.open ? tabs.open : 0}>
                {tab.panel}
              </TabPanel>)
            )
          }
        </Container>
      </main>
    </div>
  );
}

export default Shell;
