package io.resys.hdes.client.spi.envir;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstDecision.HitPolicy;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStep;
import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStepRefType;
import io.resys.hdes.client.api.programs.ImmutableProgramAssociation;
import io.resys.hdes.client.api.programs.ImmutableProgramMessage;
import io.resys.hdes.client.api.programs.ImmutableProgramWrapper;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramAssociation;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramMessage;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.spi.envir.EnvirFactory.EvirBuilderSourceEntity;
import io.resys.hdes.client.spi.flow.validators.FlowAssociationValidator;
import io.resys.hdes.client.spi.util.HdesAssert;

public class AssociationVisitor {
  private final Map<String, EvirBuilderSourceEntity> externalIdToSource = new HashMap<>();
  private final Map<AstBodyType, List<String>> typeToExternalId = new HashMap<>(Map.of(
        AstBodyType.DT, new ArrayList<String>(),
        AstBodyType.FLOW_TASK, new ArrayList<String>(),
        AstBodyType.FLOW, new ArrayList<String>()
      ));
  private final Map<String, List<ProgramMessage>> externalIdToDependencyErrors = new HashMap<>();
  private final Map<String, List<ProgramMessage>> externalIdToDependencyWarnings = new HashMap<>();
  private final Map<String, ProgramWrapper<?, ?>> externalIdToWrapper = new HashMap<>();
  private final Map<String, List<ProgramAssociation>> externalIdToAssoc = new HashMap<>();

