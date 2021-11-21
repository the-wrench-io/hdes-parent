package io.resys.wrench.assets.controllers;

import java.time.Duration;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.resys.hdes.client.api.HdesComposer;
import io.resys.hdes.client.api.HdesComposer.ComposerEntity;
import io.resys.hdes.client.api.HdesComposer.ComposerState;
import io.resys.hdes.client.api.HdesComposer.CopyAs;
import io.resys.hdes.client.api.HdesComposer.CreateEntity;
import io.resys.hdes.client.api.HdesComposer.DebugRequest;
import io.resys.hdes.client.api.HdesComposer.DebugResponse;
import io.resys.hdes.client.api.HdesComposer.StoreDump;
import io.resys.hdes.client.api.HdesComposer.UpdateEntity;

@RestController
@RequestMapping(ControllerUtil.ASSET_CONTEXT_PATH)
public class ComposerController {
  private final HdesComposer composer;
  private static final Duration timeout = Duration.ofMillis(1000);
  
  public ComposerController(HdesComposer composer) {
    super();
    this.composer = composer;
  }

  @GetMapping(value="/dataModels", produces = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState dataModels() {
    return composer.get().await().atMost(timeout);
  }

  @GetMapping(value="/exports", produces = MediaType.APPLICATION_JSON_VALUE)
  public StoreDump exports() {
    return composer.getStoreDump().await().atMost(timeout);
  }
  
  @PostMapping(path = "/commands", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerEntity<?> commands(@RequestBody UpdateEntity command) {
    return composer.dryRun(command).await().atMost(timeout);
  }

  @PostMapping(path = "/debugs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public DebugResponse debug(@RequestBody DebugRequest debug) {
    return composer.debug(debug).await().atMost(timeout);
  }

  @PostMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState create(@RequestBody CreateEntity entity) {
    return composer.create(entity).await().atMost(timeout);
  }

  @PutMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState update(@RequestBody UpdateEntity entity) {
    return composer.update(entity).await().atMost(timeout);
  }
  
  @DeleteMapping(path = "/resources/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState delete(@PathVariable String id) {
    return composer.delete(id).await().atMost(timeout);
  }
  
  @PostMapping(path = "/copyas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState copyAs(@RequestBody CopyAs entity) {
    return composer.copyAs(entity).await().atMost(timeout);
  }

  @GetMapping(path = "/resources/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ComposerEntity<?> get(@PathVariable String id) {
    return composer.get(id).await().atMost(timeout);
  }
  
  @GetMapping(path = "/resources/{id}/history", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ComposerEntity<?>> history(@RequestParam("id") String id) {
    return composer.getHistory(id).await().atMost(timeout);
  }
}
