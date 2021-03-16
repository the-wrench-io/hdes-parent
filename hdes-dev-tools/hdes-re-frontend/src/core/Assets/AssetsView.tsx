import React from 'react';
import clsx from 'clsx';
import { makeStyles, Theme } from '@material-ui/core/styles';
import Grid, { GridSpacing } from '@material-ui/core/Grid';

import { Resources } from '../Resources';
import { CodeBlock } from '../CodeBlock';
import { FlowSvg } from '../Svg'


const useStyles = makeStyles((theme) => ({
  showCode: {
    animation: "1s ease-out 0s 1 $slideInFromLeft"
  },
  "@keyframes slideInFromLeft": {
    "0%": {
      transform: 'translateX(-100%)'
    },
    "100%": {
      transform: 'translateX(0)'
    }
  }

}));


interface AssetsViewProps {
};

const AssetsView: React.FC<AssetsViewProps> = () => {
  const { session } = React.useContext(Resources.Context);
  const workspace = session.workspace;
  const [config, setConfig] = React.useState<{ mode: 'code-svg' | 'svg' | 'code' }>({ mode: 'svg' });
  const classes = useStyles();

  if (!workspace) {
    throw new Error("Can't dispaly asset because workspace is not defined!");
  }

  console.log("new config", config);

  const open = session.history.open;
  const tabData = session.tabs[open];
  const blob = Object.values(workspace.snapshot.blobs).filter(b => b.id === tabData.id)[0];

  let showCode;
  let moveImage;
  if (config.mode === 'svg') {
    
  } else if (config.mode === 'code-svg') {
    showCode = classes.showCode;
  } else if (config.mode === 'code') {

  }

  return (<div>
    <Grid container spacing={0}>
      {config.mode === 'svg' ? null :
        (<Grid item xs={6} className={showCode}>
          <CodeBlock doc={blob.src} />
        </Grid>)
      }
      <Grid item xs={config.mode === 'svg' ? 12 : 6}>
        <FlowSvg listeners={{
          mousedown: (node, event) => {
            setConfig({ mode: 'code-svg' })
          }
        }} />
      </Grid>
    </Grid>
  </div>);
}

export default AssetsView;
