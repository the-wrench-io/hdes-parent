import Ast from './Ast'


class AstMapper<T> {
  private node: Ast.AstNode;
  private mappers: Record<string, (node: Ast.AstNode) => T>
  private anyMapper?: (node: Ast.AstNode) => T;
  
  constructor(node: Ast.AstNode) {
    this.node = node;
    this.mappers = {};
  }
  
  map(nodeType: Ast.NodeType, mapping: (node: Ast.AstNode) => T): AstMapper<T> {
    if(this.mappers[nodeType]) {
      throw Error("Can't redefined nodeType: " + nodeType + " in AstMapper!");
    }
    this.mappers[nodeType] = mapping;
    return this;
  }
  any(mapping: (node: Ast.AstNode) => T): AstMapper<T>  {
    this.anyMapper = mapping;
    return this;    
  }
  build(): T {
    const mapper = this.mappers[this.node.nodeType];
    if(mapper) {
      return mapper(this.node);
    }
    if(this.anyMapper) {
      return this.anyMapper(this.node); 
    }
    throw Error("Can't map node with type: " + this.node.nodeType + " in AstMapper!");
  }
}


export default AstMapper;