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
package io.jenetics;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import io.jenetics.internal.util.Concurrency;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PhenotypeEvaluationPerf {

	static final int SIZE = 1000;

	private final Random _random = new Random(123);

	private final Executor _executor = ForkJoinPool.commonPool();

	private final Function<Genotype<DoubleGene>, Double> _ff =
		gt -> sin(toRadians(gt.getGene().getAllele()));

	private final Factory<Genotype<DoubleGene>> _genotype = Genotype.of(
		DoubleChromosome.of(0, 1, 50)
	);

	private Factory<Phenotype<DoubleGene, Double>> factory() {
		return () -> Phenotype.of(_genotype.newInstance(), 0, _ff);
	}

	private ISeq<Phenotype<DoubleGene, Double>> _unevaluated = factory()
		.instances()
		.limit(SIZE)
		.collect(ISeq.toISeq());

	private ISeq<Phenotype<DoubleGene, Double>> _evaluated = factory()
		.instances()
		.limit(SIZE)
		.map(Phenotype::evaluate)
		.collect(ISeq.toISeq());

	private ISeq<Phenotype<DoubleGene, Double>> _halfEvaluated = factory()
		.instances()
		.limit(SIZE)
		.map(pt -> _random.nextBoolean() ? pt.evaluate() : pt)
		.collect(ISeq.toISeq());


	@Setup(Level.Iteration)
	public void init() {
		_unevaluated = factory()
			.instances()
			.limit(SIZE)
			.collect(ISeq.toISeq());

		_evaluated = factory()
			.instances()
			.limit(SIZE)
			.map(Phenotype::evaluate)
			.collect(ISeq.toISeq());

		_halfEvaluated = factory()
			.instances()
			.limit(SIZE)
			.map(pt -> _random.nextBoolean() ? pt.evaluate() : pt)
			.collect(ISeq.toISeq());
	}


	@Benchmark
	public double baselineUnevaluated() {
		try (Concurrency c = Concurrency.with(_executor)) {
			c.execute(_unevaluated);
		}
		return _unevaluated.get(0).getFitness();
	}

	@Benchmark
	public double unevaluated() {
		final ISeq<Phenotype<DoubleGene, Double>> pop = _unevaluated.stream()
			.filter(pt -> !pt.isEvaluated())
			.collect(ISeq.toISeq());

		try (Concurrency c = Concurrency.with(_executor)) {
			c.execute(pop);
		}
		return _unevaluated.get(0).getFitness();
	}

	@Benchmark
	public double baselineEvaluated() {
		try (Concurrency c = Concurrency.with(_executor)) {
			c.execute(_evaluated);
		}
		return _evaluated.get(0).getFitness();
	}

	@Benchmark
	public double evaluated() {
		final ISeq<Phenotype<DoubleGene, Double>> pop = _evaluated.stream()
			.filter(pt -> !pt.isEvaluated())
			.collect(ISeq.toISeq());

		try (Concurrency c = Concurrency.with(_executor)) {
			if (!pop.isEmpty()) {
				c.execute(pop);
			}
		}
		return _evaluated.get(0).getFitness();
	}

	@Benchmark
	public double baselineHalfEvaluated() {
		try (Concurrency c = Concurrency.with(_executor)) {
			c.execute(_halfEvaluated);
		}
		return _halfEvaluated.get(0).getFitness();
	}

	@Benchmark
	public double halfEvaluated() {
		final ISeq<Phenotype<DoubleGene, Double>> pop = _halfEvaluated.stream()
			.filter(pt -> !pt.isEvaluated())
			.collect(ISeq.toISeq());

		try (Concurrency c = Concurrency.with(_executor)) {
			c.execute(pop);
		}
		return _halfEvaluated.get(0).getFitness();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + PhenotypeEvaluationPerf.class.getSimpleName() + ".*")
			.warmupIterations(5)
			.measurementIterations(7)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
Benchmark                                      Mode  Cnt      Score     Error  Units
PhenotypeEvaluationPerf.baselineEvaluated      avgt   50  25087.267 ± 625.360  ns/op
PhenotypeEvaluationPerf.baselineHalfEvaluated  avgt   50  24801.377 ± 644.174  ns/op
PhenotypeEvaluationPerf.baselineUnevaluated    avgt   50  24174.220 ± 597.956  ns/op
PhenotypeEvaluationPerf.evaluated              avgt   50   5341.027 ± 128.542  ns/op
PhenotypeEvaluationPerf.halfEvaluated          avgt   50   7289.133 ± 158.460  ns/op
PhenotypeEvaluationPerf.unevaluated            avgt   50   7413.798 ± 248.385  ns/op
 */
