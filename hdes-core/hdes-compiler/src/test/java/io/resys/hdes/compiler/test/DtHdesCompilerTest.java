package io.resys.hdes.compiler.test;

import static io.resys.hdes.compiler.test.TestUtil.compiler;
import static io.resys.hdes.compiler.test.TestUtil.file;
import static io.resys.hdes.compiler.test.TestUtil.log;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler.Resource;


public class DtHdesCompilerTest {
  @Test
  public void simpleHitPolicyMatrixDt() {
    final var file = file("SimpleHitPolicyMatrixDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyMatrixDt.hdes", file)
    .build();
    log(code, file);
  }
  
  
  @Test
  public void simpleHitPolicyAllDt() {
    final var file = file("SimpleHitPolicyAllDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyAllDt.hdes", file)
    .build();
    log(code, file);
  }
  
  @Test
  public void simpleHitPolicyFirstDt() {
    final var file = file("SimpleHitPolicyFirstDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyFirstDt.hdes", file)
    .build();
    log(code, file);
  }
  
  @Test
  public void formulaHitPolicyFirstDt() {
    final var file = file("FormulaHitPolicyFirstDt.hdes");
    List<Resource> code = compiler.parser()
        .add("FormulaHitPolicyFirstDt.hdes", file)
    .build();
    log(code, file);
  }

}
