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
package io.jenetics.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Either<A, B> {

	private final A _left;
	private final B _right;

	private Either(final A left, final B right) {
		_left = left;
		_right = right;
	}

	public boolean isLeft() {
		return _left != null;
	}

	public boolean isRight() {
		return _right != null;
	}

	public Optional<A> left() {
		return Optional.ofNullable(_left);
	}

	public A leftValue() {
		if (!isLeft()) {
			throw new NoSuchElementException("No left value.");
		}
		return _left;
	}

	public Optional<B> right() {
		return Optional.ofNullable(_right);
	}

	public B rightValue() {
		if (!isRight()) {
			throw new NoSuchElementException("No right value.");
		}
		return _right;
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(_left)^Objects.hashCode(_right);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Either &&
			Objects.equals(_left, ((Either)obj)._left) &&
			Objects.equals(_right, ((Either)obj)._right);
	}

	@Override
	public String toString() {
		return isLeft()
			? format("Left[%s]", _left)
			: format("Right[%s]", _right);
	}

	public static <A, B> Either<A, B> left(final A value) {
		return new Either<>(requireNonNull(value), null);
	}

	public static <A, B> Either<A, B> right(final B value) {
		return new Either<>(null, requireNonNull(value));
	}

}
