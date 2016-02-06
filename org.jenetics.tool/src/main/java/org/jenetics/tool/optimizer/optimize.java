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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class optimize<T, R extends Comparable<? super R>> {

//	/**
//	 * Worker class used for hiding the gene type.
//	 *
//	 * @param <G> the gene type
//	 */
//	private class Worker<G extends Gene<?, G>> {
//		private final org.jenetics.optimizer.Codec<G, T> _codec;
//		private final EvolutionParam<G, R> _param;
//
//		Worker(final org.jenetics.optimizer.Codec<G, T> codec, final EvolutionParam<G, R> param) {
//			_codec = requireNonNull(codec);
//			_param = requireNonNull(param);
//		}
//
//		private T optimize(final Function<T, R> function, final Optimize optimize) {
//			final Engine<G, R> engine = Engine
//				.builder(function.compose(_codec.decoder()), _codec.encoding())
//				.fitnessScaler(_param.getFitnessScaler())
//				.survivorsSelector(_param.getSurvivorsSelector())
//				.offspringSelector(_param.getOffspringSelector())
//				.alterers(_param.getAlterers())
//				.optimize(optimize)
//				.offspringFraction(_param.getOffspringFraction())
//				.populationSize(_param.getPopulationSize())
//				.maximalPhenotypeAge(_param.getMaximalPhenotypeAge())
//				.build();
//
//			final Genotype<G> bgt = engine.stream()
//				.limit(limit.bySteadyFitness(30))
//				.collect(EvolutionResult.toBestGenotype());
//
//			return _codec.decoder().apply(bgt);
//		}
//
//		T argmin(final Function<T, R> function) {
//			return optimize(function, Optimize.MINIMUM);
//		}
//
//		T argmax(final Function<T, R> function) {
//			return optimize(function, Optimize.MAXIMUM);
//		}
//
//	}
//
//	private Worker<?> _worker;
//
//	private optimize() {
//	}
//
//	public T argmin(final Function<T, R> function) {
//		return _worker.argmin(function);
//	}
//
//	public static <R extends Comparable<? super R>> Double
//	sargmin(final double min, final double max, final Function<Double, R> f) {
//		return null;
//	}
//
//	public T argmax(final Function<T, R> function) {
//		return _worker.argmax(function);
//	}
//
//	private static <
//		G extends Gene<?, G>,
//		S,
//		R extends Comparable<? super R>
//	>
//	optimize<S, R> of(final org.jenetics.optimizer.Codec<G, S> codec, final EvolutionParam<G, R> param) {
//		final optimize<S, R> optimizer = new optimize<>();
//		optimizer._worker = optimizer.new Worker<>(codec, param);
//
//		return optimizer;
//	}
//
//	private static <
//		G extends Gene<?, G>,
//		S,
//		R extends Comparable<? super R>
//	>
//	optimize<S, R> of(final org.jenetics.optimizer.Codec<G, S> codec) {
//		return of(codec, new EvolutionParam<G, R>());
//	}
//
//	public static <R extends Comparable<? super R>> optimize<Double, R> ofDouble(
//		final double min,
//		final double max
//	) {
//		final org.jenetics.optimizer.Codec<DoubleGene, Double> codec = org.jenetics.optimizer.Codec.ofDouble(min, max);
//		final EvolutionParam<DoubleGene, R> param =
//			new EvolutionParam<DoubleGene, R>()
//				.alterers(
//					new Mutator<>(0.15),
//					new MeanAlterer<>()
//				);
//
//		return of(codec, param);
//	}
//
//	public static <T, R extends Comparable<? super R>>
//	T _argmin(final Function<T, R> function) {
//		return null;
//	}
//
//	public static <R extends Comparable<? super R>>
//	int argmin(final int min, final int max, final IntFunction<R> function) {
//		return 0;
//	}
//
//	public static <R extends Comparable<? super R>>
//	long argmin(final long min, final long max, final LongFunction<R> function) {
//		return 0;
//	}
//
//	public static <R extends Comparable<? super R>>
//	double argmin(final double min, final double max, final DoubleFunction<R> function) {
//		return 0;
//	}
//
//	static Object foo = null;
//
//	static MinFac<Integer> between(final int min, final int max) {
//		return (MinFac<Integer>)foo;
//	}
//
//	interface MinFac<T> {
//		default <R extends Comparable<? super R>> T argmin(final Function<T, R> function) {
//			//Minimizer.of(null, null, null).argmin(function);
//			return null;
//		}
//	}
//
//	static class IntegerMinFac implements MinFac<Integer> {
//		public <R extends Comparable<? super R>> Integer argmin(final Function<Integer, R> function) {
//			return null;
//		}
//	}
//
//	public static <R extends Comparable<? super R>>
//	Optimizer<Integer, R> range(int min, int max) {
//		return (Optimizer<Integer, R>)foo;
//	}
//
//	public static void main(final String[] args) {
//		final Double result = optimize.<Double>ofDouble(0, Math.PI)
//			.argmin(Math::sin);
//
//		System.out.println(result);
//
//		argmin(0L, 100L, (long i) -> "");
//
//		optimize.<String>range(0, 1).argmin(optimize::fitness);
//
//		Minimizer<Integer, String> minimizer = range(0, 1);
//		minimizer.argmin(i -> "");
//
//		int r = optimize.between(1, 2).argmin(i -> "df");
//	}
//
//	static String fitness(int i) {
//		return "";
//	}
}
