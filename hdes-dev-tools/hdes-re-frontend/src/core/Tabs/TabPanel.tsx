import React from 'react';
import Grid from '@material-ui/core/Grid';

import { Resources } from '../Resources';


interface PanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const Panel: React.FC<PanelProps> = ({ children, value, index, ...other }) => {
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

interface TabPanelProps {
};

const TabPanel: React.FC<TabPanelProps> = () => {
  const { session } = React.useContext(Resources.Context);
  const tabs = session.tabs;
  const active = session.history.open;
    
  const result = tabs.map((tab, index) => (
    <Panel key={index} index={index} value={active ? active : 0}>
    </Panel>)
  )
  return (<>{result}</>);
}

export default TabPanel;
