/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 */
package io.jenetics;

import java.util.Random;
import java.util.random.RandomGenerator;

import io.jenetics.util.MSeq;

/**
 * The shift mutation, applies mutation between two randomly chosen points.
 * A random value between the two points splits the sequences of genes
 * between the positions. The second sequence is then shifted in front of
 * the first one. This mutation operator can also be used for combinatorial problems,
 * where no duplicated genes within a chromosome are allowed, e.g., for the TSP. In
 * contrast to the ShiftMutation, the number of genes which are mutated has an
 * expectancy value of p. Hereby a random value with a shifted expectancy value is
 * used.
 * For more details see: Kruse et al.. Elements of evolutionary algorithms. In Kruse et al., Computational
 * intelligence: A methodological introduction (pp. 255–285). Springer International Publishing.
 * <a href="https://doi.org/10.1007/978-3-030-42227-1_12">https://doi.org/10.1007/978-3-030-42227-1_12</a>
 *
 * @see Mutator
 * @see ShiftMutator
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 7.2
 * @since 7.2
 */

public class ShiftMutatorWithK<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
	extends Mutator<G, C> {

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public ShiftMutatorWithK(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public ShiftMutatorWithK() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Splits the values between two points with into two sequences and shifts the second
	 * one in front of the first one. The distance between the two positions has an
	 * expectancy value equal p.
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final RandomGenerator random
	) {
		final MutatorResult<Chromosome<G>> result;
		if(chromosome.length() > 1) {
			double lengthDouble = getDistance(p, random);
			int lengthInt = (int) Math.round(chromosome.length() * lengthDouble);
			if(lengthInt == 0) {
				result = new MutatorResult<>(chromosome, 0);
			} else {
				int startingPoint;
				if (lengthInt >= chromosome.length()) {
					startingPoint = 0;
				} else {
					startingPoint = random.nextInt(chromosome.length() - lengthInt);
				}
				int endPoint = startingPoint + lengthInt;
				int middlePoint = startingPoint + random.nextInt(lengthInt);
				final MSeq<G> genes = MSeq.of(chromosome);
				MSeq<G> firstSeq = genes.subSeq(startingPoint, middlePoint).copy();
				int difOne = endPoint - middlePoint;
				MSeq<G> secondSeq = genes.subSeq(middlePoint, endPoint).copy();
				int difTwo = middlePoint - startingPoint;
				int i = 0;
				for (G g : firstSeq) {
					genes.set(startingPoint + i + difOne, g);
					i++;
				}
				i = 0;
				for (G g : secondSeq) {
					genes.set(middlePoint + i - difTwo, g);
					i++;
				}

				result = new MutatorResult<>(
					chromosome.newInstance(genes.toISeq()),
					endPoint - startingPoint
				);
			}
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}
		return result;
	}

	static double getDistance(double p, RandomGenerator random) {
		double r = random.nextDouble();
		if(p == 0) {
			return 0;
		} else if(p < 0.292893) {
			double b = (2-Math.sqrt(2))/p;
			double m = -Math.pow(b,2)/2;
			return (-b+Math.sqrt(Math.pow(b,2)+2*r*m))/m;
		} else if(p < 0.5){
			double b = (Math.pow(p,2)-0.5)/(Math.pow(p,2)-p);
			double m = 2-2*b;
			return (-b+Math.sqrt(Math.pow(b,2)+2*r*m))/m;
		} else if(p == 0.5) {
			return r;
		} else if(p < 0.707107) {
			double b = (Math.pow(p,2)-0.5)/(Math.pow(p, 2)- p);
			double m = 2-2*b;
			return (b-Math.sqrt(Math.pow(b,2)+2*m*(r-1+b+0.5*m)))/-m;
		} else if(p < 1) {
			double b = 0.5*(1-Math.pow(((2-Math.sqrt(2))/(1-p)-1), 2));
			double m = -b+Math.sqrt(1-2*b)+1;
			return (b-Math.sqrt(Math.pow(b,2)+2*m*(r-1+b+0.5*m)))/-m;
		} else {
			return 1;
		}
	}


}
