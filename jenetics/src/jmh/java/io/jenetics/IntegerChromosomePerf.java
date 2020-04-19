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

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class IntegerChromosomePerf {

	static final class IntChromosome implements Chromosome<IntegerGene> {
		private final int[] _alleles;
		private final int _min;
		private final int _max;

		IntChromosome(final int[] alleles, final int min, final int max) {
			_min = min;
			_max = max;
			_alleles = alleles;
		}
		@Override
		public IntegerGene get(final int index) {
			return IntegerGene.of(_alleles[index], _min, _max);
		}
		@Override
		public int length() {
			return _alleles.length;
		}

		@Override
		public Chromosome<IntegerGene> newInstance(ISeq<IntegerGene> genes) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Chromosome<IntegerGene> newInstance() {
			throw new UnsupportedOperationException();
		}
	}

	@Param({"1", "10", "100", "1000", "10000", "100000"})
	public int size;

	public IntChromosome intChromosome;

	public IntegerChromosome integerChromosome;

	@Setup
	public void setup() {
		int[] alleles = new Random()
			.ints(size, 0, 1000_000)
			.toArray();
		intChromosome = new IntChromosome(alleles, 0, 1000_000);

		integerChromosome = IntegerChromosome.of(0, 1000_000, size);
	}

	@Benchmark
	public void intChromosome(final Blackhole bh) {
		for (var gene : intChromosome) {
			bh.consume(gene);
		}
	}

	@Benchmark
	public void integerChromosome(final Blackhole bh) {
		for (var gene : integerChromosome) {
			bh.consume(gene);
		}
	}

}

/*
Benchmark                                (size)  Mode  Cnt       Score       Error  Units
IntegerChromosomePerf.intChromosome           1  avgt   15       8.070 ±     0.093  ns/op
IntegerChromosomePerf.intChromosome          10  avgt   15      78.319 ±     0.613  ns/op
IntegerChromosomePerf.intChromosome         100  avgt   15     790.453 ±     6.070  ns/op
IntegerChromosomePerf.intChromosome        1000  avgt   15    7858.052 ±   101.965  ns/op
IntegerChromosomePerf.intChromosome       10000  avgt   15   76493.077 ±  2003.014  ns/op
IntegerChromosomePerf.intChromosome      100000  avgt   15  753099.748 ± 12635.873  ns/op
IntegerChromosomePerf.integerChromosome       1  avgt   15       8.979 ±     0.058  ns/op
IntegerChromosomePerf.integerChromosome      10  avgt   15      70.606 ±     0.672  ns/op
IntegerChromosomePerf.integerChromosome     100  avgt   15     702.207 ±     5.826  ns/op
IntegerChromosomePerf.integerChromosome    1000  avgt   15    6955.720 ±    53.914  ns/op
IntegerChromosomePerf.integerChromosome   10000  avgt   15   67476.859 ±  2709.146  ns/op
IntegerChromosomePerf.integerChromosome  100000  avgt   15  640108.412 ±  5120.020  ns/op
 */
