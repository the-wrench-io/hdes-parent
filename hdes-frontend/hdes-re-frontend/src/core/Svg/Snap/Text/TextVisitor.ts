import { TextApi } from './TextApi';

class ImmutableSize implements TextApi.Size {
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

class ImmutableWord implements TextApi.Word {
  private _value: string;
  private _size: TextApi.Size;
  private _breaking: boolean;

  constructor(value: string, size: TextApi.Size, breaking: boolean) {
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

class ImmutableLine implements TextApi.Line {
  private _words: readonly TextApi.Word[];
  private _size: TextApi.Size;

  constructor(words: readonly TextApi.Word[], size: TextApi.Size) {
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

class ImmutableTextWrapper implements TextApi.TextWrapper {
  private _lines: readonly TextApi.Line[];
  private _max: TextApi.Size;
  
  constructor(lines: readonly TextApi.Line[], max: TextApi.Size) {
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


class TextVisitorDefault implements TextApi.TextVisitor {
  
  private _tester: TextApi.TestText;
  private _space: number;
  private _bounds: { width: number, height: number};
  
  constructor(tester: TextApi.TestText, bounds: TextApi.Size) {
    this._tester = tester;
    this._space = tester(" ").dimensions.width;
    this._bounds = bounds;
  }
  
  visitText(text: string): TextApi.TextWrapper {
    let width = 0;
    let height = 0;
    const lines: TextApi.Line[] = [];
    
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
    const max: TextApi.Size = new ImmutableSize(height, width)
    return new ImmutableTextWrapper(lines, max);
  }
  
  visitLines(src: string): readonly TextApi.Line[] {
    const result: TextApi.Line[] = [];
    for (const line of src.split(" ")) {
      let height = 0;
      let width = 0;
      const words: TextApi.Word[] = this.visitWords(line);  
      
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
  visitWords(src: string): TextApi.Word[] {
    const result: TextApi.Word[] = [];
    const words = src.split("-");
    let index = -1;
    for(const word of words) {
      index++;
      
      const test = this._tester(word);
      // break the word
      if(test.dimensions.width > this._bounds.width) {
        const breakAt = Math.min(Math.floor(this._bounds.width/test.avg.width), word.length-2, 0);
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
