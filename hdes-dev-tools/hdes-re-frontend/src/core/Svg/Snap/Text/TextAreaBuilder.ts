import { TextApi } from './TextApi'

class TextAreaBuilderDefault implements TextApi.TextAreaBuilder {
  private _bounds: TextApi.Size;
  private _usedWidth = 0;
  private _area: string[] = [];
  private _runningValue: string = '';
  
  constructor(bounds: TextApi.Size) {
    this._bounds = bounds;
  }
  
  break(): TextApi.TextAreaBuilder {
    this._area.push(this._runningValue);
    this._usedWidth = 0;
    this._runningValue = '';
    return this;
  }

  lines(lines: readonly TextApi.Line[]): TextApi.TextAreaBuilder {
    for(const line of lines) {
      for(const word of line.words) {
        this.append(word);
      }
      this.break();
    }
    return this;
  }
  
  append(word: TextApi.Word): TextApi.TextAreaBuilder {
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
