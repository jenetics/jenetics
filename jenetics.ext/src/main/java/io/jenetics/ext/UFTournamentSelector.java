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

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * The last front selection procedure takes two arguments: a front F and the
 * number of solutions to select k. The algorithm starts by sorting the unique
 * fitnesses associated with the members of F in descending order of crowding
 * distance (>dist) and stores the result in a list F (line 2). It then proceeds
 * to fill the initially empty set of solutions S by cycling over the sorted
 * unique fitnesses F. For each fitness, the algorithm first selects each
 * individual sharing this fitness and puts the result in set T (line 6). If
 * the resulting set is not empty, the procedure randomly picks one solution
 * from T, adds it to set S and finally removes it from the front F so that
 * individuals can be picked only once (lines 8 to 10). When all k solutions
 * have been selected, the loop stops and set S is returned (line 14).
 *
 * <p>
 *  <b>Reference:</b><em>
 *      Félix-Antoine Fortin and Marc Parizeau. 2013. Revisiting the NSGA-II
 *      crowding-distance computation. In Proceedings of the 15th annual
 *      conference on Genetic and evolutionary computation (GECCO '13),
 *      Christian Blum (Ed.). ACM, New York, NY, USA, 623-630.
 *      DOI=<a href="http://dx.doi.org/10.1145/2463372.2463456">
 *          10.1145/2463372.2463456</a></em>
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class UFTournamentSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	@Override
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
		final int count,
		final Optimize opt
	) {
		return null;
	}
}
