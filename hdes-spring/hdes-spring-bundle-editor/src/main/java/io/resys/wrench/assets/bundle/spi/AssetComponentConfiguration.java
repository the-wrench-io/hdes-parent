package io.resys.wrench.assets.bundle.spi;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.config.HdesClientConfig.ServiceInit;
import io.resys.hdes.client.spi.flow.autocomplete.DescAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.IdAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.input.InputAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.input.InputDataTypeAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.input.InputDebugValueAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.input.InputRequiredAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.input.InputsAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.SwitchAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.SwitchBodyAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.TaskAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.TaskCollectionAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.TaskThenAutocomplete;
import io.resys.hdes.client.spi.flow.autocomplete.task.TasksAutocomplete;
import io.resys.hdes.client.spi.flow.validators.DescriptionValidator;
import io.resys.hdes.client.spi.flow.validators.IdValidator;
import io.resys.wrench.assets.context.config.AssetConfigBean;
import io.resys.wrench.assets.context.config.GitConfigBean;


@Configuration
@EnableConfigurationProperties({
  GitConfigBean.class,
  AssetConfigBean.class})
public class AssetComponentConfiguration {
  @Bean
  public HdesClient hdesClient(ApplicationContext context, ObjectMapper objectMapper, AssetConfigBean assetConfigBean, HdesStore store) {
    final ServiceInit init = new ServiceInit() {
      @Override
      public <T> T get(Class<T> type) {
        return context.getAutowireCapableBeanFactory().createBean(type);
      }
    };

    final HdesClientImpl hdesClient = HdesClientImpl.builder()
        .store(store)
        .objectMapper(objectMapper)
        .serviceInit(init)
        .flowVisitors(
          new IdAutocomplete(),
          new DescAutocomplete(),
          new InputsAutocomplete(),
          new TasksAutocomplete(),
          new TaskThenAutocomplete(),
          new InputRequiredAutocomplete(),
          new InputDataTypeAutocomplete(),
          new InputAutocomplete(),
          new TaskAutocomplete(),
          new TaskCollectionAutocomplete(),
          new SwitchAutocomplete(),
          new InputDebugValueAutocomplete(),
          new SwitchBodyAutocomplete(),
          new IdValidator(),
          new DescriptionValidator()
        )
        .build();
    
    return hdesClient;
  }
}
