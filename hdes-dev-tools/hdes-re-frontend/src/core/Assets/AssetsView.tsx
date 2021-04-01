import React from 'react';
import { makeStyles } from '@material-ui/core/styles';

import { Resources } from '../Resources';
import { CodeBlock } from '../CodeBlock';
import { SvgTree } from '../Svg'
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
    
    { 
      <SvgTree 
        listeners={{ onClick: (node, event) => setConfig({ mode: 'code-svg' })}}
        graph={(builder) => (builder
          .start("start", { next: "decide-claim" })
          .switch("decide-claim",             { next: [ { id: "collision-claim" }, { id: "vandalism-claim" }, { id: "felloffroad-claim" }]})
          .decision("collision-claim",        { next: "calculate-collision" })
          .decision("calculate-collision",    { next: "final-calculation" })
          .decision("vandalism-claim",        { next: "calculate-vandalism" })
          .decision("calculate-vandalism",    { next: "final-calculation" })
          .decision("felloffroad-claim",      { next: "calculate-felloffroad" })
          .decision("calculate-felloffroad",  { next: "final-calculation" })
          .service("final-calculation",       { next: "end-claim" })
          .end("end-claim", {})
        .build({x: 300, y: 40 }))}
      />
      
      /*
    <FlowSvg listeners={{
      mousedown: (node, event) => setConfig({ mode: 'code-svg' })
    }} />
    */}
  </Slider>);
}

export default AssetsView;
