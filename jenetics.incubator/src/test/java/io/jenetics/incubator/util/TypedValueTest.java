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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static io.jenetics.incubator.util.TypedValue.box;
import static io.jenetics.incubator.util.TypedValue.flatMap;
import static io.jenetics.incubator.util.TypedValue.map;
import static io.jenetics.incubator.util.TypedValue.or;
import static io.jenetics.incubator.util.TypedValue.orElse;
import static io.jenetics.incubator.util.TypedValue.orElseGet;
import static io.jenetics.incubator.util.TypedValue.unbox;

import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.Test;

public class TypedValueTest {

	record Meter(Double value) implements TypedValue<Double, Meter> {
	}

	record Distance(Meter value) implements TypedValue<Meter, Distance> {
	}

	record NumberValue(Number value) implements TypedValue<Number, NumberValue> {
	}

	record BrokenValue(String value, String other)
		implements TypedValue<String, BrokenValue>
	{
	}

	@Test
	public void boxValue() {
		final Meter meter = box(12.5, Meter.class);

		assertThat(meter).isEqualTo(new Meter(12.5));
		assertThat(meter.value()).isEqualTo(12.5);
	}

	@Test
	public void boxNullValue() {
		assertThat(box(null, Meter.class)).isNull();
	}

	@Test
	public void boxDeclaredValueType() {
		final NumberValue value = box(23, NumberValue.class);

		assertThat(value).isEqualTo(new NumberValue(23));
		assertThat(value.value()).isInstanceOf(Integer.class);
	}

	@Test
	public void boxWithNullType() {
		assertThatNullPointerException()
			.isThrownBy(() -> box(12.5, (Class<Meter>)null));
	}

	@Test
	public void boxWithMissingValueConstructor() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> box("foo", BrokenValue.class));
	}

	@Test
	public void unboxValue() {
		assertThat(unbox(new Meter(12.5))).isEqualTo(12.5);
	}

	@Test
	public void unboxNullValue() {
		final Double value = unbox(null);

		assertThat(value).isNull();
	}

	@Test
	public void unboxNestedValue() {
		final var distance = box(box(12.5, Meter.class), Distance.class);

		assertThat(unbox(unbox(distance))).isEqualTo(12.5);
	}

	@Test
	public void withValue() {
		assertThat(new Meter(12.5).with(value -> value * 2))
			.isEqualTo(new Meter(25.0));
	}

	@Test
	public void withNullResult() {
		assertThat(new Meter(12.5).with(_ -> null)).isNull();
	}

	@Test
	public void mapValue() {
		assertThat(map(new Meter(12.5), value -> value * 2))
			.isEqualTo(new Meter(25.0));
	}

	@Test
	public void mapNullValue() {
		final var called = new AtomicBoolean();

		final Meter result = TypedValue.<Double, Meter>map(null, _ -> {
			called.set(true);
			return 12.5;
		});

		assertThat(result).isNull();
		assertThat(called.get()).isFalse();
	}

	@Test
	public void mapWithNullFunction() {
		assertThatNullPointerException()
			.isThrownBy(() -> map(new Meter(12.5), null));
	}

	@Test
	public void flatMapValue() {
		final var result = flatMap(new Meter(12.5), value -> box(value * 2, Meter.class));
		assertThat(result).isEqualTo(new Meter(25.0));
	}

	@Test
	public void flatMapNullValue() {
		final var called = new AtomicBoolean();

		final Meter result = TypedValue.flatMap(null, _ -> {
			called.set(true);
			return new Meter(12.5);
		});

		assertThat(result).isNull();
		assertThat(called.get()).isFalse();
	}

	@Test
	public void flatMapWithNullFunction() {
		assertThatNullPointerException()
			.isThrownBy(() -> flatMap(new Meter(12.5), null));
	}

	@Test
	public void orElseValue() {
		assertThat(orElse(new Meter(12.5), 23.0)).isEqualTo(12.5);
	}

	@Test
	public void orElseNullValue() {
		assertThat(orElse(null, 23.0)).isEqualTo(23.0);
	}

	@Test
	public void orElseWithNullOther() {
		assertThatNullPointerException()
			.isThrownBy(() -> orElse(new Meter(12.5), null));
	}

	@Test
	public void orElseGetValue() {
		final var called = new AtomicBoolean();

		assertThat(orElseGet(new Meter(12.5), () -> {
			called.set(true);
			return 23.0;
		})).isEqualTo(12.5);
		assertThat(called.get()).isFalse();
	}

	@Test
	public void orElseGetNullValue() {
		assertThat(orElseGet(null, () -> 23.0)).isEqualTo(23.0);
	}

	@Test
	public void orElseGetWithNullSupplier() {
		assertThatNullPointerException()
			.isThrownBy(() -> orElseGet(new Meter(12.5), null));
	}

	@Test
	public void orValue() {
		final var called = new AtomicBoolean();
		final var meter = new Meter(12.5);

		assertThat(or(meter, () -> {
			called.set(true);
			return new Meter(23.0);
		})).isSameAs(meter);
		assertThat(called.get()).isFalse();
	}

	@Test
	public void orNullValue() {
		final var meter = new Meter(23.0);

		assertThat(or(null, () -> meter)).isSameAs(meter);
	}

}


