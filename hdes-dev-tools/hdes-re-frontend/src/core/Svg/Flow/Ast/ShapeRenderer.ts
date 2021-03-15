import {HdesSnap} from '../../Snap'

import { Ast } from './Ast';
import RendererVisitorDefault from './RendererVisitor';


class ShapeRendererDefault implements Ast.ShapeRenderer {
  private _theme?: Ast.ShapeRendererTheme;
  private _snap?: HdesSnap;
  private _shapes?: Ast.ShapeView;
 
  theme(theme: Ast.ShapeRendererTheme) : Ast.ShapeRenderer {
    this._theme = theme;
    return this;  
  }
  snap(snap: HdesSnap): Ast.ShapeRenderer {
    this._snap = snap;
    return this;
  }
  shapes(shapes: Ast.ShapeView): Ast.ShapeRenderer {
    this._shapes = shapes;
    return this;
  }
  build(): void {
    if(!this._theme) {
      throw new Error("theme is not defined!");
    }
    if(!this._snap) {
      throw new Error("snap cord is not defined!");
    }
    if(!this._shapes) {
      throw new Error("shapes cord is not defined!");
    }    

    new RendererVisitorDefault(this._snap, this._theme).visitRoot(this._shapes);
  }
}


export default ShapeRendererDefault;