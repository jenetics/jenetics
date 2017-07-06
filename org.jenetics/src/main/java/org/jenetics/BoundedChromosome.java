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
package org.jenetics;

/**
 * Chromosome interface for {@code BoundedGene}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6
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
	public A getMin();

	/**
	 * Return the maximum value of this {@code BoundedChromosome}.
	 *
	 * @return the maximum value of this {@code BoundedChromosome}.
	 */
	public A getMax();

}
