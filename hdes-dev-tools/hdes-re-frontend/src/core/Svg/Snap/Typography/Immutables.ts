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

  constructor(value: string, size: Typography.Size) {
    this._value = value;
    this._size = size;
  }
  get value() {
    return this._value;
  }
  get size() {
    return this._size;
  }
}

class ImmutableLine implements Typography.Line {
  private _words: readonly Typography.Word[];
  private _size: Typography.Size;

  constructor(line: string, tester: Typography.TestText, space: number) {
    const wordsSplitAtSpace = line.split(" ");
    
    let height = 0;
    let width = wordsSplitAtSpace.length > 1 ? -space : 0;
    const tested: Typography.Word[] = [];
    for (const wordAtSpace of wordsSplitAtSpace) {
      const wordsAtDash = wordAtSpace.split("-");
      let dashIndex = 0;
      for(const word of wordsAtDash) {
        const isLast = ++dashIndex === wordsAtDash.length;
        const dash = wordsAtDash.length > 1 && !isLast ? '-' : '';
        
        const test = tester(word);
        width += test.width + space;
        if (height < test.height) {
          height = test.height;
        }
        console.log(word+dash)
        tested.push(new ImmutableWord(word+dash, new ImmutableSize(test.height, test.width)));
      }
    }
    this._words = tested;
    this._size = new ImmutableSize(height, width);
  }
  get words() {
    return this._words;
  }
  get size() {
    return this._size;
  }
}

class ImmutableBox implements Typography.Box {
  private _lines: readonly Typography.Line[];
  private _size: Typography.Size;
  
  constructor(text: string, size: Typography.Size, tester: Typography.TestText) {
    const space = tester(" ").width;
    const parsedLines: Typography.Line[] = [];
    let width = 0;
    let height = 0;
    for (const line of text.replaceAll("  ", " ").split(/\r?\n/)) {
      const parsedLine = new ImmutableLine(line, tester, space);
      parsedLines.push(parsedLine);
      if(parsedLine.size.height > height) {
        height = parsedLine.size.height;
      }
      width += parsedLine.size.width;
      
    }
    this._lines = parsedLines;
    this._size = new ImmutableSize(height, width + parsedLines.length * space);
  }
  get lines() {
    return this._lines;
  }
  get size() {
    return this._size;
  }
}


export default ImmutableBox;
