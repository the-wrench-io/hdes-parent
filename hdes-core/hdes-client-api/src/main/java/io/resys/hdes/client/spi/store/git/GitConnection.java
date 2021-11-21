package io.resys.hdes.client.spi.store.git;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.ehcache.CacheManager;
import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;

@Value.Immutable
public interface GitConnection {
  GitInit getInit();
  
  StoreEntityLocation getLocation();
  GitSerializer getSerializer();
  
  GitCredsSupplier getCreds();
  CacheManager getCacheManager();
  String getCacheName();
  Integer getCacheHeap();
  
  String getAssetsPath();          // relative path starts from repository root
  Path getParentPath();           // path where git repository is cloned
  String getAbsolutePath();       // git working directory path
  String getAbsoluteAssetsPath(); // absolute path for assets in the git working directory 
  TransportConfigCallback getCallback();
  Git getClient();
  
  @Value.Immutable
  interface GitInit {
    String getBranch();
    String getRemote();
    String getSshPath();
    String getStorage();
  }
  
  @Value.Immutable
  interface GitCreds {
    String getUser();
    String getEmail();
  } 

  @Value.Immutable
  interface GitEntry {
    String getId();
    Timestamp getCreated();
    Timestamp getModified();
    AstBodyType getBodyType();
    String getRevision();
    String getBlobHash();
    String getTreeValue();
    String getBlobValue();
    List<AstCommand> getCommands();
  }
  
  interface GitSerializer {
    List<AstCommand> read(String commands);
    String write(List<AstCommand> commands);
  }
  interface GitCredsSupplier extends Supplier<GitCreds> {}
  
}
