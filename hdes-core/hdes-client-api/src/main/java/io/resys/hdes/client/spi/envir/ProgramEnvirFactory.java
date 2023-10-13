package io.resys.hdes.client.spi.envir;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2023 Copyright 2020 ReSys OÜ
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


import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.HdesCache;
import io.resys.hdes.client.api.HdesClient.HdesTypesMapper;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.ast.AstBody.CommandMessageType;
import io.resys.hdes.client.api.ast.AstBranch;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.AstTag;
import io.resys.hdes.client.api.programs.BranchProgram;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.ImmutableProgramEnvir;
import io.resys.hdes.client.api.programs.ImmutableProgramMessage;
import io.resys.hdes.client.api.programs.ImmutableProgramWrapper;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramMessage;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.TagProgram;
import io.resys.hdes.client.spi.branch.BranchProgramBuilder;
import io.resys.hdes.client.spi.config.HdesClientConfig;
import io.resys.hdes.client.spi.decision.DecisionProgramBuilder;
import io.resys.hdes.client.spi.flow.FlowProgramBuilder;
import io.resys.hdes.client.spi.groovy.ServiceProgramBuilder;
import io.resys.hdes.client.spi.tag.TagProgramBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgramEnvirFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramEnvirFactory.class);
  private final HdesClientConfig config;
  private final HdesAstTypes hdesTypes;
  private final HdesTypesMapper hdesFactory;
  private final HdesCache cache;
  private final AssociationVisitor tree = new AssociationVisitor();
  private final List<String> visitedIds = new ArrayList<>();
  private final List<String> cachlessIds = new ArrayList<>();
  private final StringBuilder treelog = new StringBuilder();
  private ProgramEnvir baseEnvir;
  
  public ProgramEnvirFactory(HdesAstTypes hdesTypes, HdesTypesMapper hdesFactory, HdesClientConfig config) {
    super();
    this.hdesTypes = hdesTypes;
    this.hdesFactory = hdesFactory;
    this.cache = config.getCache();
    this.config = config;
  }
  
  public ProgramEnvirFactory add(ProgramEnvir envir) {
    this.baseEnvir = envir;
    return this;
  }
  public ProgramEnvirFactory add(AstSource entity, boolean cachless) {
    if(cachless) {
      cachlessIds.add(entity.getId());
    }
    final var wrapper = visitSource(entity);
    visitedIds.add(wrapper.getId());
    tree.add(wrapper);
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public ProgramEnvir build() {
    final var envir = ImmutableProgramEnvir.builder();
    if(baseEnvir != null) {
      baseEnvir.getValues().values().stream()
        .filter(wrapper -> !visitedIds.contains(wrapper.getId()))
        .forEach(tree::add);
    }
    
    tree.build().forEach(e -> {

      visitTreeLog(e);
      
      envir.putValues(e.getId(), e);
      
      final var ast = e.getAst().orElse(null);
      if(ast == null) {
        return;
      }

      // TODO: handle entries not unique
      switch (ast.getBodyType()) {
      case DT: envir.putDecisionsByName(ast.getName(), (ProgramWrapper<AstDecision, DecisionProgram>) e); break;
      case FLOW_TASK: envir.putServicesByName(ast.getName(), (ProgramWrapper<AstService, ServiceProgram>) e); break;
      case FLOW: envir.putFlowsByName(ast.getName(), (ProgramWrapper<AstFlow, FlowProgram>) e); break;
      case TAG: envir.putTagsByName(ast.getName(), (ProgramWrapper<AstTag, TagProgram>) e); break;
      case BRANCH: envir.putBranchesByName(ast.getName(), (ProgramWrapper<AstBranch, BranchProgram>) e); break;
      default: break;
      
      }
    });
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug(new StringBuilder()
          .append("Envir status: " + treelog.length()).append(System.lineSeparator())
          .append(treelog.toString())
          .toString());
    }
    return envir.build();
  }
  
  public void visitTreeLog(ProgramWrapper<?, ?> wrapper) {
    if(LOGGER.isDebugEnabled()) {
      final String name = wrapper.getAst().map(w -> w.getName()).orElseGet(() -> wrapper.getId());
      treelog.append("  - ").append(name).append(": ").append(wrapper.getStatus()).append(System.lineSeparator());
      if(wrapper.getStatus() != ProgramStatus.UP) {
      
        for(final var error : wrapper.getErrors()) {
          treelog.append("    - ").append(error.getId()).append(": ").append(error.getMsg()).append(System.lineSeparator());
          if(error.getException() != null) {
            treelog.append("      ").append(ExceptionUtils.getStackTrace(error.getException())).append(System.lineSeparator());
          }
        }
      }
    }

  }
  
  private ProgramWrapper<?, ?> visitSource(AstSource entity) {
    ProgramWrapper<?, ?> result = null;
    switch (entity.getBodyType()) {
    case DT: result = visitDecision(entity); break;
    case FLOW: result = visitFlow(entity); break;
    case FLOW_TASK: result = visitFlowTask(entity); break;
    case TAG: result = visitTag(entity); break;
    case BRANCH: result = visitBranch(entity); break;
    default: throw new IllegalArgumentException("unknown command format type: '" + entity.getBodyType() + "'!");
    }
    return result;
  }

  private ProgramWrapper<AstDecision, DecisionProgram> visitDecision(AstSource src) {
    final ImmutableProgramWrapper.Builder<AstDecision, DecisionProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstDecision ast = null;
    try {
      
      if(cachlessIds.contains(src.getId())) {
        ast = hdesTypes.decision().src(src.getCommands()).build();
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (AstDecision) cached.get();
        } else {
          ast = hdesTypes.decision().src(src.getCommands()).build();
          cache.setAst(ast, src);
        }
      }
      
      final var errors = ast.getMessages().stream()
        .filter(m -> m.getType() == CommandMessageType.ERROR)
        .map(error -> ImmutableProgramMessage.builder()
            .id("ast-error")
            .msg("line: " + error.getLine() + ": " + error.getValue())
            .build())
        .collect(Collectors.toList());
      builder.addAllErrors(errors);
      
      if(!errors.isEmpty()) {
        builder.status(ProgramStatus.AST_ERROR);
      }
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - decision source: ").append(this.hdesFactory.commandsString(src.getCommands()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    DecisionProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new DecisionProgramBuilder(hdesFactory).build(ast);
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = (DecisionProgram) cached.get();
          } else {
            program = new DecisionProgramBuilder(hdesFactory).build(ast);
            cache.setProgram(program, src);          
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - decision source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString(), e);
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    
    return builder.id(src.getId())
        .type(AstBodyType.DT)
        .ast(Optional.ofNullable(ast))
        .program(Optional.ofNullable(program))
        .source(src)
        .build();
  }
  
  private ProgramWrapper<AstFlow, FlowProgram> visitFlow(AstSource src) {
    final ImmutableProgramWrapper.Builder<AstFlow, FlowProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    
    AstFlow ast = null;
    try {
      if(cachlessIds.contains(src.getId())) {
        ast = hdesTypes.flow().src(src.getCommands()).build();
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (AstFlow) cached.get();
        } else {
          ast = hdesTypes.flow().src(src.getCommands()).build();
          cache.setAst(ast, src);
        }
      }
      final var errors = ast.getMessages().stream()
        .filter(m -> m.getType() == CommandMessageType.ERROR)
        .map(error -> ImmutableProgramMessage.builder()
            .id("ast-error")
            .msg("line: " + error.getLine() + ": " + error.getValue())
            .build())
        .collect(Collectors.toList());
      builder.addAllErrors(errors);
      
      if(!errors.isEmpty()) {
        builder.status(ProgramStatus.AST_ERROR);
      }
      
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - flow source: ").append(this.hdesFactory.commandsString(src.getCommands()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    FlowProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new FlowProgramBuilder(hdesFactory).build(ast);
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = (FlowProgram) cached.get();
          } else {
            program = new FlowProgramBuilder(hdesFactory).build(ast);
            cache.setProgram(program, src);
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - flow source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString(), e);
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    return builder.id(src.getId())
        .ast(Optional.ofNullable(ast)).program(Optional.ofNullable(program))
        .source(src)
        .type(AstBodyType.FLOW).build(); 
  }
  
  private ProgramWrapper<AstTag, TagProgram> visitTag(AstSource src) {
    final ImmutableProgramWrapper.Builder<AstTag, TagProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstTag ast = null;
    try {      
      if(cachlessIds.contains(src.getId())) {
        ast = hdesTypes.tag().src(src.getCommands()).build();
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (AstTag) cached.get();
        } else {
          ast = hdesTypes.tag().src(src.getCommands()).build();
          cache.setAst(ast, src);
        }
      }
      
      final var errors = ast.getMessages().stream()
          .filter(m -> m.getType() == CommandMessageType.ERROR)
          .map(error -> ImmutableProgramMessage.builder()
              .id("ast-error")
              .msg("line: " + error.getLine() + ": " + error.getValue())
              .build())
          .collect(Collectors.toList());
        builder.addAllErrors(errors);
      if(!errors.isEmpty()) {
        LOGGER.error(new StringBuilder()
            .append(String.join(System.lineSeparator(), errors.stream().map(e -> e.getMsg()).collect(Collectors.toList())))
            .append("  - tag source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString());
        builder.status(ProgramStatus.AST_ERROR);
      }
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - tag source: ").append(this.hdesFactory.commandsString(src.getCommands()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    TagProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new TagProgramBuilder(config).build(ast);          
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = (TagProgram) cached.get();
          } else {
            program = new TagProgramBuilder(config).build(ast);
            cache.setProgram(program, src);
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - tag source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString(), e);
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    
    return builder.id(src.getId()).type(AstBodyType.TAG)
        .ast(Optional.ofNullable(ast)).program(Optional.ofNullable(program))
        .source(src)
        .build(); 
  }

  private ProgramWrapper<AstBranch, BranchProgram> visitBranch(AstSource src) {
    final ImmutableProgramWrapper.Builder<AstBranch, BranchProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstBranch ast = null;
    try {
      if(cachlessIds.contains(src.getId())) {
        ast = hdesTypes.branch().src(src.getCommands()).build();
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (AstBranch) cached.get();
        } else {
          ast = hdesTypes.branch().src(src.getCommands()).build();
          cache.setAst(ast, src);
        }
      }

      final var errors = ast.getMessages().stream()
          .filter(m -> m.getType() == CommandMessageType.ERROR)
          .map(error -> ImmutableProgramMessage.builder()
              .id("ast-error")
              .msg("line: " + error.getLine() + ": " + error.getValue())
              .build())
          .collect(Collectors.toList());
      builder.addAllErrors(errors);
      if(!errors.isEmpty()) {
        LOGGER.error(new StringBuilder()
            .append(String.join(System.lineSeparator(), errors.stream().map(e -> e.getMsg()).collect(Collectors.toList())))
            .append("  - branch source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString());
        builder.status(ProgramStatus.AST_ERROR);
      }
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - branch source: ").append(this.hdesFactory.commandsString(src.getCommands()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }

    BranchProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new BranchProgramBuilder(config).build(ast);
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = (BranchProgram) cached.get();
          } else {
            program = new BranchProgramBuilder(config).build(ast);
            cache.setProgram(program, src);
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - branch source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString(), e);
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }

    return builder.id(src.getId()).type(AstBodyType.BRANCH)
        .ast(Optional.ofNullable(ast)).program(Optional.ofNullable(program))
        .source(src)
        .build();
  }
  
  
  private ProgramWrapper<AstService, ServiceProgram> visitFlowTask(AstSource src) {
    final ImmutableProgramWrapper.Builder<AstService, ServiceProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstService ast = null;
    try {      
      if(cachlessIds.contains(src.getId())) {
        ast = hdesTypes.service().src(src.getCommands()).build();
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (AstService) cached.get();
        } else {
          ast = hdesTypes.service().src(src.getCommands()).build();
          cache.setAst(ast, src);
        }
      }
      
      final var errors = ast.getMessages().stream()
          .filter(m -> m.getType() == CommandMessageType.ERROR)
          .map(error -> ImmutableProgramMessage.builder()
              .id("ast-error")
              .msg("line: " + error.getLine() + ": " + error.getValue())
              .build())
          .collect(Collectors.toList());
        builder.addAllErrors(errors);
      if(!errors.isEmpty()) {
        LOGGER.error(new StringBuilder()
            .append(String.join(System.lineSeparator(), errors.stream().map(e -> e.getMsg()).collect(Collectors.toList())))
            .append("  - service source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString());
        builder.status(ProgramStatus.AST_ERROR);
      }
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - service source: ").append(this.hdesFactory.commandsString(src.getCommands()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    ServiceProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new ServiceProgramBuilder(config).build(ast);          
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = (ServiceProgram) cached.get();
          } else {
            program = new ServiceProgramBuilder(config).build(ast);
            cache.setProgram(program, src);
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - service source: ").append(this.hdesFactory.commandsString(src.getCommands()))
            .toString(), e);
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    
    return builder.id(src.getId()).type(AstBodyType.FLOW_TASK)
        .ast(Optional.ofNullable(ast)).program(Optional.ofNullable(program))
        .source(src)
        .build(); 
  }
  
  private List<ProgramMessage> visitException(Exception e) {
    return Arrays.asList(ImmutableProgramMessage.builder()
          .id("exception")
          .msg(e.getMessage() == null ? "no-desc-available": e.getMessage().replaceAll("\"", "'"))
          .exception(e)
          .build()
        );
  }
}
