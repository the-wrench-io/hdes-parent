package io.resys.hdes.client.spi.datadump;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import io.resys.hdes.client.api.HdesClient.HdesTypesMapper;
import io.resys.hdes.client.api.HdesComposer.StoreDump;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableStoreDump;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.ast.AstBody.Headers;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision.AstDecisionRow;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.ast.ImmutableAstSource;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.spi.changeset.AstChangesetFactory;
import io.resys.hdes.client.spi.decision.DecisionAstBuilderImpl;



public class DataDumpBuilder {

  private final HdesTypesMapper defs;

  public DataDumpBuilder(HdesTypesMapper defs) {
    super();
    this.defs = defs;
  }

  private static String md5(String ...input) {
    Vector<InputStream> v = new Vector<>();
    
    for(final var el : input) {
      v.add(new ByteArrayInputStream(el.getBytes(StandardCharsets.UTF_8)));
    }
    SequenceInputStream seqStream = new SequenceInputStream(v.elements());
    try {
      String md5Hash = DigestUtils.md5Hex(seqStream);
      seqStream.close();
      return md5Hash;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private AstSource visitDt(StoreEntity service) throws IOException {
    final var dt = new DecisionAstBuilderImpl(defs).src(service.getBody()).build();
    final var headers = dt.getHeaders();
    final List<AstCommand> commands = createHeaderCommands(headers);

    createRow(headers, dt.getRows(), commands);
    commands.add(ImmutableAstCommand.builder().value(dt.getHitPolicy().name()).type(AstCommandValue.SET_HIT_POLICY).build());
    commands.add(ImmutableAstCommand.builder().value(dt.getName()).type(AstCommandValue.SET_NAME).build());
    commands.add(ImmutableAstCommand.builder().value(dt.getDescription()).type(AstCommandValue.SET_DESCRIPTION).build());


    return ImmutableAstSource.builder()
        .id(service.getId())
        .bodyType(service.getBodyType())
        .hash(md5(body))
        .addAllCommands(commands)
        .build();
  }

  private AstSource visitSt(StoreEntity service) throws IOException {
    final var changes = AstChangesetFactory.src(service.getBody(), null);
    final var src = new StringBuilder();
    changes.getCommands().forEach(c -> src.append(c.getValue()).append(System.lineSeparator()));
    final var body = src.toString();
    
    return ImmutableAstSource.builder()
        .id(service.getId())
        .bodyType(service.getBodyType())
        .hash(md5(body))
        .addCommands(ImmutableAstCommand.builder().value(body).type(AstCommandValue.SET_BODY).build())
        .build();
  }

  private AstSource visitFl(StoreEntity service) throws IOException {
    final var changes = AstChangesetFactory.src(service.getBody(), null);
    final var src = new StringBuilder();
    changes.getCommands().forEach(c -> src.append(c.getValue()).append(System.lineSeparator()));
    final var body = src.toString();
    
    return ImmutableAstSource.builder()
        .id(service.getId())
        .bodyType(service.getBodyType())
        .hash(md5(body))
        .addCommands(ImmutableAstCommand.builder().value(body).type(AstCommandValue.SET_BODY).build())
        .build();
  }

  public StoreDump build(StoreState state) {
    final var result = ImmutableStoreDump.builder();
    final var inputs = new ArrayList<String>();
    
    final var assets = new ArrayList<StoreEntity>();
    assets.addAll(state.getDecisions().values());
    assets.addAll(state.getServices().values());
    assets.addAll(state.getFlows().values());
    
    for (final var service : assets) {
      try {
        switch (service.getBodyType()) {
        case FLOW:
          inputs.add(service.getSrc());
          result.addValue(visitFl(service));
          break;
        case DT:
          inputs.add(service.getSrc());
          result.addValue(visitDt(service));
          break;
        case FLOW_TASK:
          inputs.add(service.getSrc());
          result.addValue(visitSt(service));
          break;

        default:
          continue;
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to create migration because of asset: '"  + service.getId() + "::" + service.getName() + "', msg: " + e.getMessage(), e);
      }
    }
    return result.id(md5(inputs.toArray(new String[0]))).build();
  }
  
  
  private void createRow(Headers headers, List<AstDecisionRow> nodes, List<AstCommand> result) {  
    final List<TypeDef> types = new ArrayList<>();
    types.addAll(headers.getAcceptDefs());
    types.addAll(headers.getReturnDefs());
    int rows = 1;
    
    for(final var node : nodes) {
    
      int nextId = types.size() * rows + rows;
      result.add(ImmutableAstCommand.builder().type(AstCommandValue.ADD_ROW).build());
  
      Map<String, Object> entries = new HashMap<>();
      node.getCells().forEach(e -> entries.put(e.getHeader(), e.getValue()));
  
      for(final var header : types) {
        Object value = entries.get(header.getName());
        result.add(ImmutableAstCommand.builder()
            .id(String.valueOf(nextId++))
            .value(value == null ? null : header.getSerializer().serialize(header.getValue(), value))
            .type(AstCommandValue.SET_CELL_VALUE)
            .build());
      }
      
      ++rows;
    }
  }

  private List<AstCommand> createHeaderCommands(Headers headers) {
    final List<AstCommand> result = new ArrayList<>();
    final List<TypeDef> types = new ArrayList<>();
    
    types.addAll(headers.getAcceptDefs());
    types.addAll(headers.getReturnDefs());
    int index = 0;
    for(TypeDef dataType : types) {
      String id = String.valueOf(index);
      result.add(ImmutableAstCommand.builder().type(dataType.getDirection() == Direction.IN ? AstCommandValue.ADD_HEADER_IN : AstCommandValue.ADD_HEADER_OUT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getName()).type(AstCommandValue.SET_HEADER_REF).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getScript()).type(AstCommandValue.SET_HEADER_SCRIPT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getValueType() == null ? null : dataType.getValueType().name()).type(AstCommandValue.SET_HEADER_TYPE).build());
      index++;
    }
    return result;
  }
}
