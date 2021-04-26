import Snap from 'snapsvg-cjs-ts';
import { Tree } from '../Tree';
import { TypographyDimensions, TextDimensions } from './Tester';
import TextVisitorDefault from './TextVisitor';
import TextAreaBuilderDefault from './TextAreaBuilder';

interface TextInit {
  text: Tree.Typography,
  theme: Tree.Theme,
  attr: {},
  center: Tree.Coordinates,
  size: Tree.Dimensions;  
}

const render = (snap: Snap.Paper, props: TextInit): Snap.Element => {

  const attributes = Object.assign({
    fontSize: '0.9em',
    fontWeight: 100,
    fill: props.theme.stroke,
    stroke: props.theme.stroke,
    alignmentBaseline: "middle",
    textAnchor: "middle"
  }, props.attr)

  const tester = (text: string) => TextDimensions(snap, text, attributes);
  const text: string = props.text.text ? props.text.text : "";
  const box = new TextVisitorDefault(tester, props.size).visitText(text);
  
  const textArea = new TextAreaBuilderDefault(props.size).lines(box.lines).build();

  const x = props.center.x;
  const y = props.center.y + 3;

  const t = snap.text(x, y, textArea);
  t.attr(attributes);
  t.selectAll("tspan:nth-child(n+2)");
  return t;
}


export type { TextInit };
export { render, TypographyDimensions };