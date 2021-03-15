import Snap from 'snapsvg-cjs-ts';

import { Typography } from './Ast'
import ImmutableBox from './Immutables';
import TextBoxVisitor from './Visitor';


const render = (snap: Snap.Paper, props: {
    text: string, 
    size: Typography.Size, 
    center: Typography.Pos, 
    tester: Typography.TestText,
    attr: {}}) => {
      
  const box = new ImmutableBox(props.text, props.size, props.tester);
  return new TextBoxVisitor(snap, props.center, props.size, props.attr, props.tester(" ").width).visitBox(box);
}

export { render };