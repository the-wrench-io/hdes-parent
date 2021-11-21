package io.resys.wrench.assets.git;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.spi.store.HdesGitStore;
import io.resys.hdes.client.spi.store.git.GitConnection.GitCredsSupplier;
import io.resys.hdes.client.spi.store.git.ImmutableGitCreds;
import io.resys.wrench.assets.context.config.GitConfigBean;

@ConditionalOnProperty(name = "wrench.assets.git.enabled", havingValue = "true")
public class AssetGitConfiguration {
  
  @Bean
  public HdesStore hdesStore(Optional<GitCredsSupplier> authorProvider, GitConfigBean gitConfigBean, ObjectMapper objectMapper) {
    final GitCredsSupplier creds;
    if(authorProvider.isEmpty()) {
      if(gitConfigBean.getEmail() != null && gitConfigBean.getEmail().contains("@")) {
        creds = () -> ImmutableGitCreds.builder().user(gitConfigBean.getEmail().split("@")[0]).email(gitConfigBean.getEmail()).build(); 
      } else {
        creds = () -> ImmutableGitCreds.builder().user("assetManager").email("assetManager@resys.io").build();  
      } 
    } else {
      creds = authorProvider.get();
    }
    
    return HdesGitStore.builder()
        .remote(gitConfigBean.getRepositoryUrl())
        .branch(gitConfigBean.getBranchSpecifier())
        .sshPath(gitConfigBean.getPrivateKey())
        .storage(gitConfigBean.getRepositoryPath())
        .objectMapper(objectMapper)
        .creds(creds)
        .build();
  }
}
