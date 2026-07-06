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

import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static io.jenetics.incubator.util.TypedValue.box;
import static io.jenetics.incubator.util.TypedValue.flatMap;
import static io.jenetics.incubator.util.TypedValue.map;
import static io.jenetics.incubator.util.TypedValue.or;
import static io.jenetics.incubator.util.TypedValue.orElse;
import static io.jenetics.incubator.util.TypedValue.orElseGet;
import static io.jenetics.incubator.util.TypedValue.unbox;
import static java.util.Objects.requireNonNull;

/**
 * Interface for <em>typed</em> values. Instead of building APIs which only
 * works with <em>values</em>, like {@link String}, {@link Integer} or
 * {@link java.time.LocalDate}, and helps to enrich the value with type
 * information. It is essentially a <em>box</em> around a <em>primitive</em>
 * value. With the {@link #box(Object, Class)} method, a box of a given type can
 * be created, which is unboxed with the {@link #unbox(Record)} method.
 *
 * <h1>Problem</h1>
 * When designing a good API, it is not sufficient to have only the values. A value
 * only transports the value without semantic. Classes (Types) with proper names
 * transport semantic. APIs should use a type instead of a value only. Makes the
 * usage of the API safer. Avoid stringified APIs. When using wrapper types, the
 * nullable problem becomes apparent. The value of a typed-value can never be null.
 * If a null value for a parameter is needed, the wrapper type or box type must
 * be null. If you have a null value and have to box it, there is some boilerplate
 * necessary when the value needs to be boxed. The same is true for unboxing the
 * value. The Java language has no native box/unbox feature, which also handles
 * null values gracefully. The unbox method can't be an instance method. When it
 * is needed to handle null values/boxes gracefully, static methods must be implemented.
 * The main point is to design an API of static methods, which can handle nulls
 * correctly.
 * The second problem is the multistep boxing/unboxing. Boxing and unboxing hierarchically.
 * Should this problem be addressed by this interface?
 * A box is a higher kinded type?
 *
 * @param <V> the underlying, boxed value type
 * @param <B> the <em>box</em> type
 * @apiNote The central metaphor of the {@code Typedvalue} interface ist <em>boxing</em>
 * and <em>unboxing</em>. To make use of the actual value, it must be unboxed.
 * The main purpose is therefore to make the APIs (method- and class arguments)
 * safer.
 */
public interface TypedValue<V, B extends Record & TypedValue<V, B>> {

