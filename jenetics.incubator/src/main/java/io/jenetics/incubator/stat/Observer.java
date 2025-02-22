package io.jenetics.incubator.stat;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class Observer implements Supplier<Histogram> {
	private final Sampling sampling;
	private final Histogram.Partition partition;
	private final Executor executor;

	private Histogram histogram;

	public Observer(
		final Sampling sampling,
		final Histogram.Partition partition,
		final Executor executor
	) {
		this.sampling = requireNonNull(sampling);
		this.partition = requireNonNull(partition);
		this.executor = requireNonNull(executor);
	}

	@Override
	public synchronized Histogram get() {
		if (histogram == null) {
			executor.execute(() -> {
				histogram = new Histogram.Builder(partition).build(sampling);
			});
		}
		return histogram;
	}

	public Histogram.Partition partition() {
		return partition;
	}

}
