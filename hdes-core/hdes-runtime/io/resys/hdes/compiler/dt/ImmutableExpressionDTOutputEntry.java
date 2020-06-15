package io.resys.hdes.compiler.dt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link ExpressionDT.ExpressionDTOutputEntry}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableExpressionDTOutputEntry.builder()}.
 */
@Generated(from = "ExpressionDT.ExpressionDTOutputEntry", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class ImmutableExpressionDTOutputEntry
    implements ExpressionDT.ExpressionDTOutputEntry {
  private final Integer value;

  private ImmutableExpressionDTOutputEntry(Integer value) {
    this.value = value;
  }

  /**
   * @return The value of the {@code value} attribute
   */
  @Override
  public Integer getValue() {
    return value;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ExpressionDT.ExpressionDTOutputEntry#getValue() value} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for value
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableExpressionDTOutputEntry withValue(Integer value) {
    Integer newValue = Objects.requireNonNull(value, "value");
    if (this.value.equals(newValue)) return this;
    return new ImmutableExpressionDTOutputEntry(newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableExpressionDTOutputEntry} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableExpressionDTOutputEntry
        && equalTo((ImmutableExpressionDTOutputEntry) another);
  }

  private boolean equalTo(ImmutableExpressionDTOutputEntry another) {
    return value.equals(another.value);
  }

  /**
   * Computes a hash code from attributes: {@code value}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + value.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code ExpressionDTOutputEntry} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "ExpressionDTOutputEntry{"
        + "value=" + value
        + "}";
  }

  /**
   * Creates an immutable copy of a {@link ExpressionDT.ExpressionDTOutputEntry} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ExpressionDTOutputEntry instance
   */
  public static ImmutableExpressionDTOutputEntry copyOf(ExpressionDT.ExpressionDTOutputEntry instance) {
    if (instance instanceof ImmutableExpressionDTOutputEntry) {
      return (ImmutableExpressionDTOutputEntry) instance;
    }
    return ImmutableExpressionDTOutputEntry.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableExpressionDTOutputEntry ImmutableExpressionDTOutputEntry}.
   * <pre>
   * ImmutableExpressionDTOutputEntry.builder()
   *    .value(Integer) // required {@link ExpressionDT.ExpressionDTOutputEntry#getValue() value}
   *    .build();
   * </pre>
   * @return A new ImmutableExpressionDTOutputEntry builder
   */
  public static ImmutableExpressionDTOutputEntry.Builder builder() {
    return new ImmutableExpressionDTOutputEntry.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableExpressionDTOutputEntry ImmutableExpressionDTOutputEntry}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "ExpressionDT.ExpressionDTOutputEntry", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_VALUE = 0x1L;
    private long initBits = 0x1L;

    private @Nullable Integer value;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ExpressionDTOutputEntry} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(ExpressionDT.ExpressionDTOutputEntry instance) {
      Objects.requireNonNull(instance, "instance");
      value(instance.getValue());
      return this;
    }

    /**
     * Initializes the value for the {@link ExpressionDT.ExpressionDTOutputEntry#getValue() value} attribute.
     * @param value The value for value 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder value(Integer value) {
      this.value = Objects.requireNonNull(value, "value");
      initBits &= ~INIT_BIT_VALUE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableExpressionDTOutputEntry ImmutableExpressionDTOutputEntry}.
     * @return An immutable instance of ExpressionDTOutputEntry
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableExpressionDTOutputEntry build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableExpressionDTOutputEntry(value);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_VALUE) != 0) attributes.add("value");
      return "Cannot build ExpressionDTOutputEntry, some of required attributes are not set " + attributes;
    }
  }
}
