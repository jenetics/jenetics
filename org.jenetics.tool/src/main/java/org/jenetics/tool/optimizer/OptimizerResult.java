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
package org.jenetics.tool.optimizer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.JAXBContextCache;
import org.jenetics.internal.util.jaxb;

import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(OptimizerResult.Model.Adapter.class)
public class OptimizerResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	static {
		JAXBContextCache.addPackage("org.jenetics.tool.optimizer");
	}

	private final ISeq<Genotype<DoubleGene>> _genotypes;
	private final EvolutionParam<G, C> _param;
	private final C _fitness;
	private final C _rawFitness;
	private long _generation;

	public OptimizerResult(
		final ISeq<Genotype<DoubleGene>> genotypes,
		final EvolutionParam<G, C> param,
		final C fitness,
		final C rawFitness,
		final long generation
	) {
		_genotypes = requireNonNull(genotypes);
		_param = requireNonNull(param);
		_fitness = requireNonNull(fitness);
		_rawFitness = requireNonNull(rawFitness);
		_generation = generation;
	}

	public ISeq<Genotype<DoubleGene>> getGenotypes() {
		return _genotypes;
	}

	public EvolutionParam<G, C> getParam() {
		return _param;
	}

	public C getFitness() {
		return _fitness;
	}

	public C getRawFitness() {
		return _rawFitness;
	}

	public long getGeneration() {
		return _generation;
	}

	public void write(final File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		final File file = new File(dir, format("%07d.xml", _generation));
		try {
			IO.jaxb.write(this, file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	OptimizerResult<G, C> read(final File file) {
		try {
			return (OptimizerResult<G, C>)IO.jaxb.read(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "optimizer-result")
	@XmlType(name = "org.jenetics.tool.optimizer.OptimizerResult")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final static class Model {

		@XmlElement(name = "genotype", required = true)
		public List genotypes;

		@XmlElement(name = "best-param", required = true, nillable = false)
		public Object param;

		@XmlElement(name = "best-fitness", required = true, nillable = false)
		public Object fitness;

		@XmlElement(name = "best-raw-fitness", required = true, nillable = false)
		public Object rawFitness;

		@XmlElement(name = "generation", required = true, nillable = false)
		public long generation;

		public final static class Adapter
			extends XmlAdapter<Model, OptimizerResult>
		{
			@Override
			public Model marshal(final OptimizerResult result) throws Exception {
				final Model m = new Model();
				if (!result.getGenotypes().isEmpty()) {
					m.genotypes = (List)result.getGenotypes().stream()
						.map(jaxb.Marshaller(result.getGenotypes().get(0)))
						.collect(toList());
				}
				m.param = jaxb.marshal(result.getParam());
				m.fitness = result.getFitness();
				m.rawFitness = result.getRawFitness();
				m.generation = result.getGeneration();
				return m;
			}

			@Override
			public OptimizerResult unmarshal(final Model m) throws Exception {
				return new OptimizerResult(
					(ISeq)m.genotypes.stream()
						.map(jaxb.Unmarshaller(m.genotypes.get(0)))
						.collect(ISeq.toISeq()),
					(EvolutionParam)jaxb.unmarshal(m.param),
					(Comparable)m.fitness,
					(Comparable)m.rawFitness,
					m.generation
				);
			}
		}
	}

}
