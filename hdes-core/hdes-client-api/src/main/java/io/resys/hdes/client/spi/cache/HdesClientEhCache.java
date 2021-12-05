package io.resys.hdes.client.spi.cache;

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

import java.util.Optional;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import io.resys.hdes.client.api.HdesCache;
import io.resys.hdes.client.api.ImmutableCacheEntry;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.AstSource;
import io.resys.hdes.client.api.programs.Program;

public class HdesClientEhCache implements HdesCache {
  private static final String CACHE_NAME = HdesCache.class.getCanonicalName();
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
    return Optional.ofNullable(cache.get(src.getHash()))
        .or(() -> Optional.ofNullable(cache.get(src.getId())))
        .map(e -> e.getProgram().orElse(null));
  }
  @Override
  public Optional<AstBody> getAst(AstSource src) {
    final var cache = getCache();
    return Optional.ofNullable(cache.get(src.getHash()))
        .or(() -> Optional.ofNullable(cache.get(src.getId())))
        .map(e -> e.getAst());
  }
  @Override
  public Program setProgram(Program program, AstSource src) {
    final var cache = getCache();
    final var previous = cache.get(src.getHash());
    final var entry = ImmutableCacheEntry.builder().from(previous).program(program).build();
    cache.put(entry.getId(), entry);
    cache.put(src.getHash(), entry);
    return program;
  }
  @Override
  public AstBody setAst(AstBody ast, AstSource src) {
    final var entry = ImmutableCacheEntry.builder().id(src.getId()).source(src).ast(ast).build();
    final var cache = getCache();
    cache.put(entry.getId(), entry);
    cache.put(src.getHash(), entry);
    return ast;
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
