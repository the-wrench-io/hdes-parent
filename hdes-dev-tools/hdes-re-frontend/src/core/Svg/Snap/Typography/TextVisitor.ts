import { Typography } from './Ast';

class ImmutableSize implements Typography.Size {
  private _width: number;
  private _height: number;
  constructor(height: number, width: number) {
    this._height = height;
    this._width = width;
  }
  get width() {
    return this._width;
  }
  get height() {
    return this._height;
  }
}

class ImmutableWord implements Typography.Word {
  private _value: string;
  private _size: Typography.Size;
  private _breaking: boolean;

  constructor(value: string, size: Typography.Size, breaking: boolean) {
    this._value = value;
    this._size = size;
    this._breaking = breaking;
  }
  get value() {
    return this._value;
  }
  get size() {
    return this._size;
  }
  get breaking() {
    return this._breaking;
  }
}

class ImmutableLine implements Typography.Line {
  private _words: readonly Typography.Word[];
  private _size: Typography.Size;

  constructor(words: readonly Typography.Word[], size: Typography.Size) {
    this._words = words;
    this._size = size;
  }
  get words() {
    return this._words;
  }
  get size() {
    return this._size;
  }
}

class ImmutableTextWrapper implements Typography.TextWrapper {
  private _lines: readonly Typography.Line[];
  private _max: Typography.Size;
  
  constructor(lines: readonly Typography.Line[], max: Typography.Size) {
    this._lines = lines;
    this._max = max;
  }
  get lines() {
    return this._lines;
  }
  get max() {
    return this._max;
  }
}


class TextVisitorDefault implements Typography.TextVisitor {
  
  private _tester: Typography.TestText;
  private _space: number;
  private _bounds: { width: number, height: number};
  
  constructor(tester: Typography.TestText, bounds: Typography.Size) {
    this._tester = tester;
    this._space = tester(" ").dimensions.width;
    this._bounds = bounds;
  }
  
  visitText(text: string): Typography.TextWrapper {
    let width = 0;
    let height = 0;
    const lines: Typography.Line[] = [];
    
    const src = text.replaceAll("  ", " ").split(/\r?\n/);
    for (const segment of src) {
      for(const line of this.visitLines(segment)) {
        if(line.size.height > height) {
          height = line.size.height;
        }
        width += line.size.width;
        lines.push(line)
      }   
    }
    const max: Typography.Size = new ImmutableSize(height, width)
    return new ImmutableTextWrapper(lines, max);
  }
  
  visitLines(src: string): readonly Typography.Line[] {
    const result: Typography.Line[] = [];
    for (const line of src.split(" ")) {
      let height = 0;
      let width = 0;
      const words: Typography.Word[] = this.visitWords(line);  
      
      for(const word of words) {
        width += word.size.width;
        if (height < word.size.height) {
          height = word.size.height;
        }
      }
      
      result.push(new ImmutableLine(words, new ImmutableSize(height, width)));
    }
    return result;
  } 
  visitWords(src: string): Typography.Word[] {
    const result: Typography.Word[] = [];
    const words = src.split("-");
    let index = -1;
    for(const word of words) {
      index++;
      
      const test = this._tester(word);
      // break the word
      if(test.dimensions.width > this._bounds.width) {
        const breakAt = Math.min(Math.floor(this._bounds.width/test.avg), word.length-2, 0);
        if(breakAt === 0) {
          // unreasnoble bounds
          continue;
        }
        const part1 = word.substring(0, breakAt);
        const part2 = word.substring(breakAt+1, word.length);
        result.push(new ImmutableWord(part1, this._tester(word).dimensions, true));
        result.push(...this.visitWords(part2));
      } else {
        const isLast = index === words.length-1;
        const breaking = words.length > 1 && !isLast;
        const suffix = breaking ? '-' : ' ';
        const part = word+suffix;
        result.push(new ImmutableWord(part, this._tester(part).dimensions, breaking));
      }
    }
    return result;
  }
}

export default TextVisitorDefault;
