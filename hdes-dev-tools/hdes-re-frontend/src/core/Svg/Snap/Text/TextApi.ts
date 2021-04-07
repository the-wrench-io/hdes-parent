declare namespace TextApi {

  interface Size {
    width: number, height: number;
  }
  
  type TestText = (text: string) => { dimensions: Size, avg: {width: number, height: number} };


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

  interface TextAreaBuilder {
    break(): TextAreaBuilder;
    lines(lines: readonly Line[]): TextAreaBuilder;
    append(word: Word): TextAreaBuilder;
    build(): string[];
  }

  interface TextVisitor {
    visitText(text: string): TextWrapper;
    visitLines(src: string): readonly Line[];
    visitWords(src: string): Word[];
  }
}


export type { TextApi };