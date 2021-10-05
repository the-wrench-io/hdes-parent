package io.resys.hdes.client.spi;

import org.codehaus.groovy.control.CompilerConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import groovy.lang.GroovyClassLoader;
import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.spi.decision.DecisionAstBuilderImpl;
import io.resys.hdes.client.spi.flow.FlowAstBuilderImpl;
import io.resys.hdes.client.spi.groovy.ServiceAstBuilderImpl2;
import io.resys.hdes.client.spi.groovy.ServiceExecutorCompilationCustomizer;

public class HdesAstTypesImpl implements HdesAstTypes {

  private final ObjectMapper objectMapper;
  private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
  private final GroovyClassLoader gcl;
  private final HdesDataTypeFactory dataType;
  
  public HdesAstTypesImpl(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
    
    CompilerConfiguration config = new CompilerConfiguration();
    config.setTargetBytecode(CompilerConfiguration.JDK8);
    config.addCompilationCustomizers(new ServiceExecutorCompilationCustomizer());
    
    this.gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    this.dataType = new HdesDataTypeFactory(objectMapper);
  }

  @Override
  public DecisionAstBuilder decision() {
    return new DecisionAstBuilderImpl(objectMapper);
  }
  @Override
  public FlowAstBuilder flow() {
    return new FlowAstBuilderImpl(yaml);
  }
  @Override
  public ServiceAstBuilder service() {
    return new ServiceAstBuilderImpl2(dataType, gcl);
  }
  @Override
  public DataTypeAstBuilder dataType() {
    return dataType.create();
  }
}
