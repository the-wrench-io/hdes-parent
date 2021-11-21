package io.resys.hdes.client.spi.cache;

import java.io.Serializable;
import java.util.Optional;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.programs.Program;

public class HdesClientEhCache implements HdesClientCache {
  private static final String CACHE_NAME = HdesClientCache.class.getCanonicalName();
  private final CacheManager cacheManager;
  
  private HdesClientEhCache(CacheManager cacheManager) {
    super();
    this.cacheManager = cacheManager;
  }
  private Cache<String, CacheEntry> getCache() {
    return cacheManager.getCache(CACHE_NAME, String.class, CacheEntry.class);
  }
  @Override
  public Optional<Program> getProgram(AstSource src) {
    final var cache = getCache();
    return Optional.ofNullable(cache.get(src.getHash())).map(e -> e.getProgram().orElse(null));
  }
  @Override
  public Optional<AstBody> getAst(AstSource src) {
    final var cache = getCache();
    return Optional.ofNullable(cache.get(src.getHash())).map(e -> e.getAst());
  }
  @Override
  public Program setProgram(Program program, AstSource src) {
    final var cache = getCache();
    final var previous = cache.get(src.getHash());
    final var entry = ImmutableCacheEntry.builder().from(previous).program(program).build();
    cache.put(entry.getId(), entry);
    return program;
  }
  @Override
  public AstBody setAst(AstBody ast, AstSource src) {
    final var entry = ImmutableCacheEntry.builder().source(src).ast(ast).build();
    final var cache = getCache();
    cache.put(entry.getId(), entry);
    return ast;
  }
  @Value.Immutable
  public interface CacheEntry extends Serializable {
    String getId();
    AstSource getSource();
    AstBody getAst();
    Optional<Program> getProgram();
  }
  
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public HdesClientEhCache build() {    
      
      final var cacheHeap = 500;
      final var cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
          .withCache(CACHE_NAME,
              CacheConfigurationBuilder.newCacheConfigurationBuilder(
                  String.class, CacheEntry.class, 
                  ResourcePoolsBuilder.heap(cacheHeap))) 
          .build(); 
      cacheManager.init();
      
      return new HdesClientEhCache(cacheManager);
    }
  }
}
