package io.resys.wrench.assets.git;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.git.spi.HdesStoreGit;
import io.resys.hdes.client.git.spi.connection.GitConnection.GitCredsSupplier;
import io.resys.hdes.client.git.spi.connection.ImmutableGitCreds;
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
    
    return HdesStoreGit.builder()
        .remote(gitConfigBean.getRepositoryUrl())
        .branch(gitConfigBean.getBranchSpecifier())
        .sshPath(gitConfigBean.getPrivateKey())
        .storage(gitConfigBean.getRepositoryPath())
        .objectMapper(objectMapper)
        .creds(creds)
        .build();
  }
  
  
  
//  @Bean
//  public GitRepository gitRepository(
//      GitConfigBean gitConfigBean, 
//      Optional<AssetAuthorProvider> authorProvider) 
//      throws InvalidRemoteException, IOException, GitAPIException {
//    
//    if(authorProvider.isEmpty()) {
//      if(gitConfigBean.getEmail() != null && gitConfigBean.getEmail().contains("@")) {
//        authorProvider = Optional.of(() -> new AssetAuthorProvider.Author(gitConfigBean.getEmail().split("@")[0], gitConfigBean.getEmail()));
//      } else {
//        authorProvider = Optional.of(() -> new AssetAuthorProvider.Author("assetManager", "assetManager@resys.io"));  
//      } 
//    }
//    return new SshGitRepository(gitConfigBean.toConfig(), authorProvider.get());
//  }
//  @Bean
//  public AssetLocation gitAsssetLocation(GitRepository gitRepository) {
//    return new AssetLocation(gitRepository.getWorkingDir(), "/assets/", true);
//  }
//  @Bean
//  public ServiceStore serviceStore(GitRepository gitRepository, AssetLocation location) {
//    return new GitAssetStore(gitRepository, location);
//  }
}
