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
 * Immutable implementation of {@link ExpressionDT.ExpressionDTIn}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableExpressionDTIn.builder()}.
 */
@Generated(from = "ExpressionDT.ExpressionDTIn", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class ImmutableExpressionDTIn implements ExpressionDT.ExpressionDTIn {
  private final Integer value0;
  private final Integer value1;

  private ImmutableExpressionDTIn(Integer value0, Integer value1) {
    this.value0 = value0;
    this.value1 = value1;
  }

  /**
   * @return The value of the {@code value0} attribute
   */
  @Override
  public Integer getValue0() {
    return value0;
  }

  /**
   * @return The value of the {@code value1} attribute
   */
  @Override
  public Integer getValue1() {
    return value1;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ExpressionDT.ExpressionDTIn#getValue0() value0} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for value0
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableExpressionDTIn withValue0(Integer value) {
    Integer newValue = Objects.requireNonNull(value, "value0");
    if (this.value0.equals(newValue)) return this;
    return new ImmutableExpressionDTIn(newValue, this.value1);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ExpressionDT.ExpressionDTIn#getValue1() value1} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for value1
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableExpressionDTIn withValue1(Integer value) {
    Integer newValue = Objects.requireNonNull(value, "value1");
    if (this.value1.equals(newValue)) return this;
    return new ImmutableExpressionDTIn(this.value0, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableExpressionDTIn} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableExpressionDTIn
        && equalTo((ImmutableExpressionDTIn) another);
  }

  private boolean equalTo(ImmutableExpressionDTIn another) {
    return value0.equals(another.value0)
        && value1.equals(another.value1);
  }

  /**
   * Computes a hash code from attributes: {@code value0}, {@code value1}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + value0.hashCode();
    h += (h << 5) + value1.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code ExpressionDTIn} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "ExpressionDTIn{"
        + "value0=" + value0
        + ", value1=" + value1
        + "}";
  }

  /**
   * Creates an immutable copy of a {@link ExpressionDT.ExpressionDTIn} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ExpressionDTIn instance
   */
  public static ImmutableExpressionDTIn copyOf(ExpressionDT.ExpressionDTIn instance) {
    if (instance instanceof ImmutableExpressionDTIn) {
      return (ImmutableExpressionDTIn) instance;
    }
    return ImmutableExpressionDTIn.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableExpressionDTIn ImmutableExpressionDTIn}.
   * <pre>
   * ImmutableExpressionDTIn.builder()
   *    .value0(Integer) // required {@link ExpressionDT.ExpressionDTIn#getValue0() value0}
   *    .value1(Integer) // required {@link ExpressionDT.ExpressionDTIn#getValue1() value1}
   *    .build();
   * </pre>
   * @return A new ImmutableExpressionDTIn builder
   */
  public static ImmutableExpressionDTIn.Builder builder() {
    return new ImmutableExpressionDTIn.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableExpressionDTIn ImmutableExpressionDTIn}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "ExpressionDT.ExpressionDTIn", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_VALUE0 = 0x1L;
    private static final long INIT_BIT_VALUE1 = 0x2L;
    private long initBits = 0x3L;

    private @Nullable Integer value0;
    private @Nullable Integer value1;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ExpressionDTIn} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(ExpressionDT.ExpressionDTIn instance) {
      Objects.requireNonNull(instance, "instance");
      value0(instance.getValue0());
      value1(instance.getValue1());
      return this;
    }

    /**
     * Initializes the value for the {@link ExpressionDT.ExpressionDTIn#getValue0() value0} attribute.
     * @param value0 The value for value0 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder value0(Integer value0) {
      this.value0 = Objects.requireNonNull(value0, "value0");
      initBits &= ~INIT_BIT_VALUE0;
      return this;
    }

    /**
     * Initializes the value for the {@link ExpressionDT.ExpressionDTIn#getValue1() value1} attribute.
     * @param value1 The value for value1 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder value1(Integer value1) {
      this.value1 = Objects.requireNonNull(value1, "value1");
      initBits &= ~INIT_BIT_VALUE1;
      return this;
    }

    /**
     * Builds a new {@link ImmutableExpressionDTIn ImmutableExpressionDTIn}.
     * @return An immutable instance of ExpressionDTIn
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableExpressionDTIn build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableExpressionDTIn(value0, value1);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_VALUE0) != 0) attributes.add("value0");
      if ((initBits & INIT_BIT_VALUE1) != 0) attributes.add("value1");
      return "Cannot build ExpressionDTIn, some of required attributes are not set " + attributes;
    }
  }
}
