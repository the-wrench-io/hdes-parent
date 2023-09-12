package io.resys.hdes.client.spi.composer;

import io.resys.hdes.client.api.HdesComposer.ComposerState;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CreateBranchVisitor {

  private final ComposerState state;
  private final List<AstCommand> result = new ArrayList<>();

  public List<AstCommand> visitCommands(List<AstCommand> commands) {
    for (final var command : commands) {
      visitCommand(command);
    }
    return result;
  }

  private void visitCommand(AstCommand command) {
    switch (command.getType()) {
      case CREATE_BRANCH:
        visitCreateBranch(command);
        break;
      default:
        throw new IllegalArgumentException("Not supported command type: " + command.getType());
    }
  }

  private void visitCreateBranch(AstCommand command) {
    final var tagId = command.getId();
    final var tagComposerEntity= Objects.requireNonNull(state.getTags().get(tagId), () -> "Tag '" + tagId + "' not found!");
    final var tag = Objects.requireNonNull(tagComposerEntity.getAst(), () -> "AstTag '" + tagId + "' not found!");

    result.add(ImmutableAstCommand.builder().type(AstCommandValue.CREATE_BRANCH).value(tagId + "_dev").build());

    final var decisions = tag.getValues().stream().filter(astTagValue -> astTagValue.getBodyType().equals(AstBodyType.DT)).collect(Collectors.toList());
    final var flows = tag.getValues().stream().filter(astTagValue -> astTagValue.getBodyType().equals(AstBodyType.FLOW)).collect(Collectors.toList());
    final var flowTasks = tag.getValues().stream().filter(astTagValue -> astTagValue.getBodyType().equals(AstBodyType.FLOW_TASK)).collect(Collectors.toList());

    for (final var decision : decisions) {
      result.add(ImmutableAstCommand.builder().type(AstCommandValue.CREATE_BRANCH_DT).nestedCommands(decision.getCommands()).build());
    }
    for (final var flow : flows) {
      result.add(ImmutableAstCommand.builder().type(AstCommandValue.CREATE_BRANCH_FLOW).nestedCommands(flow.getCommands()).build());
    }
    for (final var flowTask : flowTasks) {
      result.add(ImmutableAstCommand.builder().type(AstCommandValue.CREATE_BRANCH_FLOWTASK).nestedCommands(flowTask.getCommands()).build());
    }
  }
}
