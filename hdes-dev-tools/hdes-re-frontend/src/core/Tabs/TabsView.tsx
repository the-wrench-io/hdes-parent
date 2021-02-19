import React from 'react';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';

import { Resources } from '../Resources';


interface TabsViewProps {};


const TabsView: React.FC<TabsViewProps> = ({}) => {
  const { session, setSession } = React.useContext(Resources.Context);
  
  const active = session.history.open;
  const tabs = session.tabs;

  const handleTabChange = (_event: React.ChangeEvent<{}>, newValue: number) => {
    setSession((session) => session.changeTab(newValue));
  };

  return (<Tabs value={active} onChange={handleTabChange}>
      { tabs.map((tab, index) => (<Tab key={index} value={index} label={tab.label} />)) }
    </Tabs>
  );
}

export default TabsView;
