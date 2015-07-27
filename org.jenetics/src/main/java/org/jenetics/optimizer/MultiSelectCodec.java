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
package org.jenetics.optimizer;

import java.util.function.Function;

import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MultiSelectCodec<T> implements Codec<ISeq<? extends T>, DoubleGene> {

	private final ISeq<Codec<T, DoubleGene>> _codecs;

	public MultiSelectCodec(final ISeq<Codec<T, DoubleGene>> codecs) {
		_codecs = codecs;
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return null;
	}

	@Override
	public Function<Genotype<DoubleGene>, ISeq<? extends T>> decoder() {
		return null;
	}

}
