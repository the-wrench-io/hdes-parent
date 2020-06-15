package io.resys.hdes.compiler.dt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link ExpressionDT.ExpressionDTOut}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableExpressionDTOut.builder()}.
 */
@Generated(from = "ExpressionDT.ExpressionDTOut", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class ImmutableExpressionDTOut implements ExpressionDT.ExpressionDTOut {
  private final Collection<ExpressionDT.ExpressionDTOutputEntry> values;

  private ImmutableExpressionDTOut(Collection<ExpressionDT.ExpressionDTOutputEntry> values) {
    this.values = values;
  }

  /**
   * @return The value of the {@code values} attribute
   */
  @Override
  public Collection<ExpressionDT.ExpressionDTOutputEntry> getValues() {
    return values;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ExpressionDT.ExpressionDTOut#getValues() values} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for values
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableExpressionDTOut withValues(Collection<ExpressionDT.ExpressionDTOutputEntry> value) {
    if (this.values == value) return this;
    Collection<ExpressionDT.ExpressionDTOutputEntry> newValue = Objects.requireNonNull(value, "values");
    return new ImmutableExpressionDTOut(newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableExpressionDTOut} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableExpressionDTOut
        && equalTo((ImmutableExpressionDTOut) another);
  }

  private boolean equalTo(ImmutableExpressionDTOut another) {
    return values.equals(another.values);
  }

  /**
   * Computes a hash code from attributes: {@code values}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + values.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code ExpressionDTOut} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "ExpressionDTOut{"
        + "values=" + values
        + "}";
  }

  /**
   * Creates an immutable copy of a {@link ExpressionDT.ExpressionDTOut} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ExpressionDTOut instance
   */
  public static ImmutableExpressionDTOut copyOf(ExpressionDT.ExpressionDTOut instance) {
    if (instance instanceof ImmutableExpressionDTOut) {
      return (ImmutableExpressionDTOut) instance;
    }
    return ImmutableExpressionDTOut.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableExpressionDTOut ImmutableExpressionDTOut}.
   * <pre>
   * ImmutableExpressionDTOut.builder()
   *    .values(Collection&amp;lt;io.resys.hdes.compiler.dt.ExpressionDT.ExpressionDTOutputEntry&amp;gt;) // required {@link ExpressionDT.ExpressionDTOut#getValues() values}
   *    .build();
   * </pre>
   * @return A new ImmutableExpressionDTOut builder
   */
  public static ImmutableExpressionDTOut.Builder builder() {
    return new ImmutableExpressionDTOut.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableExpressionDTOut ImmutableExpressionDTOut}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "ExpressionDT.ExpressionDTOut", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_VALUES = 0x1L;
    private long initBits = 0x1L;

    private @Nullable Collection<ExpressionDT.ExpressionDTOutputEntry> values;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ExpressionDTOut} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(ExpressionDT.ExpressionDTOut instance) {
      Objects.requireNonNull(instance, "instance");
      values(instance.getValues());
      return this;
    }

    /**
     * Initializes the value for the {@link ExpressionDT.ExpressionDTOut#getValues() values} attribute.
     * @param values The value for values 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder values(Collection<ExpressionDT.ExpressionDTOutputEntry> values) {
      this.values = Objects.requireNonNull(values, "values");
      initBits &= ~INIT_BIT_VALUES;
      return this;
    }

    /**
     * Builds a new {@link ImmutableExpressionDTOut ImmutableExpressionDTOut}.
     * @return An immutable instance of ExpressionDTOut
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableExpressionDTOut build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableExpressionDTOut(values);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_VALUES) != 0) attributes.add("values");
      return "Cannot build ExpressionDTOut, some of required attributes are not set " + attributes;
    }
  }
}
