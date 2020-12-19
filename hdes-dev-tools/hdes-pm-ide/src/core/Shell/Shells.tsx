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

type ShellProps = {
  operations: { 
    label: string, icon: React.ReactNode,
    dialog: (open: boolean, handleClose: () => void) => React.ReactNode
  } [],
  views: { label: string, icon: React.ReactNode }[],
  tabs: { label: string, panel: React.ReactNode } []
};

interface TabPanelProps {
  children?: React.ReactNode;
  index: any;
  value: any;
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
  const listOperations = operations.map((item, index) => (
    <ListItem button onClick={() => handleDialogOpen(index)}>
      <ListItemIcon>{item.icon}</ListItemIcon>
      <ListItemText primary={item.label}/>
    </ListItem>));
  const listDialogs = operations.map((item, index) => item.dialog(dialogOpen === index, handleDialogClose));
  const listViews = views.map((item, index) => (
    <ListItem button key={index}>
      <ListItemIcon>{item.icon}</ListItemIcon>
      <ListItemText primary={item.label} />
    </ListItem>
  ));
  const menus = (<React.Fragment>
      <Divider/><List><ListSubheader inset>Operation</ListSubheader>{listOperations}</List>
      <Divider/><List><ListSubheader inset>Views</ListSubheader>{listViews}</List>
    </React.Fragment>)
  
  // Tabs
  const [tabOpen, setTabOpen] = React.useState('0');
  const handleTabChange = (event: React.ChangeEvent<{}>, newValue: string) => setTabOpen(newValue);
  const listTabs = tabs.map((tab, index) => <Tab value={index + ''} label={tab.label} />);
  const listPanels = tabs.map((tab, index) => <TabPanel index={index + ''} value={tabOpen}>{tab.panel}</TabPanel>);
  
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
            <Tabs value={tabOpen} onChange={handleTabChange}>{listTabs}</Tabs>
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
        {menus}
      </Drawer>

      <main className={classes.content}>
        <div className={classes.appBarSpacer} />
        <Container maxWidth="lg" className={classes.container}>{listDialogs}{listPanels}</Container>
      </main>
    </div>
  );
}

export default Shell;
