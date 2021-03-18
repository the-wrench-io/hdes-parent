import React from 'react';
import { makeStyles } from '@material-ui/core/styles';

import { Resources } from '../Resources';
import { CodeBlock } from '../CodeBlock';
import { FlowSvg } from '../Svg'
import { Slider } from '../Views'


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
  const { active } = Resources.useWorkspace();
  
  const [config, setConfig] = React.useState<{ mode: 'code-svg' | 'svg' | 'code' }>({ mode: 'svg' });
  const classes = useStyles();

  let showCode;
  let moveImage;
  if (config.mode === 'svg') {

  } else if (config.mode === 'code-svg') {
    showCode = classes.showCode;
  } else if (config.mode === 'code') {

  }

  return (<Slider open={config.mode === 'code-svg'}>
    <CodeBlock doc={active.blob.src} />
    <FlowSvg listeners={{
      mousedown: (node, event) => setConfig({ mode: 'code-svg' })
    }} />
  </Slider>);
}

export default AssetsView;
