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

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.comb.subset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.ElementComparator;
import io.jenetics.ext.util.ElementDistance;
import io.jenetics.ext.util.Pareto;

/**
 * Unique fitness based tournament selection.
 *
 * The last front selection procedure takes two arguments: a front F and the
 * number of solutions to select k. The algorithm starts by sorting the unique
 * fitnesses associated with the members of F in descending order of crowding
 * distance (dist) and stores the result in a list F (line 2). It then proceeds
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
	private final Comparator<Phenotype<G, C>> _dominance;
	private final ElementComparator<Phenotype<G, C>> _comparator;
	private final ElementDistance<Phenotype<G, C>> _distance;
	private final ToIntFunction<Phenotype<G, C>> _dimension;

	public UFTournamentSelector(
		final Comparator<? super C> dominance,
		final ElementComparator<? super C> comparator,
		final ElementDistance<? super C> distance,
		final ToIntFunction<? super C> dimension
	) {
		requireNonNull(dominance);
		requireNonNull(comparator);
		requireNonNull(distance);
		requireNonNull(dimension);

		_dominance = (a, b) -> dominance.compare(a.getFitness(), b.getFitness());
		_comparator = comparator.map(Phenotype::getFitness);
		_distance = distance.map(Phenotype::getFitness);
		_dimension = v -> dimension.applyAsInt(v.getFitness());
	}

	@Override
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
		final int count,
		final Optimize opt
	) {
		final Random random = RandomRegistry.getRandom();

		final int[] rank = Pareto.ranks(population, _dominance);
		final double[] dist = Pareto.crowdingDistance(
			population, _comparator, _distance, _dimension
		);

		final List<Phenotype<G, C>> S = new ArrayList<>();

		while (S.size() < count) {
			final int k = min(2*count - S.size(), population.size());
			final int[] G = subset(population.size(), k);

			int p = 0;
			for (int j = 0; j < G.length; j += 2) {
				if (cco(G[j], G[j + 1], rank, dist)) {
					p = G[j];
				} else if (cco(G[j + 1], G[j], rank, dist)) {
					p = G[j + 1];
				} else {
					p = subset(new int[]{G[j], G[j + 1]}, 1)[0];
				}

				final C fitness = population.get(p).getFitness();
				final List<Phenotype<G, C>> list = population.stream()
					.filter(pt -> pt.getFitness().equals(fitness))
					.collect(Collectors.toList());

				S.add(list.get(random.nextInt(list.size())));
			}
		}

		return ISeq.of(S);
	}

	private boolean cco(
		final int i, final int j,
		final int[] rank, final double[] dist
	) {
		return rank[i] < rank[j] || (rank[i] == rank[j] && dist[i] > dist[j]);
	}

}
