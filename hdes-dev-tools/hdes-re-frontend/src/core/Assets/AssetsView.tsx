import React from 'react';
import Grid from '@material-ui/core/Grid';

import { Resources } from '../Resources';



interface AssetsViewProps {
};

const AssetsView: React.FC<AssetsViewProps> = ({}) => {
  const { session } = React.useContext(Resources.Context);
  
  return (<div>ASSETS</div>);
}

export default AssetsView;
