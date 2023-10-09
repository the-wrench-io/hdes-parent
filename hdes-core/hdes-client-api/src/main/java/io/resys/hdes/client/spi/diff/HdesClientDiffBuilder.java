package io.resys.hdes.client.spi.diff;

import com.github.difflib.DiffUtils;
import io.resys.hdes.client.api.HdesClient.DiffBuilder;
import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.diff.ImmutableTagDiff;
import io.resys.hdes.client.api.diff.TagDiff;
import io.resys.hdes.client.spi.util.HdesAssert;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.difflib.UnifiedDiffUtils.generateUnifiedDiff;

@Slf4j
public class HdesClientDiffBuilder implements DiffBuilder {
  
  private static class DiffException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DiffException(String message) {
      super(message);
    }
  }

  private static final int NO_OF_CONTEXT_LINES = 2;
  private static final String LN = "\n";
  private static final String NEW_FILE_FLAG = "new file mode 100644"; 
  private static final String DELETED_FILE_FLAG = "deleted file mode 100644";

  private String baseId;
  private String targetId;
  private Collection<StoreEntity> tags;
  private LocalDateTime targetDate;


  public HdesClientDiffBuilder tags(Collection<StoreEntity> tags) {
    this.tags = tags;
    return this;
  }

  public HdesClientDiffBuilder baseId(String baseId) {
    this.baseId = baseId;
    return this;
  }
  
  public HdesClientDiffBuilder targetId(String targetId) {
    this.targetId = targetId;
    return this;
  }

  public HdesClientDiffBuilder targetDate(LocalDateTime targetDate) {
    this.targetDate = targetDate;
    return this;
  }

  @Override
  public TagDiff build() {
    HdesAssert.notNull(tags, () -> "tags must be defined!");
    HdesAssert.notEmpty(baseId, () -> "baseId must be defined!");
    HdesAssert.notEmpty(targetId, () -> "targetId must be defined!");
    HdesAssert.notNull(targetDate, () -> "targetDate must be defined!");

    final var baseTag = getTagById(tags, baseId);
    final var targetTag = getTagById(tags, targetId);

    final var baseAssets = getAssetsFromTag(baseTag);
    final var targetAssets = getAssetsFromTag(targetTag);

    final var diffBody = new StringBuilder();

    for (final var baseAsset : baseAssets) {
      final var baseName = getName(baseAsset);
      final var baseLines = getLines(baseAsset);
      final var targetAsset = findMatchById(targetAssets, baseAsset);
      if (targetAsset.isEmpty()) {
        final var diff = String.join(LN, generateDiff(baseLines, Collections.emptyList(), baseName, null));
        diffBody.append(LN).append(diff).append(LN).append(DELETED_FILE_FLAG);
        continue;
      }
      final var targetName = getName(targetAsset.get());
      final var targetLines = getLines(targetAsset.get());
      final var diff = String.join(LN, generateDiff(baseLines, targetLines, baseName, targetName));
      diffBody.append(LN).append(diff);
    }

    final var newAssets = targetAssets.stream()
        .filter(t -> findMatchById(baseAssets, t).isEmpty())
        .collect(Collectors.toList());

    for (final var newAsset : newAssets) {
      final var targetName = getName(newAsset);
      final var targetLines = getLines(newAsset);
      final var diff = String.join(LN, generateDiff(Collections.emptyList(), targetLines, null, targetName));
      diffBody.append(LN).append(diff).append(LN).append(NEW_FILE_FLAG);
    }

    return ImmutableTagDiff.builder()
        .baseId(baseId)
        .targetId(targetId)
        .created(targetDate)
        .baseName(getNameFromTag(baseTag))
        .targetName(getNameFromTag(targetTag))
        .body(diffBody.toString())
        .build();
  }

  private List<AstCommand> getAssetsFromTag(StoreEntity entity) {
    return entity.getBody().stream()
        .filter(c -> c.getType().equals(AstCommandValue.SET_TAG_DT) ||
            c.getType().equals(AstCommandValue.SET_TAG_FL) ||
            c.getType().equals(AstCommandValue.SET_TAG_ST))
        .collect(Collectors.toList());
  }

  private String getNameFromTag(StoreEntity entity) {
    return entity.getBody().stream()
        .filter(c -> c.getType().equals(AstCommandValue.SET_TAG_NAME))
        .findFirst()
        .orElseThrow(() -> new DiffException("Tag does not have a name!"))
        .getValue();
  }

  private List<String> getLines(AstCommand value) {
    if (value.getValue() == null) {
      return Collections.emptyList();
    }
    if (value.getType().equals(AstCommandValue.SET_TAG_DT)) {
      return List.of(value.getValue().split("},\\{"));
    }
    return value.getValue().lines().collect(Collectors.toUnmodifiableList());
  }

  private String getName(AstCommand value) {
    switch (value.getType()) {
      case SET_TAG_FL:
        return "flows/" + matchName(value, "id: (\\w+)");
      case SET_TAG_ST:
        return "services/" + matchName(value, "public class (\\w+)");
      case SET_TAG_DT:
        return "decisions/" + matchName(value, "\"value\":\"(\\w+)\",\"type\":\"SET_NAME\"\\},\\{");
      default:
        return null;
    }
  }

  private Optional<AstCommand> findMatchById(List<AstCommand> targetValues, AstCommand baseValue) {
    return targetValues.stream().filter(v -> Objects.equals(v.getId(), baseValue.getId())).findFirst();
  }

  private String matchName(AstCommand value, String regex) {
    final var body = value.getValue();
    if (body == null) {
      return null;
    }
    final var matcher = Pattern.compile(regex).matcher(body);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private StoreEntity getTagById(Collection<StoreEntity> tags, String id) {
    if (tags.size() > 0) {
      return tags.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow(() -> new DiffException("Tag not found!"));
    } else {
      throw new DiffException("No tags yet!");
    }
  }

  private List<String> generateDiff(List<String> baseLines, List<String> targetLines, String baseName, String targetName) {
    final var patch = DiffUtils.diff(baseLines, targetLines);
    return generateUnifiedDiff(baseName, targetName, baseLines, patch, NO_OF_CONTEXT_LINES);
  }

}
