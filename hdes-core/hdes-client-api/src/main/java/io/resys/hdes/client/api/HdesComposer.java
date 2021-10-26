package io.resys.hdes.client.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramAssociation;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramMessage;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.smallrye.mutiny.Uni;

/**
 * Backend for composer related service. 
 * Provides mutability of the assets.
 */
public interface HdesComposer {
  
  Uni<ComposerState> get();
  Uni<ComposerState> update(UpdateEntity asset);
  Uni<ComposerState> create(CreateEntity asset);
  Uni<ComposerState> delete(String id);
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableComposerState.class)
  @JsonDeserialize(as = ImmutableComposerState.class)
  interface ComposerState {
    Map<String, ComposerEntity<AstFlow>> getFlows();
    Map<String, ComposerEntity<AstService>> getServices();
    Map<String, ComposerEntity<AstDecision>> getDecisionss();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableComposerEntity.class)
  @JsonDeserialize(as = ImmutableComposerEntity.class)
  interface ComposerEntity<A extends AstBody> {
    String getId();
    @Nullable
    A getAst();
    
    List<ProgramMessage> getWarnings();
    List<ProgramMessage> getErrors();
    List<ProgramAssociation> getAssociations();
    ProgramStatus getStatus();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableUpdateEntity.class)
  @JsonDeserialize(as = ImmutableUpdateEntity.class)
  interface UpdateEntity {
    String getId();
    String getBody();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateEntity.class)
  @JsonDeserialize(as = ImmutableCreateEntity.class)
  interface CreateEntity {
    String getName();
    String getType();
  }

}
