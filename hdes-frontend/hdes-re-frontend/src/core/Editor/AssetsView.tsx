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
        coords={{
      //    x: 300, y: 40
           x: 0, y: 20
        }}
        node={{
          min: { height: 40,  width: 100}, 
          max: { height: 100, width: 200}
        }}
        listeners={{ onClick: (node, event) => setConfig({ mode: 'code-svg' })} }
      
      /*
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
        .build())}
        
        /**/
        grid={(builder) => (builder
          .header("c-1",  {kind: "IN",   order: 0, typography: {text: "level",   desc: "describes types and other things"} })
          .header("c-2",  {kind: "IN",   order: 1, typography: {text: "risk",    desc: "describes types and other things"} })
          .header("c-3",  {kind: "OUT",  order: 2, typography: {text: "result",  desc: "describes types and other things"} })
          
          .row("r-1",     {order: 0, typography: { text: "first row", desc: "describe summary" }})
          .row("r-2",     {order: 1, typography: { text: "first row", desc: "describe summary" }})
          .row("r-3",     {order: 2, typography: { text: "first row", desc: "describe summary" }})
          
          .cell("l-1",    {headerId: "c-1", rowId: "r-1", typography: {text: "rule x", desc: "rule desc"}})
          .cell("l-2",    {headerId: "c-2", rowId: "r-1", typography: {text: "rule y", desc: "rule desc"}})
          .cell("l-3",    {headerId: "c-3", rowId: "r-1", typography: {text: "rule z", desc: "rule desc"}})
      
          .cell("l-4",    {headerId: "c-1", rowId: "r-2", typography: {text: "rule x", desc: "rule desc"}})
          .cell("l-5",    {headerId: "c-2", rowId: "r-2", typography: {text: "rule y", desc: "rule desc"}})
          .cell("l-6",    {headerId: "c-3", rowId: "r-2", typography: {text: "rule z", desc: "rule desc"}})
      
          .cell("l-7",    {headerId: "c-1", rowId: "r-3", typography: {text: "rule x", desc: "rule desc"}})
          .cell("l-8",    {headerId: "c-2", rowId: "r-3", typography: {text: "rule y", desc: "rule desc"}})
          .cell("l-9",    {headerId: "c-3", rowId: "r-3", typography: {text: "rule z", desc: "rule desc"}})
          .build()
        )}
        
      />
      
      
      
      
      /*
    <FlowSvg listeners={{
      mousedown: (node, event) => setConfig({ mode: 'code-svg' })
    }} />
    */}
  </Slider>);
}

export {AssetsView};
