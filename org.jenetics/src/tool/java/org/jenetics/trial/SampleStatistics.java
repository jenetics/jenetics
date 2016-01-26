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
package org.jenetics.trial;

import static java.lang.String.format;

import java.util.function.Consumer;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 */
public class SampleStatistics implements Consumer<Sample> {

	private final int _size;
	private final ISeq<DoubleMomentStatistics> _moments;
	private final ISeq<ExactQuantile> _quantiles;

	public SampleStatistics(final int size) {
		_size = require.positive(size);
		_moments = MSeq.of(DoubleMomentStatistics::new, size).toISeq();
		_quantiles = MSeq.of(ExactQuantile::new, size).toISeq();
	}

	@Override
	public void accept(final Sample sample) {
		if (sample.size() != _size) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_moments.size(), sample.size()
			));
		}

		for (int i = 0; i < _size; ++i) {
			_moments.get(i).accept(sample.get(i));
			_quantiles.get(i).accept(sample.get(i));
		}
	}

	public SampleStatistics combine(final SampleStatistics statistics) {
		if (statistics._size != _size) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_size, statistics._size
			));
		}

		for (int i = 0; i < _size; ++i) {
			_moments.get(i).combine(statistics._moments.get(i));
			_quantiles.get(i).combine(statistics._quantiles.get(i));
		}

		return this;
	}

	/*
	private static final String[] HEADER = {
		"1-P",
		"2-TG-mean",
		"3-TG-variance",
		"4-TG-skewness",
		"5-TG-kurtosis",
		"6-TG-median",
		"7-TG-low",
		"8-TG-high",
		"9-TG-min",
		"10-TG-max",
		"11-F-mean",
		"12-F-variance",
		"13-F-skewness",
		"14-F-kurtosis",
		"15-F-median",
		"16-F-low",
		"17-F-high",
		"18-F-min",
		"19-F-max"
	};

	private final int _samples;
	private final Engine<G, Double> _engine;
	private final Function<P, Predicate<? super EvolutionResult<G, Double>>> _limit;
	private final Function<? super P, ? extends Comparable<?>> _parameterConverter;

	private final List<Object[]> _result = synchronizedList(new ArrayList<>());

	public SampleStatistics(
		final int samples,
		final Engine<G, Double> engine,
		final Function<P, Predicate<? super EvolutionResult<G, Double>>> limit,
		final Function<? super P, ? extends Comparable<?>> parameterConverter
	) {
		_samples = samples;
		_engine = requireNonNull(engine);
		_limit = requireNonNull(limit);
		_parameterConverter = requireNonNull(parameterConverter);
	}

	public SampleStatistics(
		final int samples,
		final Engine<G, Double> engine,
		final Function<P, Predicate<? super EvolutionResult<G, Double>>> limit
	) {
		this(samples, engine, limit, SampleStatistics::defaultParameterConverter);
	}

	private static Comparable<?> defaultParameterConverter(final Object parameter) {
		return parameter instanceof Comparable<?>
			? (Comparable<?>)parameter
			: Objects.toString(parameter);
	}

	@Override
	public void accept(final P parameter) {
		_result.add(exec(requireNonNull(parameter)));
	}

	private Object[] exec(final P parameter) {
		final SampleSummary[] result = IntStream.range(0, _samples)
			.mapToObj(i -> toResult(parameter))
			.collect(toCandleStickPoint(a -> a._1, a -> a._2));

		final Object[] data = new Object[19];
		data[0] = _parameterConverter.apply(parameter);

		// Total generation
		data[1] = df(result[0].mean);
		data[2] = df(result[0].variance);
		data[3] = df(result[0].skewness);
		data[4] = df(result[0].kurtosis);
		data[5] = (int)result[0].median;
		data[6] = (int)result[0].low;
		data[7] = (int)result[0].high;
		data[8] = (int)result[0].min;
		data[9] = (int)result[0].max;

		// Fitness
		data[10] = df(result[1].mean);
		data[11] = df(result[1].variance);
		data[12] = df(result[1].skewness);
		data[13] = df(result[1].kurtosis);
		data[14] = df(result[1].median);
		data[15] = df(result[1].low);
		data[16] = df(result[1].high);
		data[17] = df(result[1].min);
		data[18] = df(result[1].max);

		return data;
	}

	private IntDoublePair toResult(final P parameter) {
		final EvolutionStatistics<Double, DoubleMomentStatistics> statistics =
			EvolutionStatistics.ofNumber();

		final EvolutionResult<G, Double> result = _engine.stream()
			.limit(_limit.apply(parameter))
			.peek(statistics)
			.collect(toBestEvolutionResult());

		return IntDoublePair.of(
			(int)result.getTotalGenerations(),
			result.getBestFitness()
		);
	}

	@SuppressWarnings("unchecked")
	public Object[][] getResult() {
		final List<Object[]> result = new ArrayList<>(_result);
		result.sort((a, b) -> ((Comparable)a[0]).compareTo((Comparable)b[0]));
		result.add(0, HEADER);

		return result.toArray(new Object[0][]);
	}


	public void write(final Appendable out) {
		write(getResult(), out);
	}


	public void write(final File file) throws IOException {
		try (PrintWriter writer = new PrintWriter(file)) {
			write(writer);
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private static void write(final Object[][] data, final Appendable out) {
		final String[][] sdata = Stream.of(data)
			.map(e -> Stream.of(e).map(Objects::toString).toArray(String[]::new))
			.toArray(String[][]::new);

		final int[] width = Stream.of(sdata)
			.map(e -> Stream.of(e).mapToInt(String::length).toArray())
			.collect(toWidth(sdata[0].length));

		final String pattern = IntStream.of(width)
			.mapToObj(i -> format("%%%ds", i))
			.collect(joining("  "));

		final UncheckedAppendable uout = new UncheckedAppendable(out);

		uout.append("#")
			.append(format(pattern, (Object[])sdata[0]))
			.append('\n');

		Stream.of(sdata).skip(1).forEach(d ->
			uout.append(" ")
				.append(format(pattern, (Object[])d))
				.append('\n')
		);

	}

	private static Collector<int[], ?, int[]> toWidth(final int length) {
		return Collector.of(() -> new int[length],
			SampleStatistics::max,
			SampleStatistics::max
		);
	}

	private static int[] max(final int[] a, final int[] b) {
		for (int i = 0; i < a.length; ++i) {
			a[i] = Math.max(a[i], b[i]);
		}
		return a;
	}

	private static String df(final double value) {
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(7);
		format.setMinimumFractionDigits(7);

		return format.format(value).replace(",", "");
	}

	public void warmup(final Engine<?, ?> engine) {
		System.out.print("Warmup");

		final long start = System.currentTimeMillis();
		for (int i = 0; i < 700; ++i) {
			engine.stream()
				.limit(300)
				.mapToInt(r -> r.getAlterCount())
				.sum();
		}
		final long end = System.currentTimeMillis();

		System.out.println(format(": %f sec.", (end - start)/1_000.0));
	}
	*/

}
