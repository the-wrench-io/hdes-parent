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
    const result: string[] = []; 
    for(const line of box.lines) {
      result.push(...this.visitLine(line).content);
    }
    
    const x = this._center.x - this._size.width / 2;
    const y = this._center.y - this._size.height / 2;
    
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
    const content: string[] = [];
    let filled = 0;
    for(let index = 0; index < line.words.length; index++) {
      const word = line.words[index];
      const newWords  = this.visitWord(word, filled).content;
      if(newWords.length > 1) {
        filled = ;
      }
    }
    
    return {content}    
  }
  visitWord(word: Typography.Word, filled: number): Typography.VisitedElement {
    const content: string = '';
    return {content}    
  }
}

export default TextBoxVisitor;


/*



const test = (svg: Snap.Paper, init: TypographyInit): { x: number, y: number, lines: string[] } => {
  const x = init.center.x - init.size.width / 2;
  const y = init.center.y - init.size.height / 2;

  const content = init.txt.split("");
  const temp = svg.text(0, 0, content);
  temp.attr(init.attributes);
  svg.remove();


  // line width and height
  const { width, height } = temp.getBBox();



  ///  var letter_width = temp.getBBox().width / content.length;
      const { x, y } = test(svg, init);

      var words = init.txt.split(" ");
      var width_so_far = 0, current_line = 0, lines = [''];
      for (var i = 0; i < words.length; i++) {

        var l = words[i].length;
        if (width_so_far + (l * letter_width) > init.max_width) {
          lines.push('');
          current_line++;
          width_so_far = 0;
        }

        width_so_far += l * letter_width;
        lines[current_line] += words[i] + " ";
      }

      var t = this
        .text(x, y, lines)
        .attr(init.attributes);
      t.selectAll("tspan:nth-child(n+2)").attr({
        dy: "1.2em", x
      });

      return t;
    };


  const lines: string[] = [];

  return {
    x, y, lines
  };
}



*/