import React from 'react';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import CloseIcon from '@material-ui/icons/Close';
import IconButton from '@material-ui/core/IconButton';
import { makeStyles } from '@material-ui/core/styles';


import { Resources } from '../Resources';



const useStyles = makeStyles((theme) => ({
  tab: {
    flexDirection: 'row-reverse',
  },
  tabLabel: {
    minHeight: 'unset',
  },
  close: {
    marginBottom: 'unset !important',
    padding: '0px'
  },
  closeSpacing: {
    flexGrow: 1
  },
  closeIcon: {
    "&:hover": {
      color: theme.palette.secondary.main
    }
  }
}));


interface TabsViewProps {};


const TabsView: React.FC<TabsViewProps> = ({}) => {
  const classes = useStyles();

  const { session, setSession } = React.useContext(Resources.Context);
  
  const active = session.history.open;
  const tabs = session.tabs;
  const activeTab = session.tabs[active];
  

  const handleTabChange = (_event: React.ChangeEvent<{}>, newValue: number) => {
    setSession((session) => session.changeTab(newValue));
  };

  const handleTabClose = (_event: React.ChangeEvent<{}>, newValue: number) => {
    setSession((session) => session.removeTab(activeTab.id));
  };
    
  return (<Tabs value={active} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
      { tabs.map((tab, index) => (<Tab key={index} value={index} 
        classes={{wrapper: classes.tab, labelIcon: classes.tabLabel}}
        icon={(<>
          <IconButton className={classes.close} onClick={(e) => handleTabClose(e, index)} >
            <CloseIcon color="disabled" className={classes.closeIcon}/>
          </IconButton>
          <span className={classes.closeSpacing}></span></>)} 
        label={tab.label} />)) }
    </Tabs>
  );
}

export default TabsView;