  public AssociationVisitor add(EvirBuilderSourceEntity source, ProgramWrapper<?, ?> wrapper) {
    final var externalId = source.getExternalId();
    HdesAssert.isTrue(!this.externalIdToSource.containsKey(externalId), () -> "commandJson with id: '" + externalId + "' is already defined!");
    externalIdToSource.put(source.getExternalId(), source);
    typeToExternalId.get(wrapper.getType()).add(wrapper.getId());
    externalIdToDependencyErrors.put(wrapper.getId(), new ArrayList<>());
    externalIdToDependencyWarnings.put(wrapper.getId(), new ArrayList<>());
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public Collection<ProgramWrapper<?, ?>> build() {

    typeToExternalId.get(AstBodyType.FLOW).stream()
      .map(id -> (ProgramWrapper<AstFlow, FlowProgram>) externalIdToWrapper.get(id))
      .forEach(this::visitFlowProgramAssociation);
    
    return externalIdToWrapper.values().stream()
        .map(this::visitWrapper)
        .collect(Collectors.toList());
  }
    
  private void visitFlowProgramAssociation(ProgramWrapper<AstFlow, FlowProgram> wrapper) {
    if(wrapper.getProgram().isEmpty()) {
      return;
    }
    
    if(externalIdToAssoc.containsKey(wrapper.getId())) {
      return;
    }
    externalIdToAssoc.put(wrapper.getId(), new ArrayList<ProgramAssociation>());
    
    final var validator = new FlowAssociationValidator(wrapper.getAst().get());
    final var program = wrapper.getProgram().get();
    program.getSteps().values().forEach(step -> visitFlowStep(wrapper, step, validator));
    
    for(final var entry : validator.build()) {
      if(entry.getMessages().isEmpty()) {
        continue;
      }
      
      final var step = entry.getStep();
      if(step != null) {
        final var ref = entry.getStep().getBody().getRef();
        
        entry.getMessages().forEach(msg -> {
          final var progMsg = ImmutableProgramMessage.builder().id("dependency-error").msg("line: " + msg.getLine() + ": " + msg.getValue()).build();          
          externalIdToDependencyErrors.get(wrapper.getId()).add(progMsg);
        });
        
        final var allAssoc = externalIdToAssoc.get(wrapper.getId());          
        final var assoc = allAssoc.stream().filter(a -> a.getRef().equals(ref)).findFirst();
        allAssoc.remove(assoc.get());
        allAssoc.add(ImmutableProgramAssociation.builder()
            .from(assoc.get())
            .refStatus(ProgramStatus.DEPENDENCY_ERROR)
            .build());
      } else {
        entry.getMessages().forEach(msg -> {
          final var progMsg = ImmutableProgramMessage.builder().id("dependency-warning").msg("line: " + msg.getLine() + ": " + msg.getValue()).build();          
          externalIdToDependencyWarnings.get(wrapper.getId()).add(progMsg);
        });
      }
      
      
    }
  }
  
  private void visitFlowStep(ProgramWrapper<AstFlow, FlowProgram> wrapper, FlowProgramStep step, FlowAssociationValidator validator) {
    if(step.getBody() == null) {
      return;
    } 
    final var result = externalIdToAssoc.get(wrapper.getId());
    final var refWrapper = visitRefWrapper(wrapper, step);
  
    if(refWrapper == null) {
      return;
    }
    
    if(refWrapper.getType() == AstBodyType.DT) {
      AstDecision decision = (AstDecision) refWrapper.getAst().get();
      Boolean collection = decision.getHitPolicy() == HitPolicy.ALL;
      if(!step.getBody().getCollection().equals(collection)) {
        result.add(ImmutableProgramAssociation.builder()
            .ref(step.getBody().getRef())
            .owner(true)
            .refStatus(ProgramStatus.DEPENDENCY_ERROR)
            .build());
        externalIdToDependencyErrors.get(wrapper.getId()).add(ImmutableProgramMessage.builder()
            .id("dependency-error")
            .msg("Dependency for ref: '" + step.getId() + "/" + step.getBody().getRef() + "'"
                + " with id: '" + refWrapper.getId() + "'"
                + " has collection: '" + collection + "'"
                + " but flow has: '" + step.getBody().getCollection() + "'!")
            .build());
      }
    }
    validator.visitStep(step, refWrapper);
  }
  
  private ProgramWrapper<?, ?> visitRefWrapper(ProgramWrapper<AstFlow, FlowProgram> wrapper, FlowProgramStep step) {
    List<String> refs = Collections.emptyList();
    final var refType = step.getBody().getRefType();
    if(refType == FlowProgramStepRefType.SERVICE) {
      refs = typeToExternalId.get(AstBodyType.FLOW_TASK);
    } else if(refType == FlowProgramStepRefType.DT) {
      refs = typeToExternalId.get(AstBodyType.DT);
    }
    
    final var result = externalIdToAssoc.get(wrapper.getId());
    final var ref = refs.stream()
        .map(id -> externalIdToWrapper.get(id))
        .filter(w -> w.getAst().isPresent())
        .filter(w -> w.getAst().get().getName().equals(step.getBody().getRef()))
        .collect(Collectors.toList());
    
    if(ref.isEmpty()) {
      result.add(ImmutableProgramAssociation.builder()
          .ref(step.getBody().getRef())
          .owner(true)
          .refStatus(ProgramStatus.DEPENDENCY_ERROR)
          .build());
      externalIdToDependencyErrors.get(wrapper.getId()).add(ImmutableProgramMessage.builder()
          .id("dependency-error")
          .msg("Missing dependency for ref: '" + step.getId() + "/" + step.getBody().getRef() + "'!")
          .build());
      
      return null;
    } else if(ref.size() > 1) {
      result.add(ImmutableProgramAssociation.builder()
          .owner(true)
          .ref(step.getBody().getRef())
          .refStatus(ProgramStatus.DEPENDENCY_ERROR)
          .build());
      final var deps = String.join(",", ref.stream().map(e -> e.getId()).collect(Collectors.toList()));
      externalIdToDependencyErrors.get(wrapper.getId()).add(ImmutableProgramMessage.builder()
          .id("dependency-error")
          .msg("Found '" + ref.size() + "'" + 
               " ref: '" + step.getId() + "/" + step.getBody().getRef() + "'" + 
               " dependencies: '" + deps + "' instead of 1!")
          .build());
      
      return null;
    }
    
    final var refWrapper = ref.get(0);
    if(refWrapper.getStatus() != ProgramStatus.UP) {
      result.add(ImmutableProgramAssociation.builder()
          .ref(step.getBody().getRef())
          .owner(true)
          .refStatus(ProgramStatus.DEPENDENCY_ERROR)
          .build());
      externalIdToDependencyErrors.get(wrapper.getId()).add(ImmutableProgramMessage.builder()
          .id("dependency-error")
          .msg("Dependency for ref: '" + step.getId() + "/" + step.getBody().getRef() + "' with id: '" + refWrapper.getId() + "' has a ERROR!")
          .build());
      
      result.add(ImmutableProgramAssociation.builder()
          .id(wrapper.getId())
          .ref(wrapper.getAst().get().getName())
          .refType(wrapper.getAst().get().getBodyType())
          .owner(false)
          .refStatus(ProgramStatus.DEPENDENCY_ERROR)
          .build());
      
      return null;
    }
    
    return refWrapper;
  }
  
  @SuppressWarnings("unchecked")
  private ProgramWrapper<?, ?> visitWrapper(ProgramWrapper<?, ?> wrapper) {
    switch (wrapper.getType()) {
    case DT: {
      final ImmutableProgramWrapper.Builder<AstDecision, DecisionProgram> builder = ImmutableProgramWrapper.builder();
      final var assoc = externalIdToAssoc.get(wrapper.getId());
      final var errors = externalIdToDependencyErrors.get(wrapper.getId());
      return builder
          .from((ProgramWrapper<AstDecision, DecisionProgram>) wrapper)
          .errors(externalIdToDependencyErrors.get(wrapper.getId()))
          .associations(assoc == null ? Collections.emptyList() : assoc)
          .status(errors.isEmpty() ? wrapper.getStatus() : ProgramStatus.DEPENDENCY_ERROR)
          .addAllErrors(errors)
          .addAllWarnings(externalIdToDependencyWarnings.get(wrapper.getId()))
          .build();
    }
    case FLOW_TASK: {
      final ImmutableProgramWrapper.Builder<AstService, ServiceProgram> builder = ImmutableProgramWrapper.builder();
      final var assoc = externalIdToAssoc.get(wrapper.getId());
      final var errors = externalIdToDependencyErrors.get(wrapper.getId());
      return builder
          .from((ProgramWrapper<AstService, ServiceProgram>) wrapper)
          .errors(externalIdToDependencyErrors.get(wrapper.getId()))
          .associations(assoc == null ? Collections.emptyList() : assoc)
          .status(errors.isEmpty() ? wrapper.getStatus() : ProgramStatus.DEPENDENCY_ERROR)
          .addAllErrors(errors)
          .addAllWarnings(externalIdToDependencyWarnings.get(wrapper.getId()))
          .build();
    }
    case FLOW: {
      final ImmutableProgramWrapper.Builder<AstFlow, FlowProgram> builder = ImmutableProgramWrapper.builder();
      final var assoc = externalIdToAssoc.get(wrapper.getId());
      final var errors = externalIdToDependencyErrors.get(wrapper.getId());
      
      return builder
          .from((ProgramWrapper<AstFlow, FlowProgram>) wrapper)
          .errors(externalIdToDependencyErrors.get(wrapper.getId()))
          .associations(assoc == null ? Collections.emptyList() : assoc)
          .status(errors.isEmpty() ? wrapper.getStatus() : ProgramStatus.DEPENDENCY_ERROR)
          .addAllErrors(errors)
          .addAllWarnings(externalIdToDependencyWarnings.get(wrapper.getId()))
          .build();
    } 
    default: throw new IllegalArgumentException("unknown command format type: '" + wrapper.getType() + "'!");
    }
  }
}