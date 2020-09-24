package io.jenetics.incubator.timeseries;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.engine.EvolutionResult;

import io.jenetics.prog.ProgramGene;

final class RegressionRunner<T> implements Runnable {

	private final Stream<EvolutionResult<ProgramGene<T>, Double>> _stream;
	private final Consumer<RegressionResult<T>> _sink;

	private final AtomicBoolean _reset = new AtomicBoolean(false);
	private final AtomicBoolean _proceed = new AtomicBoolean(true);
	private final Semaphore _semaphore = new Semaphore(1);

	RegressionRunner(
		final Stream<EvolutionResult<ProgramGene<T>, Double>> stream,
		final Consumer<RegressionResult<T>> sink
	) {
		_stream = requireNonNull(stream);
		_sink = requireNonNull(sink);
	}

	@Override
	public void run() {
		final var best =
			RegressionRunner.<EvolutionResult<ProgramGene<T>, Double>>strictlyImproving(
				RegressionRunner::min, this::reset);

		try {
			_stream
				.takeWhile(e -> _proceed.get())
				.flatMap(e -> {
					lock();
					try {
						return best.apply(e);
					} finally {
						unlock();
					}
				})
				.forEach(null);
		} catch (CancellationException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static <C> Function<C, Stream<C>>
	strictlyImproving(
		final BinaryOperator<C> comparator,
		final Predicate<? super C> reset
	) {
		requireNonNull(comparator);

		return new Function<>() {
			private C _best;

			@Override
			public Stream<C> apply(final C result) {
				if (reset.test(result)) {
					_best = null;
				}

				final C best = comparator.apply(_best, result);

				final Stream<C> stream = best == _best
					? Stream.empty()
					: Stream.of(best);

				_best = best;

				return stream;
			}
		};
	}

	private static <T extends Comparable<? super T>> T min(final T a, final T b) {
		return best(Comparator.reverseOrder(), a, b);
	}

	private static <T>
	T best(final Comparator<? super T> comparator, final T a, final T b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return comparator.compare(a, b) >= 0 ? a : b;
	}

	private boolean reset(final EvolutionResult<ProgramGene<T>, Double> element) {
		return _reset.getAndSet(false);
	}

	void reset() {
		_reset.set(true);
	}

	void stop() {
		_proceed.set(false);
	}

	void lock() {
		try {
			_semaphore.acquire();
		} catch (InterruptedException e) {
			throw new CancellationException(e.toString());
		}
	}

	void unlock() {
		_semaphore.release();
	}

}
