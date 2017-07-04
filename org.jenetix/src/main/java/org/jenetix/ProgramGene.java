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
package org.jenetix;

import static java.util.Objects.requireNonNull;

import org.jenetics.Gene;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ProgramGene<A> implements Gene<Op<A>, ProgramGene<A>> {

	private final Op<A> _op;
	private final ISeq<? extends Op<A>> _ops;

	public ProgramGene(final Op<A> op, final ISeq<? extends Op<A>> ops) {
		_op = requireNonNull(op);
		_ops = requireNonNull(ops);
	}

	public A apply(final A[] values) {
		return values[0];
	}

	public A eval(final A[] variables) {
		final MSeq<A> values = MSeq.ofLength(arity());
		for (int i = 0; i < arity(); ++i) {
			values.set(i, getChild(i).eval(variables));
		}

		return apply((A[])values.toArray());
	}

	public ProgramGene<A> getChild(final int index) {
		return null;
	}

	public ISeq<? extends Op<A>> getOps() {
		return _ops;
	}

	public int arity() {
		return _op.arity();
	}

	public IntRange arityRange() {
		return IntRange.of(0, 10);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public Op<A> getAllele() {
		return _op;
	}

	@Override
	public ProgramGene<A> newInstance() {
		return null;
	}

	@Override
	public ProgramGene<A> newInstance(Op<A> value) {
		return null;
	}
}
