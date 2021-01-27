package io.resys.hdes.pm.quarkus.runtime.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.jboss.resteasy.spi.ReaderException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import io.resys.hdes.projects.api.PmRepository;

public class ImmutableHdesProjectsContext implements HdesProjectsContext {
  private final ObjectMapper objectMapper;
  private final PmRepository repo;
  
  
  public ImmutableHdesProjectsContext(ObjectMapper objectMapper, PmRepository repo) {
    super();
    this.objectMapper = objectMapper;
    this.repo = repo;
  }

  @Override
  public Writer writer() {
    return new Writer() {
      @Override
      public byte[] build(Object value) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          objectMapper.writeValue(out, value);
          return out.toByteArray();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  @Override
  public Reader reader() {
    return new Reader() {
      @Override
      public <T> T build(byte[] body, Class<T> type) {
        try {
          return objectMapper.readValue(body, type);
        } catch(ValueInstantiationException e) {
          throw new ReaderException(e.getMessage(), e);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public <T> List<T> list(byte[] body, Class<T> type) {
        try {
          return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch(ValueInstantiationException e) {
          throw new ReaderException(e.getMessage(), e);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  @Override
  public PmRepository repo() {
    return repo;
  }
}
