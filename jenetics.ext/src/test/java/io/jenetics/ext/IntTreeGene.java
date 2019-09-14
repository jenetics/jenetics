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
package io.jenetics.ext;

import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class IntTreeGene extends AbstractTreeGene<Integer, IntTreeGene> {

	private static final long serialVersionUID = 1L;

	public IntTreeGene(
		final Integer allele,
		final int childOffset,
		final int childCount
	) {
		super(allele, childOffset, childCount);
	}

	@Override
	public IntTreeGene newInstance() {
		return newInstance(RandomRegistry.getRandom().nextInt(10000));
	}

	@Override
	public IntTreeGene newInstance(final Integer value) {
		return newInstance(value, childOffset(), childCount());
	}

	@Override
	public IntTreeGene newInstance(
		final Integer allele,
		final int childOffset,
		final int childCount
	) {
		return new IntTreeGene(allele, childOffset, childCount);
	}
}
