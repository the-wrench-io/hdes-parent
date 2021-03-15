import Snap from 'snapsvg-cjs-ts';
import { Typography } from './Ast'


class TextBoxVisitor implements Typography.Visitor {
  private _snap: Snap.Paper;
  private _center: Typography.Pos;
  private _size: Typography.Size;
  private _attr: {};
  private _space: number;
  
  constructor(snap: Snap.Paper, center: Typography.Pos, size: Typography.Size, attr: {}, space: number) {
    this._snap = snap;
    this._center = center;
    this._size = size;
    this._attr = attr;
    this._space = space;
  }
  
  visitBox(box: Typography.Box): Snap.Element {
        console.log(box);
    
    const result: string[] = []; 
    for(const line of box.lines) {
      result.push(...this.visitLine(line).values.map(c => c.content));
    }

    const x = this._center.x - this._size.width / 2;
    const y = this._center.y - box.size.height/2;
    const t = this._snap
      .text(x, y, result)
      .attr(this._attr);
      
    t.selectAll("tspan:nth-child(n+2)")
      .attr({
        dy: "1.2em", 
        x
    });

    return t;
  }
  visitLine(line: Typography.Line): Typography.VisitedElement {
    const result: {content: string, width: number}[] = [];
    let used = 0;
    let index = -1;
    let value: string = '';

    
    for(const word of line.words) {
      const avg = word.size.width / word.value.length; 
      const isLast = ++index === line.words.length;
      const wordWidth = avg*word.value.length;
      
      // split the word
      if(wordWidth > this._size.width) {
        
        
        
        continue; 
      }
      

      // split
      const willBeUsed = used + wordWidth + this._space;
      if(willBeUsed > this._size.width) {
        result.push({content: value, width: used});
        
        used = wordWidth + this._space;
        value = word.value;
        
      } else {
        used = willBeUsed;
        value += ' ' + word.value;
      }
    }
    
    result.push({content: value, width: used});
    
    return { values: result }
        
  }
  visitWord(word: Typography.Word, filled: number): Typography.VisitedElement {
    const content: string = '';
    return { values: [] }    
  }
}

export default TextBoxVisitor;
