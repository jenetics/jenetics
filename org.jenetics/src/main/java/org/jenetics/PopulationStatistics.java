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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-01 $</em>
 * @since @__version__@
 */
public interface PopulationStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	/*
	protected Optimize _optimize = Optimize.MAXIMUM;
	protected int _generation = 0;
	protected Phenotype<G, C> _best = null;
	protected Phenotype<G, C> _worst = null;
	protected int _samples = 0;
	protected double _ageMean = NaN;
	protected double _ageVariance = NaN;
	protected int _killed = 0;
	protected int _invalid = 0;
	*/

	public Phenotype<G, C> getBestPhenotype();

	public Phenotype<G, C> getWorstPhenotype();

	//public Moment<Double> getAgeMoment();

	public int getPopulationSize();

}
