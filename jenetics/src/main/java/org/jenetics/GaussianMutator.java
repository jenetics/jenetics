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

import static java.lang.Double.compare;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.String.format;
import static org.jenetics.internal.math.base.clamp;
import static org.jenetics.internal.math.random.indexes;

import java.io.Serializable;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.math.base;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * The GaussianMutator class performs the mutation of a {@link NumericGene}.
 * This mutator picks a new value based on a Gaussian distribution around the
 * current value of the gene. The variance of the new value (before clipping to
 * the allowed gene range) will be
 * <p>
 * <img
 *     src="doc-files/gaussian-mutator-var.gif"
 *     alt="\hat{\sigma }^2 = \left ( \frac{ g_{max} - g_{min} }{4}\right )^2"
 * >
 * </p>
 * The new value will be cropped to the gene's boundaries.
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
@XmlJavaTypeAdapter(GaussianMutator.Model.Adapter.class)
public final class GaussianMutator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public GaussianMutator(final double probability) {
		super(probability);
	}

	public GaussianMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	protected int mutate(final MSeq<G> genes, final double p) {
		final Random random = RandomRegistry.getRandom();

		return (int)indexes(random, genes.length(), p)
			.peek(i -> genes.set(i, mutate(genes.get(i), random)))
			.count();
	}

	G mutate(final G gene, final Random random) {
		final double min = gene.getMin().doubleValue();
		final double max = gene.getMax().doubleValue();
		final double std = (max - min)*0.25;

		double value = base.clamp(
			random.nextGaussian()*std + gene.doubleValue(),
			min, max
		);
		if (compare(value, max) >= 0) {
			value = longBitsToDouble(doubleToLongBits(max) - 1);
		}

		return gene.newInstance(value);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GaussianMutator && super.equals(obj);
	}

	@Override
	public String toString() {
		return format(
			"%s[p=%f]",
			getClass().getSimpleName(),
			_probability
		);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "gaussian-mutator")
	@XmlType(name = "org.jenetics.GaussianMutator")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	final static class Model {

		@XmlAttribute(name = "probability", required = true)
		public double probability;

		public final static class Adapter
			extends XmlAdapter<Model, GaussianMutator>
		{
			@Override
			public Model marshal(final GaussianMutator value) {
				final Model m = new Model();
				m.probability = value.getProbability();
				return m;
			}

			@Override
			public GaussianMutator unmarshal(final Model m) {
				return new GaussianMutator(m.probability);
			}
		}
	}

}
