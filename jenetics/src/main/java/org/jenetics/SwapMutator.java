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

import static java.lang.String.format;
import static org.jenetics.internal.math.random.indexes;

import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * The {@code SwapMutation} changes the order of genes in a chromosome, with the
 * hope of bringing related genes closer together, thereby facilitating the
 * production of building blocks. This mutation operator can also be used for
 * combinatorial problems, where no duplicated genes within a chromosome are
 * allowed, e.g. for the TSP.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
@XmlJavaTypeAdapter(SwapMutator.Model.Adapter.class)
public class SwapMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>

{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public SwapMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public SwapMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Swaps the genes in the given array, with the mutation probability of this
	 * mutation.
	 */
	@Override
	protected int mutate(final MSeq<G> genes, final double p) {
		final Random random = RandomRegistry.getRandom();

		return genes.length() > 1
			? (int)indexes(random, genes.length(), p)
				.peek(i -> genes.swap(i, random.nextInt(genes.length())))
				.count()
			: 0;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "swap-mutator")
	@XmlType(name = "org.jenetics.SwapMutator")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	final static class Model {

		@XmlAttribute(name = "probability", required = true)
		public double probability;

		public final static class Adapter
			extends XmlAdapter<Model, SwapMutator>
		{
			@Override
			public Model marshal(final SwapMutator value) {
				final Model m = new Model();
				m.probability = value.getProbability();
				return m;
			}

			@Override
			public SwapMutator unmarshal(final Model m) {
				return new SwapMutator(m.probability);
			}
		}
	}

}
