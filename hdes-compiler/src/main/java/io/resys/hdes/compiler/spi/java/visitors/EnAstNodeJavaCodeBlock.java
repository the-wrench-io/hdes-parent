package io.resys.hdes.compiler.spi.java.visitors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.squareup.javapoet.CodeBlock;

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.ExpressionAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AdditiveOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AndOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.BetweenExpression;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ConditionalExpression;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MethodRefNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MultiplicativeOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NegateUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.OrOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PositiveUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PostDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PostIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PreDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PreIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.TypeRefNode;
import io.resys.hdes.compiler.api.HdesCompilerException;

public class EnAstNodeJavaCodeBlock implements ExpressionAstNodeVisitor<CodeBlock, CodeBlock> {
  private final TypeNameRef naming;

  public EnAstNodeJavaCodeBlock(TypeNameRef naming) {
    super();
    this.naming = naming;
  }

  @Override
  public CodeBlock visitExpressionBody(ExpressionBody node) {
    return visit(node.getValue());
  }

  @Override
  public CodeBlock visitTypeName(TypeName node) {
    return CodeBlock.builder().add("$L", naming.get(node)).build();
  }

  @Override
  public CodeBlock visitLiteral(Literal node) {

    switch (node.getType()) {
    case BOOLEAN: return CodeBlock.builder().add("$L", node.getValue()).build();
    case STRING: return CodeBlock.builder().add("$S", node.getValue()).build();
    case DECIMAL: return CodeBlock.builder().add("new $T($S)", BigDecimal.class, node.getValue()).build();
    case INTEGER: return CodeBlock.builder().add("$L", node.getValue()).build();
    
    case DATE: return CodeBlock.builder().add("$T.parse($S)", LocalDate.class, node.getValue()).build();
    case DATE_TIME: return CodeBlock.builder().add("$T.parse($S)", LocalDateTime.class, node.getValue()).build();
    case TIME: return CodeBlock.builder().add("$T.parse($S)", LocalTime.class, node.getValue()).build();
    default: throw new HdesCompilerException(HdesCompilerException.builder().unknownExpressionNode(node));
    }
  }
  
  @Override
  public CodeBlock visitEqualityOperation(EqualityOperation node) {
    String operation;
    switch (node.getType()) {
    case EQUAL: operation = "$L.eq($L, $L)"; break;
    case NOTEQUAL: operation = "$L.neq($L, $L)"; break;
    case GREATER: operation = "$L.gt($L, $L)"; break;
    case GREATER_THEN: operation = "$L.gte($L, $L)"; break;
    case LESS: operation = "$L.lt($L, $L)"; break;
    case LESS_THEN: operation = "$L.lte($L, $L)"; break;
    // TODO: error handling
    default: throw new HdesCompilerException(HdesCompilerException.builder().unknownExpressionOperation(node));
    }
    return CodeBlock.builder()
        .add(operation, "when()", visit(node.getLeft()), visit(node.getRight()))
        .build();
  }

  @Override
  public CodeBlock visitAndOperation(AndOperation node) {
    return CodeBlock.builder().add("$L && $L", visit(node.getLeft()), visit(node.getRight())).build();
  }

  @Override
  public CodeBlock visitOrOperation(OrOperation node) {
    return CodeBlock.builder().add("$L || $L", visit(node.getLeft()), visit(node.getRight())).build();
  }

  @Override
  public CodeBlock visitConditionalExpression(ConditionalExpression node) {
    return CodeBlock.builder().add("$L ? $L : $L", visit(node.getOperation()), visit(node.getLeft()), visit(node.getRight())).build();
  }

  @Override
  public CodeBlock visitBetweenExpression(BetweenExpression node) {
    return CodeBlock.builder().add("when().between($L, $L, $L)", 
        visit(node.getValue()), 
        visit(node.getLeft()), 
        visit(node.getRight())).build();
  }

  @Override
  public CodeBlock visitAdditiveOperation(AdditiveOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitMultiplicativeOperation(MultiplicativeOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitNotUnaryOperation(NotUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitNegateUnaryOperation(NegateUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitPositiveUnaryOperation(PositiveUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitPreIncrementUnaryOperation(PreIncrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitPreDecrementUnaryOperation(PreDecrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitPostIncrementUnaryOperation(PostIncrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitPostDecrementUnaryOperation(PostDecrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitMethodRefNode(MethodRefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CodeBlock visitTypeRefNode(TypeRefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  private CodeBlock visit(AstNode node) {
    if (node instanceof ExpressionBody) {
      return visitExpressionBody((ExpressionBody) node);
    } else if (node instanceof TypeName) {
      return visitTypeName((TypeName) node);
    } else if (node instanceof Literal) {
      return visitLiteral((Literal) node);
    } else if (node instanceof NotUnaryOperation) {
      return visitNotUnaryOperation((NotUnaryOperation) node);
    } else if (node instanceof NegateUnaryOperation) {
      return visitNegateUnaryOperation((NegateUnaryOperation) node);
    } else if (node instanceof PositiveUnaryOperation) {
      return visitPositiveUnaryOperation((PositiveUnaryOperation) node);
    } else if (node instanceof PreIncrementUnaryOperation) {
      return visitPreIncrementUnaryOperation((PreIncrementUnaryOperation) node);
    } else if (node instanceof PreDecrementUnaryOperation) {
      return visitPreDecrementUnaryOperation((PreDecrementUnaryOperation) node);
    } else if (node instanceof PostIncrementUnaryOperation) {
      return visitPostIncrementUnaryOperation((PostIncrementUnaryOperation) node);
    } else if (node instanceof PostDecrementUnaryOperation) {
      return visitPostDecrementUnaryOperation((PostDecrementUnaryOperation) node);
    } else if (node instanceof MethodRefNode) {
      return visitMethodRefNode((MethodRefNode) node);
    } else if (node instanceof TypeRefNode) {
      return visitTypeRefNode((TypeRefNode) node);
    } else if (node instanceof EqualityOperation) {
      return visitEqualityOperation((EqualityOperation) node);
    } else if (node instanceof AndOperation) {
      return visitAndOperation((AndOperation) node);
    } else if (node instanceof OrOperation) {
      return visitOrOperation((OrOperation) node);
    } else if (node instanceof ConditionalExpression) {
      return visitConditionalExpression((ConditionalExpression) node);
    } else if (node instanceof BetweenExpression) {
      return visitBetweenExpression((BetweenExpression) node);
    } else if (node instanceof AdditiveOperation) {
      return visitAdditiveOperation((AdditiveOperation) node);
    } else if (node instanceof MultiplicativeOperation) {
      return visitMultiplicativeOperation((MultiplicativeOperation) node);
    }
    throw new HdesCompilerException(HdesCompilerException.builder().unknownExpressionNode(node));
  }
}
