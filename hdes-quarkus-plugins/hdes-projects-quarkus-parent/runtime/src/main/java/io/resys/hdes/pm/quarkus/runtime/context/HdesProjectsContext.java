package io.resys.hdes.pm.quarkus.runtime.context;

import java.util.List;

import io.resys.hdes.projects.api.PmRepository;

public interface HdesProjectsContext {

  PmRepository repo();
  Writer writer();
  Reader reader();
  
  interface Writer {
    byte[] build(Object value);
  }
  interface Reader {
    <T> T build(byte[] body, Class<T> type);
    <T> List<T> list(byte[] body, Class<T> type);
  }
}
