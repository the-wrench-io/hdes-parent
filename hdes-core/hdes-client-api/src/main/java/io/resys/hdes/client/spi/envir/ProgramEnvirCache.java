package io.resys.hdes.client.spi.envir;

import java.util.Optional;

import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;

public interface ProgramEnvirCache {
  Optional<ProgramWrapper<?, ?>> get(String hash);
  void add(ProgramWrapper<?, ?> wrapper);
  void remove(String hash);
}
