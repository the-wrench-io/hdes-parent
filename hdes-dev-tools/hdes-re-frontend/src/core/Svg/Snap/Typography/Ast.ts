import Snap from 'snapsvg-cjs-ts';

declare namespace Typography {
  
  interface Size {
    width: number, height: number;
  }
  
  interface Pos {
    x: number, y: number
  }
  
  interface Word {
    value: string;
    size: Size;
  }
  
  interface Line {
    words: readonly Word[];
    size: Size;
  }
  
  interface Box {
    lines: readonly Line[];
    size: Size;
  }
 
  type TestText = (text: string) => Size;

  interface VisitedElement {
    content: string[];
  }
  
  interface Visitor {
    visitBox(box: Box): Snap.Element;
    visitLine(line: Line): VisitedElement;
    visitWord(line: Word, filled: number): VisitedElement;
  }
}


export type { Typography };