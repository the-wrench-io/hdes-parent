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
    breaking: boolean;
  }

  interface Line {
    words: readonly Word[];
    size: Size;
  }

  interface TextWrapper {
    lines: readonly Line[];
    max: Size;
  }

  type TestText = (text: string) => { dimensions: Size, avg: number };

  interface TextAreaBuilder {
    break(): TextAreaBuilder;
    lines(lines: readonly Typography.Line[]): TextAreaBuilder;
    append(word: Typography.Word): TextAreaBuilder;
    build(): string[];
  }

  interface TextVisitor {
    visitText(text: string): Typography.TextWrapper;
    visitLines(src: string): readonly Typography.Line[];
    visitWords(src: string): Typography.Word[];
  }
}


export type { Typography };