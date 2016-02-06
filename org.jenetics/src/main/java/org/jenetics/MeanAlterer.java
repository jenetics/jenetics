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

import java.io.Serializable;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Seq;

/**
 * <p>
 * The order ({@link #getOrder()}) of this Recombination implementation is two.
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
@XmlJavaTypeAdapter(MeanAlterer.Model.Adapter.class)
public final class MeanAlterer<
	G extends Gene<?, G> & Mean<G>,
	C extends Comparable<? super C>
>
	extends Recombinator<G, C>
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public MeanAlterer(final double probability) {
		super(probability, 2);
	}

	/**
	 * Create a new alterer with alter probability of {@code 0.05}.
	 */
	public MeanAlterer() {
		this(0.05);
	}

	@Override
	protected int recombine(
		final Population<G, C> population,
		final int[] individuals,
		final long generation
	) {
		final Random random = RandomRegistry.getRandom();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();

		final int cindex = random.nextInt(gt1.length());
		final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
		final ISeq<Chromosome<G>> c2 = gt2.toSeq();

		// Calculate the mean value of the gene array.
		final MSeq<G> mean = mean(
			c1.get(cindex).toSeq().copy(),
			c2.get(cindex).toSeq()
		);

		c1.set(cindex, c1.get(cindex).newInstance(mean.toISeq()));

		population.set(
			individuals[0],
			pt1.newInstance(gt1.newInstance(c1.toISeq()), generation)
		);

		return 1;
	}

	private static <G extends Gene<?, G> & Mean<G>>
	MSeq<G> mean(final MSeq<G> a, final Seq<G> b) {
		for (int i = a.length(); --i >= 0;) {
			a.set(i, a.get(i).mean(b.get(i)));
		}
		return a;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MeanAlterer && super.equals(obj);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "mean-alterer")
	@XmlType(name = "org.jenetics.MeanAlterer")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	final static class Model {

		@XmlAttribute(name = "probability", required = true)
		public double probability;

		public final static class Adapter
			extends XmlAdapter<Model, MeanAlterer>
		{
			@Override
			public Model marshal(final MeanAlterer value) {
				final Model m = new Model();
				m.probability = value.getProbability();
				return m;
			}

			@Override
			public MeanAlterer unmarshal(final Model m) {
				return new MeanAlterer(m.probability);
			}
		}
	}

}
