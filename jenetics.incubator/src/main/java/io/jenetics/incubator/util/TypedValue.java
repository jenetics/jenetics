/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.util;


import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.util.TypedValue.box;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;


/**
 * Instead of using {@link String}, {@link java.math.BigDecimal} or
 * {@link java.time.LocalDate} objects directly in an API, it is often better to
 * have wrapper classes for these values, which determines the concrete usage of
 * these values E.g. instead of {@link String} use a {@code UserId} type or
 * instead of a {@link java.math.BigDecimal} use a {@code Euro} class. The
 * {@link TypedValue} interface makes the intent for such wrapper/box types
 * clear and simplifies the value transformation and the {@code null}-handling.
 *
 * <h1>Description</h1>
 * When designing an API, using <em>raw</em> value types is often not good
 * enough. A value alone is most of the time ambiguous, because of the missing
 * semantic. E.g. a {@link String} can represent a username or a customer id.
 * The meaning of the values is lost, when using sole a string. To add meaning
 * to the value a wrapper {@code record} for the string value is created.
 * {@snippet lang=java:
 * record User(String value) implements TypedValue<String, User> {}
 * // or
 * record CustomerId(String value) implements TypedValue<String, CostumerId> {}
 * }
 * The {@link TypedValue} interface makes the purpose of the wrapper record clear.
 *
 * <h2>Instance creation</h2>
 * To general contract of {@link TypedValue} boxes is, that the {@link #value()}
 * itself is never {@code null}. If {@code null}-values are needed, the box itself
 * will be {@code null}. The drawback of this approach is, that the <em>boxing</em>
 * and <em>unboxing</em> of nullable values can be quite cumbersome.
 * {@snippet lang=java:
 * String value = null; // @replace substring='null' replacement="..."
 * User user = value != null ? new User(value) : null;
 * }
 * The same pattern has to be applied, when a potential nullable user object has
 * to be unboxed.
 * {@snippet lang=java:
 * User user = null; // @replace substring='null' replacement="..."
 * String value = user != null ? user.value() : null;
 * }
 * The static methods of the {@link TypedValue} form an API for extracting and
 * manipulating the boxed values in a null-safe manner.
 * <p><b>Null-safe boxing</b></p>
 * {@snippet lang=java:
 * String value = null; // @replace substring='null' replacement="..."
 * User user = box(value, User::new);
 * // or
 * User user = box(value, User.class);
 * }
 * <p><b>Null-safe unboxing</b></p>
 * {@snippet lang=java:
 * User user = null; // @replace substring='null' replacement="..."
 * String value = unbox(user);
 * }
 *
 * <h2>Value transformation</h2>
 * Existing boxed values can easily be changed with the {@link #with(Function)}
 * method, if the box isn't {@code null}.
 * {@snippet lang=java:
 * Millis ms = new Millis(1000L)
 * ms = ms.with(v -> v*10);
 * }
 * If the box might be {@code null}, the box value can be changed with the
 * {@link #map(Record, Function)} method.
 * {@snippet lang=java:
 * Millis ms = null; // @replace substring='null' replacement="..."
 * ms = map(ms, v -> v*10);
 * }
 * Or using the {@link #flatMap(Record, Function)}.
 * {@snippet lang=java:
 * Millis timeout = null; // @replace substring='null' replacement="..."
 * Millis offset = null; // @replace substring='null' replacement="..."
 * Millis ms = flatMap(offset, o -> map(timeout, t -> t + o));
 * }
 *
 * <h2>Combining values</h2>
 * The {@link #map(Record, Function)}, {@link #mapValue(Record, Function)} and
 * {@link #flatMap(Record, Function)} functions can be used for safely combining
 * several, possible {@code null}, typed values.
 * {@snippet lang=java:
 * record Ohm(Double value) implements TypedValue<Double, Ohm> {}
 * record Ampere(Double value) implements TypedValue<Double, Ampere> {}
 * record Volt(Double value) implements TypedValue<Double, Volt> {}
 * record Watt(Double value) implements TypedValue<Double, Watt> {}
 *
 * final Ohm resistance = box(3.4, Ohm::new);
 * final Ampere current = box(5.4, Ampere::new);
 *
 * final Volt volt = flatMap(resistance, r ->
 *         flatMap(current, c -> new Volt(r*c))
 *     );
 * final Watt watt = flatMap(volt, v ->
 *        map(current, c -> c*v, Watt::new)
 *     );
 * final Double value = mapValue(volt, v ->
 *         mapValue(current, c -> c*v)
 *     );
 * }
 *
 * <h2>Default values</h2>
 * <p><b>Returning a default value for a possible null box</b></p>
 * {@snippet lang=java:
 * User user = null; // @replace substring='null' replacement="..."
 * String name = orElse(user, "default-user");
 * }
 * <p><b>Returning a lazy default value for a possible null box</b></p>
 * {@snippet lang=java:
 * User user = null; // @replace substring='null' replacement="..."
 * String name = orElseGet(user, () -> "default-user");
 * }
 * <p><b>Returning another box for a possible null box</b></p>
 * {@snippet lang=java:
 * User defaultUser = new User("default-user");
 * User user = null; // @replace substring='null' replacement="..."
 * user = or(user, () -> defaultUser);
 * }
 *
 * @apiNote
 * <b>The boxed value type must never be {@code null}. Only the box type can be
 * {@code null}.</b>
 *
 * @param <V> the boxed type
 * @param <B> the box type
 */