	/**
	 * Creates a box of the given {@code type} and the given {@code value}.
	 * If the value is {@code null}, the box will be {@code null} as well. Which
	 * is the main difference for just calling the constructor of the box type.
	 *
	 * @param value the value to box
	 * @param type  the box type
	 * @param <V>   the underlying, boxed value type
	 * @param <B>   the <em>box</em> type
	 * @return a new boxed value, or {@code null}
	 */
	static <V, B extends Record & TypedValue<V, B>>
	@Nullable B box(@Nullable V value, Class<B> type) {
		requireNonNull(type);
		if (type.isInterface()) {
			throw new IllegalArgumentException(
				"Box type '%s' is an interface".formatted(type.getName())
			);
		}

		if (value == null) {
			return null;
		}

		try {
			return type.getDeclaredConstructor(valueType(type))
				.newInstance(value);
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Class<?> valueType(Class<?> type) {
		for (var component : type.getRecordComponents()) {
			if (component.getName().equals("value")) {
				return component.getType();
			}
		}

		throw new IllegalArgumentException(
			"Box type '%s' has no 'value' component.".formatted(type.getName())
		);
	}

	/* *************************************************************************
	 * Static methods that form the API for the typed-value usage. These methods
	 * gracefully handle {@code null} values.
	 * ************************************************************************/

	/**
	 * Returns the boxed value, or {@code null} if the box is {@code null}.
	 *
	 * @param box the boxed value
	 * @param <V> the underlying, boxed value type
	 * @param <B> the <em>box</em> type
	 * @return the unboxed value, or {@code null}
	 * @see #value()
	 */
	static <V, B extends Record & TypedValue<V, B>> @Nullable V unbox(@Nullable B box) {
		return box != null ? box.value() : null;
	}

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param <V> the underlying, boxed value type
	 * @param <B> the <em>box</em> type
	 * @return the transformed box
	 * @see #with(Function)
	 */
	static <V, B extends Record & TypedValue<V, B>> @Nullable B map(
		@Nullable B box,
		Function<? super V, ? extends @Nullable V> fn
	) {
		requireNonNull(fn);
		return box != null ? box.with(fn) : null;
	}

	/**
	 * Transforms value of the given {@code box}. If the box is {@code null},
	 * {@code null} is returned without calling the transformation function.
	 *
	 * @param box the box value
	 * @param fn  the transformation function
	 * @param <V> the underlying, boxed value type
	 * @param <B> the <em>box</em> type
	 * @return the transformed box
	 */
	static <V, B extends Record & TypedValue<V, B>> @Nullable B flatMap(
		@Nullable B box,
		Function<? super V, ? extends @Nullable B> fn
	) {
		requireNonNull(fn);
		return box != null ? fn.apply(box.value()) : null;
	}

	/**
	 * Return a box of the {@code other} value, if the {@code box} is {@code null}.
	 *
	 * @param box   the box value
	 * @param other the other value
	 * @param <V>   the underlying, boxed value type
	 * @param <B>   the <em>box</em> type
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
	 * @param <V>   the underlying, boxed value type
	 * @param <B>   the <em>box</em> type
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
	 * @param <V>   the underlying, boxed value type
	 * @param <B>   the <em>box</em> type
	 * @return a new value box
	 */
	static <V, B extends Record & TypedValue<V, B>> B
	or(@Nullable B box, Supplier<? extends B> other) {
		return box != null ? box : other.get();
	}

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
	 * @param fn the function to apply to the value
	 * @return the new box, or {@code null}
	 */
	@SuppressWarnings("unchecked")
	default @Nullable B with(Function<? super V, ? extends @Nullable V> fn) {
		return box(fn.apply(value()), (Class<B>)getClass());
	}


}

interface TypedValue2<V, B1 extends Record & TypedValue<V, B1>, B2 extends Record & TypedValue2<V, B1, B2>> {
	B1 box1();
}

record Meter(Double value) implements TypedValue<Double, Meter> {
}

record Distance(Meter value) implements TypedValue<Meter, Distance> {
}

record ViennaToZurichDistance(Distance value)
	implements TypedValue<Distance, ViennaToZurichDistance> {
}

record MilliSecond(Long value) implements TypedValue<Long, MilliSecond> {
}

final class Main {

	static void sleep(MilliSecond time) throws InterruptedException {
		Thread.sleep(time.value());
	}

	static void main() throws Exception {

		var dist = box(box(3.4, Meter.class), Distance.class);
		var fooo = new Distance(new Meter(3.4));
		dist = dist.with(m -> m.with(v -> v * 2.0));

		Double val = unbox(unbox(dist));
		dist.value().value();


		Thread.sleep(1000);
		sleep(new MilliSecond(1000L));
		sleep(box(1000L, MilliSecond.class));


		Meter length = box(4.0, Meter.class);
		Meter distant = box(6.5, Meter.class);

		// Value transformation of instance value.
		length = length.with(v -> 5 * v);

		// Access value from instance.
		double v1 = length.value();

		// Null-safe value access (unboxing).
		Double v2 = unbox(length);

		// Null-safe transformation.
		length = map(length, v -> 4 * v);
		length = flatMap(length, v -> box(v * 6.5, Meter.class));

		// Null-safe access if box is null.
		length = or(length, () -> distant);
		var lengthValue = orElse(length, 6.5);
		lengthValue = orElseGet(length, Math::random);

	}
}
