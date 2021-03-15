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
    const words = line.split(" ");
    let height = 0;
    let width = words.length > 1 ? -space : 0;
    const tested: Typography.Word[] = [];
    for (const word of words) {
      const test = tester(word);
      width += test.width + space;
      if (height < test.height) {
        height = test.height;
        tested.push(new ImmutableWord(word, new ImmutableSize(test.height, test.width)));
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
    for (const line of text.replaceAll("  ", " ").split(/\r?\n/)) {
      parsedLines.push(new ImmutableLine(line, tester, space));
    }
    this._lines = parsedLines;
    this._size = size;
  }
  get lines() {
    return this._lines;
  }
  get size() {
    return this._size;
  }
}


export default ImmutableBox;
