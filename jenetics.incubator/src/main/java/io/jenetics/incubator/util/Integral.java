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

import java.time.LocalDate;
import java.util.Comparator;

/**
 * <em>Type class</em>, which allows an object of type {@code T} to be treated
 * as an <em>integral</em> value.
 *
 * @param <T> the integral type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public interface Integral<T> extends Comparator<T> {

	Integral<LocalDate> LOCAL_DATE = new Integral<>() {
		@Override
		public LocalDate min() {
			return LocalDate.MIN;
		}
		@Override
		public LocalDate next(LocalDate value) {
			return value.plusDays(1);
		}
		@Override
		public int compare(LocalDate a, LocalDate b) {
			return a.compareTo(b);
		}
	};

	Integral<Integer> INTEGER = new Integral<>() {
		@Override
		public Integer min() {
			return Integer.MIN_VALUE;
		}
		@Override
		public Integer next(Integer value) {
			return value + 1;
		}
		@Override
		public int compare(Integer a, Integer b) {
			return a.compareTo(b);
		}
	};

	T min();

	T next(T value);

	default boolean isBefore(T a, T b) {
		return compare(a, b) < 0;
	}

	default boolean isAfter(T a, T b) {
		return compare(a, b) > 0;
	}
}
