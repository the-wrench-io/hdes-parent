import { Typography } from './Ast'

class TextAreaBuilderDefault implements Typography.TextAreaBuilder {
  private _bounds: Typography.Size;
  private _usedWidth = 0;
  private _area: string[] = [];
  private _runningValue: string = '';
  
  constructor(bounds: Typography.Size) {
    this._bounds = bounds;
  }
  
  break(): Typography.TextAreaBuilder {
    this._area.push(this._runningValue);
    this._usedWidth = 0;
    this._runningValue = '';
    return this;
  }

  lines(lines: readonly Typography.Line[]): Typography.TextAreaBuilder {
    for(const line of lines) {
      for(const word of line.words) {
        this.append(word);
      }
      this.break();
    }
    return this;
  }
  
  append(word: Typography.Word): Typography.TextAreaBuilder {
    if(this._usedWidth + word.size.width > this._bounds.width) {
      this.break();
    }
    this._runningValue += word.value;
    this._usedWidth += word.size.width;
    return this;
  }
  build(): string[] {
    return this._area;
  }
}



export default TextAreaBuilderDefault;
