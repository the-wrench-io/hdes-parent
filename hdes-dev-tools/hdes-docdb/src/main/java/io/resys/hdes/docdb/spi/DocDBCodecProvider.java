package io.resys.hdes.docdb.spi;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.sun.source.tree.Tree;

import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.api.models.Objects.TreeValue;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.codec.BlobCodec;
import io.resys.hdes.docdb.spi.codec.CommitCodec;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.resys.hdes.docdb.spi.codec.TagCodec;
import io.resys.hdes.docdb.spi.codec.TreeCodec;
import io.resys.hdes.docdb.spi.codec.TreeEntryCodec;


public class DocDBCodecProvider implements CodecProvider {

  private final CommitCodec commit = new CommitCodec();
  private final BlobCodec blob = new BlobCodec();
  private final TreeEntryCodec treeEntry = new TreeEntryCodec();
  private final TreeCodec tree = new TreeCodec(treeEntry);
  private final TagCodec tag = new TagCodec();
  private final RefCodec ref = new RefCodec();
  private final RepoCodec repo = new RepoCodec();
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry)  {
    if(Repo.class.isAssignableFrom(clazz)) {
      return (Codec<T>) repo;
    }
    if(Commit.class.isAssignableFrom(clazz)) {
      return (Codec<T>) commit;
    }
    if(Blob.class.isAssignableFrom(clazz)) {
      return (Codec<T>) blob;
    }
    if(Tree.class.isAssignableFrom(clazz)) {
      return (Codec<T>) tree;
    }
    if(TreeValue.class.isAssignableFrom(clazz)) {
      return (Codec<T>) treeEntry;
    }
    if(Tag.class.isAssignableFrom(clazz)) {
      return (Codec<T>) tag;
    }
    if(Ref.class.isAssignableFrom(clazz)) {
      return (Codec<T>) ref;
    }
    return null;
  }
}
