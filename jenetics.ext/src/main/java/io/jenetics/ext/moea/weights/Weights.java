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
package io.jenetics.ext.moea.weights;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.util.ISeq;

import io.jenetics.ext.moea.Vec;

/**
 * Methods for calculating reference points (weights) on the hyper-plane.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Weights(ISeq<Vec<double[]>> values)
	implements Iterable<Vec<double[]>>
{

	public int size() {
		return values().size();
	}

	public Vec<double[]> get(final int index) {
		return values.get(index);
	}

	@Override
	public Iterator<Vec<double[]>> iterator() {
		return values.iterator();
	}

	public Stream<Vec<double[]>> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public static Weights of(final List<double[]> weights) {
		return new Weights(ISeq.of(weights.stream().map(Vec::of).toList()));
	}

}