public interface TypedValue<V, B extends Record & TypedValue<V, B>> {

	/**
	 * Return the boxed value, which will never be {@code null}. If a value
	 * needs to be {@code null}, the box type will be {@code null}.
	 *
	 * @return the boxed value
	 * @see #unbox(Record)
	 */
	V value();

	/**
	 * Creates a new box, with the given {@code fn} applied to the value. If the
	 * function returns {@code null}, the new box will be {@code null}.
	 *
	 * @see #map(Record, Function)
	 *
	 * @param fn the function to apply to the value
	 * @return the new box, or {@code null}
	 * @throws UnsupportedOperationException if the record box type, {@code B},
	 *         has no {@code value} component
	 */
	default @Nullable B with(Function<? super V, ? extends @Nullable V> fn) {
		@SuppressWarnings("unchecked")
		final var type = (Class<B>)getClass();
		return box(fn.apply(value()), type);
	}


	/* *************************************************************************
	 * Static methods from boxing and unboxing.
	 * ************************************************************************/

	/**
	 * Creates a box of the given {@code type} and the given {@code value}.
	 * If the value is {@code null}, the box will be {@code null} as well. Which
	 * is the main difference for just calling the constructor of the box type.
	 *
	 * @see #box(Object, Function)
	 *
	 * @param value the value to box
	 * @param type  the box type
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return a new boxed value, or {@code null}
	 * @throws UnsupportedOperationException if the record box type, {@code B},
	 *         has no {@code value} component
	 */
	static <V, B extends Record & TypedValue<V, B>>
	@Nullable B box(@Nullable V value, Class<B> type) {
		requireNonNull(type);

		if (value != null) {
			try {
				return type.getDeclaredConstructor(valueType(type))
					.newInstance(value);
			} catch (ReflectiveOperationException e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			return null;
		}
	}

	private static Class<?> valueType(Class<?> type) {
		for (var component : type.getRecordComponents()) {
			if (component.getName().equals("value")) {
				return component.getType();
			}
		}

		throw new UnsupportedOperationException(
			"'box' - Box type '%s' has no 'value' component."
				.formatted(type.getName())
		);
	}

	/**
	 * Creates a box of the given {@code type} and the given {@code value}.
	 * If the value is {@code null}, the box will be {@code null} as well. Which
	 * is the main difference for just calling the constructor of the box type.
	 *
	 * @see #box(Object, Class)
	 *
	 * @param value the value to box
	 * @param ctor the box constructor
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return a new boxed value, or {@code null}
	 */
	static <V, B extends Record & TypedValue<V, B>>
	@Nullable B box(@Nullable V value, Function<? super V, ? extends B> ctor) {
		requireNonNull(ctor);
		return value == null ? null : ctor.apply(value);
	}

	/**
	 * Returns the boxed value, or {@code null} if the box is {@code null}.
	 *
	 * @param box the boxed value
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return the unboxed value, or {@code null}
	 * @see #value()
	 */
	static <V, B extends Record & TypedValue<V, B>> @Nullable V unbox(@Nullable B box) {
		return box != null ? box.value() : null;
	}


	/* *************************************************************************
	 * Static methods from transforming the box values.
	 * ************************************************************************/

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @see #with(Function)
	 * @see #map(Record, Function, Class)
	 * @see #map(Record, Function, Function)
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return the transformed box
	 */
	static <V, B extends Record & TypedValue<V, B>> @Nullable B map(
		@Nullable B box,
		Function<? super V, ? extends @Nullable V> fn
	) {
		requireNonNull(fn);
		return box != null ? box.with(fn) : null;
	}

	/**
	 * Maps the values of a box type.
	 *
	 * @param box the box
	 * @param fn the value mapping function
	 * @return the mapped boxed value
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @param <V2> the mapped value
	 */
	static <V, B extends Record & TypedValue<V, B>, V2> @Nullable V2 mapValue(
		@Nullable B box,
		Function<? super V, ? extends @Nullable V2> fn
	) {
		requireNonNull(fn);
		return box != null ? fn.apply(box.value()) : null;
	}

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @see #map(Record, Function)
	 * @see #map(Record, Function, Function)
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param type the resulting box type
	 * @return the transformed box
	 * @param <V1> the boxed type
	 * @param <B1> the box type
	 * @param <V2> the mapped boxed type
	 * @param <B2> the mapped box type
	 */
	static <
		V1, B1 extends Record & TypedValue<V1, B1>,
		V2, B2 extends Record & TypedValue<V2, B2>
	>
	@Nullable B2 map(
		@Nullable B1 box,
		Function<? super V1, ? extends @Nullable V2> fn,
		Class<B2> type
	) {
		requireNonNull(fn);
		requireNonNull(type);

		return box != null
			? box(fn.apply(box.value()), type)
			: null;
	}

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @see #map(Record, Function)
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param ctor the box constructor
	 * @return the transformed box
	 * @param <V1> the boxed type
	 * @param <B1> the box type
	 * @param <V2> the mapped boxed type
	 * @param <B2> the mapped box type
	 */
	static <
		V1, B1 extends Record & TypedValue<V1, B1>,
		V2, B2 extends Record & TypedValue<V2, B2>
	>
	@Nullable B2 map(
		@Nullable B1 box,
		Function<? super V1, ? extends @Nullable V2> fn,
		Function<? super V2, ? extends B2> ctor
	) {
		requireNonNull(fn);
		requireNonNull(ctor);

		return box != null
			? box(fn.apply(box.value()), ctor)
			: null;
	}

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param <V1> the boxed type
	 * @param <B1> the box type
	 * @param <B2> the mapped box type
	 * @return the transformed box
	 */
	static <
		V1, B1 extends Record & TypedValue<V1, B1>,
		B2 extends Record & TypedValue<?, B2>
	>
	@Nullable B2 flatMap(
		@Nullable B1 box,
		Function<? super V1, ? extends @Nullable B2> fn
	) {
		requireNonNull(fn);
		return box != null ? fn.apply(box.value()) : null;
	}

	/**
	 * Converts the boxed value into an {@link Optional}.
	 *
	 * @param box the input box
	 * @return the boxed value, or {@link Optional#empty()} if the {@code box}
	 *         is null
	 * @param <V> the boxed type
	 * @param <B> the box type
	 */
	static <V, B extends Record & TypedValue<V, B>> Optional<V>
	toOptional(@Nullable B box) {
		return Optional.ofNullable(box).map(TypedValue::unbox);
	}

	/* *************************************************************************
	 * Static methods from returning default values and default boxes.
	 * ************************************************************************/

	/**
	 * Return a box of the {@code other} value, if the {@code box} is {@code null}.
	 *
	 * @param box   the box value
	 * @param other the other value
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return a new value box
	 */
	static <V, B extends Record & TypedValue<V, B>> V
	orElse(@Nullable B box, V other) {
		requireNonNull(other);
		return box != null ? box.value() : other;
	}

	/**
	 * Return a box of the {@code other} value, if the {@code box} is {@code null}.
	 *
	 * @param box   the box value
	 * @param other the other value
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return a new value box
	 */
	static <V, B extends Record & TypedValue<V, B>> V
	orElseGet(@Nullable B box, Supplier<? extends V> other) {
		requireNonNull(other);
		return box != null ? box.value() : other.get();
	}

	/**
	 * Return the {@code other} box, if the {@code box} is {@code null}.
	 *
	 * @param box   the box value
	 * @param other the other box
	 * @param <V> the boxed type
	 * @param <B> the box type
	 * @return a new value box
	 */
	static <V, B extends Record & TypedValue<V, B>> B
	or(@Nullable B box, Supplier<? extends B> other) {
		return box != null ? box : other.get();
	}
}

