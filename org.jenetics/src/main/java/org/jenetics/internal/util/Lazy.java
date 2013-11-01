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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Implements an lazily evaluated value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-01 $</em>
 * @since @__version__@
 */
public abstract class Lazy<T> implements Supplier<T> {

	private Lazy() {
	}

	public static <T> Lazy<T> valueOf(final Supplier<T> supplier) {
		return new Value<>(supplier);
	}

	private static final class Value<T> extends Lazy<T> {
		private final Supplier<T> _supplier;

		private boolean _evaluated = false;
		private T _value;

		private Value(final Supplier<T> supplier) {
			_supplier = Objects.requireNonNull(supplier);
		}

		@Override
		public T get() {
			return _evaluated ? _value : evaluate();
		}

		private T evaluate() {
			if (!_evaluated) {
				_value = _supplier.get();
				_evaluated = true;
			}

			return _value;
		}
	}

	private static final class Safe<T> extends Lazy<T> {
		private final Supplier<T> _supplier;

		private volatile boolean _evaluated = false;
		private T _value;

		private Safe(final Supplier<T> supplier) {
			_supplier = Objects.requireNonNull(supplier);
		}

		@Override
		public T get() {
			return _evaluated ? _value : evaluate();
		}

		private synchronized T evaluate() {
			if (!_evaluated) {
				_value = _supplier.get();
				_evaluated = true;
			}

			return _value;
		}
	}

}
