package io.resys.wrench.assets.bundle.spi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.resys.wrench.assets.controllers.exception.AssetExceptionMapping;
import io.resys.wrench.assets.git.AssetGitConfiguration;

@Configuration
@ConditionalOnProperty(name = "wrench.assets.enabled", havingValue = "true", matchIfMissing = true)
@Import({
  InMemoryAssetConfiguration.class, 
  AssetGitConfiguration.class, 
  AssetComponentConfiguration.class})
public class AssetAutoConfiguration {

  @Bean
  public AssetExceptionMapping assetExceptionMapping() {
    return new AssetExceptionMapping();
  }
}
