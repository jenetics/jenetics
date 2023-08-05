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
package io.jenetics;

/**
 * Chromosome interface for {@code BoundedGene}s.
 *
 * @implSpec
 * Implementations of the {@code BoundedChromosome} interface must be
 * <em>immutable</em> and guarantee efficient random access ({@code O(1)}) to
 * the genes. A {@code Chromosome} must contains at least one {@code Gene}.
 *
 * @see BoundedGene
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 1.6
 */
public interface BoundedChromosome<
	A extends Comparable<? super A>,
	G extends BoundedGene<A, G>
>
	extends Chromosome<G>
{

	/**
	 * Return the minimum value of this {@code BoundedChromosome}.
	 *
	 * @return the minimum value of this {@code BoundedChromosome}.
	 */
	default A min() {
		return gene().min();
	}

	/**
	 * Return the maximum value of this {@code BoundedChromosome}.
	 *
	 * @return the maximum value of this {@code BoundedChromosome}.
	 */
	default A max() {
		return gene().max();
	}

}
