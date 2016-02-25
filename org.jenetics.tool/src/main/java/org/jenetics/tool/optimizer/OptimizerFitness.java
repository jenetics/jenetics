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

import static java.util.Objects.requireNonNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.jaxb;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Selector;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(OptimizerFitness.Model.Adapter.class)
public final class OptimizerFitness<
	G extends Gene<?, G>,
	C extends Comparable<? super C>>
	implements Comparable<OptimizerFitness<G, C>
>
{

	private final EvolutionResult<G, C> _result;
	private final EvolutionParam<G, C> _param;

	private OptimizerFitness(
		final EvolutionResult<G, C> result,
		final EvolutionParam<G, C> param
	) {
		_result = requireNonNull(result);
		_param = requireNonNull(param);
	}

	public C getFitness() {
		return _result.getBestFitness();
	}

	public EvolutionResult<G, C> getResult() {
		return _result;
	}

	public EvolutionParam<G, C> getParam() {
		return _param;
	}

	@Override
	public int compareTo(final OptimizerFitness<G, C> other) {
		int cmp = getFitness().compareTo(other.getFitness());

		if (cmp == 0) {
			final double complexity1 =
				complexity(getParam().getAlterer()) +
				complexity(getParam().getOffspringSelector())*0.5 +
				complexity(getParam().getSurvivorsSelector())*0.5;

			final double complexity2 =
				complexity(other.getParam().getAlterer()) +
				complexity(other.getParam().getOffspringSelector())*0.5 +
				complexity(other.getParam().getSurvivorsSelector())*0.5;

			cmp = Double.compare(complexity2, complexity1);
		}

		if (cmp == 0) {
			cmp = other.getParam().getPopulationSize() -
				getParam().getPopulationSize();
		}

		return _result.getOptimize() == Optimize.MAXIMUM
			? cmp : -cmp;
	}

	private static double complexity(final Alterer<?, ?> alterer) {
		return AltererComplexity.INSTANCE.complexity(alterer);
	}

	private static double complexity(final Selector<?, ?> selector) {
		return SelectorComplexity.INSTANCE.complexity(selector);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	OptimizerFitness<G, C> of(
		final EvolutionResult<G, C> result,
		final EvolutionParam<G, C> param
	) {
		return new OptimizerFitness<>(result, param);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "optimizer-fitness")
	@XmlType(name = "org.jenetics.tool.optimizer.OptimizerFitness")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final static class Model {

		@XmlElement(name = "result", required = true, nillable = false)
		public Object result;

		@XmlElement(name = "param", required = true, nillable = false)
		public Object param;

		public final static class Adapter
			extends XmlAdapter<Model, OptimizerFitness>
		{
			@Override
			public Model marshal(final OptimizerFitness result) throws Exception {
				final Model m = new Model();
				m.result = jaxb.marshal(result.getResult());
				m.param = jaxb.marshal(result.getParam());
				return m;
			}

			@Override
			public OptimizerFitness unmarshal(final Model m) throws Exception {
				return OptimizerFitness.of(
					(EvolutionResult)jaxb.unmarshal(m.result),
					(EvolutionParam)jaxb.unmarshal(m.param)
				);
			}
		}
	}

}
