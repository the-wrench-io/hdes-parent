import React from 'react';
import { Resources } from '../Resources';


interface TabPanelRendererProps {
  plugins: {
    id?: string;
    view: React.ReactChild;
  }[]
};


const TabPanelRenderer: React.FC<TabPanelRendererProps> = ({plugins}) => {
  const { session } = React.useContext(Resources.Context);
  const tabs = session.tabs;

  if(tabs.length === 0) {
    return null;
  }

  const active = tabs[session.history.open];
  const plugin = plugins.filter(p => p.id === active.id);
  
  if(plugin.length > 0) {
    return <>{plugin[0].view}</>;
  }
  return (<>{plugin.filter(p => !p.id)[0].view}</>);
}

export default TabPanelRenderer;
