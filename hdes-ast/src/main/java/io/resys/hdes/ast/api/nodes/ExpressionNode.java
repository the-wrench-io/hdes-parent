package io.resys.hdes.ast.api.nodes;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;


public interface ExpressionNode extends AstNode {
  
  enum AdditiveType { ADD, SUBSTRACT }
  enum MultiplicativeType { DIVIDE, MULTIPLY }
  enum EqualityType { NOTEQUAL, EQUAL, LESS, LESS_THEN, GREATER, GREATER_THEN }
  
  interface RefNode extends ExpressionNode {
    String getName();
  }

  interface DataTypeConversion extends ExpressionNode {
    ExpressionNode getValue();
    NodeDataType getToType();
  }
  
  interface UnaryOperation extends ExpressionNode {
    ExpressionNode getValue();
  }

  @Value.Immutable
  interface Literal extends ExpressionNode {
    NodeDataType getType();
    String getValue();
  }
  
  @Value.Immutable
  interface DateConversion extends DataTypeConversion {}

  @Value.Immutable
  interface DateTimeConversion extends DataTypeConversion {}

  @Value.Immutable
  interface TimeConversion extends DataTypeConversion {}

  @Value.Immutable
  interface DecimalConversion extends DataTypeConversion {}
  
  /*
   * Unary operation
   */
  @Value.Immutable
  interface NotUnaryOperation extends UnaryOperation {
  }
  
  @Value.Immutable
  interface NegateUnaryOperation extends UnaryOperation {
  }
  
  /*
   * Ref nodes
   */
  @Value.Immutable
  interface MethodRefNode extends DataTypeConversion {
    @Nullable
    TypeRefNode getType();
    List<ExpressionNode> getValues();
  }
  
  @Value.Immutable
  interface TypeRefNode extends DataTypeConversion {
    
  }
  
  /*
   * Conditions and expressions 
   */
  @Value.Immutable
  interface EqualityOperation extends ExpressionNode {
    EqualityType getType();
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }
  
  @Value.Immutable
  interface ConditionalExpression extends ExpressionNode {
    EqualityOperation getOperation();
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }
  
  @Value.Immutable
  interface AndOperation extends ExpressionNode {
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }
  
  @Value.Immutable
  interface OrOperation extends ExpressionNode {
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }

  @Value.Immutable
  interface AdditiveOperation extends ExpressionNode {
    AdditiveType getType();
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }
  
  @Value.Immutable
  interface MultiplicativeOperation extends ExpressionNode {
    MultiplicativeType getType();
    ExpressionNode getLeft();
    ExpressionNode getRight();
  }
}
