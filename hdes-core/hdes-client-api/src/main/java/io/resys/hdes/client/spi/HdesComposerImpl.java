package io.resys.hdes.client.spi;

import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer;
import io.resys.hdes.client.api.HdesStore.HistoryEntity;
import io.resys.hdes.client.api.ImmutableUpdateStoreEntity;
import io.resys.hdes.client.spi.composer.ComposerStateVisitor;
import io.resys.hdes.client.spi.composer.CopyAsEntityVisitor;
import io.resys.hdes.client.spi.composer.CreateEntityVisitor;
import io.resys.hdes.client.spi.composer.DataDumpVisitor;
import io.resys.hdes.client.spi.composer.DryRunVisitor;
import io.smallrye.mutiny.Uni;

public class HdesComposerImpl implements HdesComposer {

  private final HdesClient client;
  private final ComposerStateVisitor state;
  private final DataDumpVisitor datadump;
  private final DryRunVisitor dryrun;

  public HdesComposerImpl(HdesClient client) {
    super();
    this.client = client;
    this.state = new ComposerStateVisitor(client);
    this.datadump = new DataDumpVisitor(client);
    this.dryrun = new DryRunVisitor(client);
  }

  @Override
  public Uni<ComposerState> get() {
    return client.store().query().get().onItem().transform(state::visit);
  }
  @Override
  public Uni<ComposerState> update(UpdateEntity asset) {
    return client.store().update(ImmutableUpdateStoreEntity.builder().id(asset.getId()).body(asset.getBody()).build())
        .onItem().transformToUni((updated) ->  
          client.store().query().get().onItem().transform(state::visit)
        );
  }
  @Override
  public Uni<ComposerState> create(CreateEntity asset) {
    return client.store().query().get().onItem().transform(state::visit)
        .onItem().transformToUni(state -> client.store().create(new CreateEntityVisitor(state, asset).visit()))
        .onItem().transformToUni(savedEntity -> client.store().query().get().onItem().transform(state::visit));
  }
  @Override
  public Uni<ComposerState> delete(String id) {
    client.store().query().get().onItem().transform(state::visit);
    return null;
  }

  @Override
  public Uni<ComposerEntity<?>> get(String idOrName) {
    return client.store().query().get().onItem().transform(state::visit)
      .onItem().transform(state -> {
        List<ComposerEntity<?>> entities = new ArrayList<>();
        entities.addAll(state.getDecisions().values());
        entities.addAll(state.getFlows().values());
        entities.addAll(state.getServices().values());
        entities.addAll(state.getTags().values());
        return entities.stream()
            .filter(e -> e.getId().equals(idOrName) || (e.getAst() != null && e.getAst().getName().equals(idOrName)))
            .findFirst().orElse(null);
      });
  }

  @Override
  public Uni<HistoryEntity> getHistory(String id) {
    return client.store().history().get(id);
  }

  @Override
  public Uni<ComposerState> copyAs(CopyAs copyAs) {
    return client.store().query().get().onItem().transform(state::visit)
        .onItem().transform(state -> new CopyAsEntityVisitor(state, copyAs, client).visit())
        .onItem().transformToUni(newEntity -> client.store().create(newEntity))
        .onItem().transformToUni(savedEntity -> client.store().query().get().onItem().transform(state::visit));
  }

  @Override
  public Uni<DebugResponse> debug(DebugRequest entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerEntity<?>> dryRun(UpdateEntity entity) {
    return client.store().query().get().onItem().transform(state -> dryrun.visit(state, entity));
  }

  @Override
  public Uni<StoreDump> getStoreDump() {
    return client.store().query().get().onItem().transform(datadump::visit);
  }
}
