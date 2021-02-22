import React from 'react';
import clsx from 'clsx';
import { useTheme } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import Drawer from '@material-ui/core/Drawer';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';

import Container from '@material-ui/core/Container';
import MenuIcon from '@material-ui/icons/Menu';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

import InputBase from '@material-ui/core/InputBase';
import SearchIcon from '@material-ui/icons/Search';

import ShellStyles from './ShellStyles' 
import ShellBadges from './ShellBadges';
import ShellViews from './ShellViews';


interface ShellProps {
  views: { 
    id: string,
    label: string, 
    icon: React.ReactNode, 
    onClick: () => React.ReactNode | void, 
  }[],
  badges: {
    label: string,
    icon: React.ReactNode;
    onClick: () => React.ReactNode,
  }[],
  tabs: { 
    items: React.ReactNode, 
    panel: React.ReactNode 
  },
  search: {
    onChange: (keyword: string) => void,
  },
};

const Shell: React.FC<ShellProps> = ({views, tabs, search, badges}) => {
  
  const classes = ShellStyles();
  const theme = useTheme();
    
  // Drawer
  const [drawerOpen, setDrawerOpen] = React.useState(false);
  const handleDrawerOpen = () => setDrawerOpen(true);
  const handleDrawerClose = () => setDrawerOpen(false);

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
          <Typography noWrap component="h1" variant="h6" color="inherit" className={classes.title}>
            {tabs.items}
          </Typography>
          
          <div className={classes.search}>
            <div className={classes.searchIcon}><SearchIcon /></div>
            <InputBase placeholder="Search…"
              onChange={({target}) => search.onChange(target.value)}
              inputProps={{ 'aria-label': 'search' }}
              classes={{
                root: classes.inputRoot,
                input: classes.inputInput,
              }} />
          </div>
          <ShellBadges>{badges}</ShellBadges>
        </Toolbar>
      </AppBar>

      <Drawer variant="permanent" open={drawerOpen} 
        classes={{
          paper: clsx(classes.drawerPaper, !drawerOpen && classes.drawerPaperClose),
        }}>
        
        <div className={classes.toolbarIcon}>
          <IconButton onClick={handleDrawerClose}>
            {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
          </IconButton>
        </div>

        <div className={classes.views}>
          <ShellViews open={drawerOpen}>{views}</ShellViews>
        </div>
      </Drawer>

      <main className={classes.content}>
        <div className={classes.appBarSpacer} />
        <Container maxWidth="lg" className={classes.container}>
          <>{ tabs.panel }</>
        </Container>
      </main>
    </div>
  );
}

export default Shell;
