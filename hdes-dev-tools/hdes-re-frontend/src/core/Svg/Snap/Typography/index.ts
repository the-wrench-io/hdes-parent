import Snap from 'snapsvg-cjs-ts';

import { Typography } from './Ast'
import TextVisitorDefault from './TextVisitor';
import TextAreaBuilderDefault from './TextAreaBuilder';


const render = (
  snap: Snap.Paper,
  props: {
    text: string,
    size: Typography.Size,
    center: Typography.Pos,
    tester: Typography.TestText,
    attr: {}
  }): Snap.Element => {

  const box = new TextVisitorDefault(props.tester, props.size).visitText(props.text);
  const textArea = new TextAreaBuilderDefault(props.size).lines(box.lines).build();

  const x = props.center.x// - props.size.width / 2;
  const y = props.center.y// - props.size.height / 2;
  const t = snap.text(x, y, textArea).attr(props.attr);
  
  t.selectAll("tspan:nth-child(n+2)").attr({ x, dy: "1.2em"});

  return t;
}




export { render };