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
package io.jenetics.prog.op;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Float.floatToIntBits;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * This is the <em>sealed</em> base class for unmodifiable values. The only
 * subclasses of this type are {@link Const} and {@link EphemeralConst}.
 *
 * @see Const
 * @see EphemeralConst
 *
 * @param <T> the type of the constant value
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 5.0
 */
public abstract sealed class Val<T>
	implements Op<T>
	permits Const, EphemeralConst
{

	private final String _name;

	Val(final String name) {
		_name = name;
	}

	@Override
	public final String name() {
		return _name;
	}

	/**
	 * Return the constant value.
	 *
	 * @return the constant value
	 */
	public abstract T value();

	/**
	 * The apply method will always return the {@link #value()}.
	 *
	 * @param value the input parameters will be ignored
	 * @return always {@link #value()}
	 */
	@Override
	public final T apply(final T[] value) {
		return value();
	}

	/**
	 * The arity of {@code Val} objects is always zero.
	 *
	 * @return always zero
	 */
	@Override
	public final int arity() {
		return 0;
	}

	@Override
	public final int hashCode() {
		return Objects.hashCode(value());
	}

	@Override
	public final boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Val<?> other &&
			equals(other.value(), value());
	}

	private static boolean equals(final Object a, final Object b) {
		if (a instanceof Double aa && b instanceof Double bb) {
			return doubleToLongBits(aa) == doubleToLongBits(bb);
		} else if (a instanceof Float aa && b instanceof Float bb) {
			return floatToIntBits(aa) == floatToIntBits(bb);
		} else if (a instanceof BigDecimal aa && b instanceof BigDecimal bb) {
			return aa.compareTo(bb) == 0;
		} else if (a instanceof BigInteger aa && b instanceof BigInteger bb) {
			return aa.compareTo(bb) == 0;
		}

		return Objects.equals(a, b);
	}

}
