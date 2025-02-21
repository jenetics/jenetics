package io.jenetics.incubator.stat;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;

public record Observation(
	Consumer<? super DoubleConsumer> samples,
	Histogram.Partition partition
)
	implements Supplier<Histogram>
{
	@Override
	public Histogram get() {
		return null;
	}
}
