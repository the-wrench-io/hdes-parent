package io.resys.hdes.client.spi;

import io.resys.hdes.client.api.HdesClient.EnvirBuilder;
import io.resys.hdes.client.api.HdesClient.EnvirCommandFormatBuilder;
import io.resys.hdes.client.api.HdesClient.HdesTypesMapper;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.ImmutableAstSource;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.spi.envir.ProgramEnvirFactory;
import io.resys.hdes.client.spi.staticresources.Sha2;
import io.resys.hdes.client.spi.util.HdesAssert;

public class HdesClientEnvirBuilder implements EnvirBuilder {
  private final ProgramEnvirFactory factory;
  private final HdesTypesMapper defs;
  private ProgramEnvir envir;
  
  public HdesClientEnvirBuilder(ProgramEnvirFactory factory, HdesTypesMapper defs) {
    super();
    this.factory = factory;
    this.defs = defs;
  }
  @Override
  public EnvirCommandFormatBuilder addCommand() {
    final EnvirBuilder enviBuilder = this;;
    return new EnvirCommandFormatBuilder() {
      private String id;
      private AstBodyType type;
      private String commandJson;
      private StoreEntity entity;
      
      @Override
      public EnvirCommandFormatBuilder id(String externalId) {
        this.id = externalId;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder service(String commandJson) {
        this.type = AstBodyType.FLOW_TASK;
        this.commandJson = commandJson;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder flow(String commandJson) {
        this.type = AstBodyType.FLOW;
        this.commandJson = commandJson;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder decision(String commandJson) {
        this.type = AstBodyType.DT;
        this.commandJson = commandJson;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder service(StoreEntity entity) {
        this.type = AstBodyType.FLOW_TASK;
        this.entity = entity;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder flow(StoreEntity entity) {
        this.type = AstBodyType.FLOW;
        this.entity = entity;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder decision(StoreEntity entity) {
        this.type = AstBodyType.DT;
        this.entity = entity;
        return this;
      }
      @Override
      public EnvirBuilder build() {
        HdesAssert.notNull(id, () -> "id must be defined!");
        HdesAssert.isTrue(commandJson != null || entity != null, () -> "commandJson or entity must be defined!");
        HdesAssert.isTrue(commandJson == null || entity == null, () -> "commandJson and entity can't be both defined!");
        
        factory.add(ImmutableAstSource.builder()
            .id(id)
            .bodyType(type)
            .hash(entity == null ? Sha2.blob(commandJson) : entity.getHash())
            .commands(entity == null ? defs.commandsList(commandJson) : entity.getBody())
            .build());
        
        return enviBuilder;
      }
    };
  }
  @Override
  public EnvirBuilder from(ProgramEnvir envir) {
    this.envir = envir;
    return this;
  }
  @Override
  public ProgramEnvir build() {
    return factory.add(envir).build();
  }

}
