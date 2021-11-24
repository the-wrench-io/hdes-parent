package io.resys.wrench.assets.bundle.spi;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.config.HdesClientConfig.ServiceInit;


@Configuration
public class AssetComponentConfiguration {
  
  @Bean
  public HdesClient assetServiceRepository(ApplicationContext context, ObjectMapper objectMapper, HdesStore store) {
    final ServiceInit init = new ServiceInit() {
      @Override
      public <T> T get(Class<T> type) {
        return context.getAutowireCapableBeanFactory().createBean(type);
      }
    };
    return HdesClientImpl.builder().objectMapper(objectMapper).serviceInit(init).store(store).build();
  }
}
