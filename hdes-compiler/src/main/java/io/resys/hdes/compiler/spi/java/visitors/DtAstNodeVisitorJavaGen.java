package io.resys.hdes.compiler.spi.java.visitors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;

public class DtAstNodeVisitorJavaGen extends DtAstNodeVisitorTemplate<DtJavaSpec, TypeSpec> {

  private String dtName;
  
  
  @Override
  public TypeSpec visitDecisionTableBody(DecisionTableBody node) {
    dtName = node.getId();
    TypeSpec.Builder interfaceBuilder = TypeSpec.classBuilder(JavaNaming.dtImpl(node.getId()))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get("", node.getId()));

    return interfaceBuilder.build();
  }
  
  @Override
  public DtJavaSpec visitHitPolicyAll(HitPolicyAll node) {
    // TODO Auto-generated method stub
    return super.visitHitPolicyAll(node);
  }

  @Override
  public DtJavaSpec visitHitPolicyFirst(HitPolicyFirst node) {
    // TODO Auto-generated method stub
    return super.visitHitPolicyFirst(node);
  }
  
  @Override
  public DtJavaSpec visitHitPolicyMatrix(HitPolicyMatrix node) {
    // TODO Auto-generated method stub
    return super.visitHitPolicyMatrix(node);
  }
}
