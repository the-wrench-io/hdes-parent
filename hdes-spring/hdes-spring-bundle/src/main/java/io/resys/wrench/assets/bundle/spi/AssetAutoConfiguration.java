package io.resys.wrench.assets.bundle.spi;

import java.time.Duration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.composer.ComposerEntityMapper;
import io.resys.hdes.client.spi.config.HdesClientConfig.ServiceInit;
import io.resys.hdes.client.spi.store.HdesInMemoryStore;

@Configuration
public class AssetAutoConfiguration {

  @Bean
  public AssetExceptionMapping assetExceptionMapping() {
    return new AssetExceptionMapping();
  }
  
  
  @Bean
  public HdesClient hdesClient(ObjectMapper objectMapper, ApplicationContext context) {
    final ServiceInit init = new ServiceInit() {
      @Override
      public <T> T get(Class<T> type) {
        return context.getAutowireCapableBeanFactory().createBean(type);
      }
    };
    final var store = HdesInMemoryStore.builder().objectMapper(objectMapper).build();
    return HdesClientImpl.builder().objectMapper(objectMapper).serviceInit(init).store(store).build();
    
  }
  
  @Bean
  public ProgramEnvir staticAssets(HdesClient client) {
    final var source = client.store().query().get().await().atMost(Duration.ofMinutes(1));
    return ComposerEntityMapper.toEnvir(client.envir(), source).build();
  }

}
