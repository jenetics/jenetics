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

import org.jenetics.util.Factory;
import org.jenetics.util.Verifiable;

/**
 * Genes are the atoms of the <em>Jenetics</em> library. They contain the actual
 * information (alleles) of the encoded solution. All implementations of the
 * this interface are final, immutable and can be only created via static
 * factory methods which have the name {@code of}. When extending the library
 * with own {@code Gene} implementations, it is recommended to also implement it
 * as <a href="https://en.wikipedia.org/wiki/Value_object">value objects</a>.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Value_object">Value object</a>
 *
 * @param <A> the <a href="http://en.wikipedia.org/wiki/Allele">Allele</a> type
 *         of this gene.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.1
 */
public interface Gene<A, G extends Gene<A, G>>
	extends
		Factory<G>,
		Verifiable
{

	/**
	 * Return the allele of this gene.
	 *
	 * @return the allele of this gene.
	 */
	public A getAllele();

	/**
	 * Return a new, random gene with the same type and with the same constraints
	 * than this gene. For all genes returned by this method holds
	 * {@code gene.getClass() == gene.newInstance().getClass()}. Implementations
	 * of this method has to use the {@link java.util.Random} object which can
	 * be fetched from the {@link org.jenetics.util.RandomRegistry}.
	 */
	@Override
	public G newInstance();

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 2.0
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public G newInstance(final A value);

}
