package io.jenetics.example.foo;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.StreamPublisher;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;

public class GeneticScanner implements AutoCloseable {

	private StreamPublisher<Result> _publisher;

	// Um die Sache für jetzt zu vereinfachen, fixe Konfiguration.
	private final Factory<Genotype<DoubleGene>> _gtf = Genotype.of(
		DoubleChromosome.of(0, 1),
		DoubleChromosome.of(0, 10)
	);

	// Auch hier, zunächst mal fixe Konfiguration.
	private final Engine<DoubleGene, Vec<double[]>> _engine = Engine
		.builder(this::fitness, _gtf)
		.build();


	// Die Implementierung der Fitnessfunktion muss man sich getrennt
	// ansehen. Sehr viel SW-Engineering Probleme ;-)
	private Vec<double[]> fitness(final Genotype<DoubleGene> genotype) {
		final double x = genotype.get(0).gene().allele();
		final double y = genotype.get(1).gene().allele();
		return Vec.of(x, y);
	}

	public void performScan(final Consumer<Result> callback) {
		performScan(new Subscriber<Result>() {
			private Subscription _subscription;
			@Override
			public void onSubscribe(final Subscription subscription) {
				_subscription = subscription;
				_subscription.request(1);
			}
			@Override
			public void onNext(final Result result) {
				callback.accept(result);
				_subscription.request(1);
			}
			@Override
			public void onError(final Throwable throwable) {
			}
			@Override
			public void onComplete() {}
		});
	}

	public synchronized void performScan(final Subscriber<Result> subscriber) {
		if (_publisher != null) {
			throw new IllegalStateException("Scanner already started.");
		}

		final var size = IntRange.of(30, 50);
		final var collector = MOEA.<DoubleGene, double[], Vec<double[]>>toParetoSet(size);
		final var best = Accumulator.of(collector);

		final var stream = _engine.stream()
			.limit(r -> !Thread.currentThread().isInterrupted())
			.peek(best)
			.map(r -> new Result(r, best.result()));

		final var publisher = new StreamPublisher<Result>();
		_publisher = publisher;
		publisher.subscribe(subscriber);
		publisher.attach(stream);
	}

	@Override
	public synchronized void close() {
		if (_publisher != null) {
			_publisher.close();
		}
	}

	public static void main(final String[] args) throws InterruptedException {
		try (var scanner = new GeneticScanner()) {
			// Dieser Aufruf ist non-blocking.
			scanner.performScan(GeneticScanner::onNewResult);

			// Nach 1 sec wird der Test/Messung beendet.
			Thread.sleep(1_000);
		}
	}

	private static void onNewResult(final Result result) {
		System.out.println(result);
	}

}

final class Result {
	final EvolutionResult<DoubleGene, Vec<double[]>> current;
	final ISeq<Phenotype<DoubleGene, Vec<double[]>>> best;
	Result(
		final EvolutionResult<DoubleGene, Vec<double[]>> current,
		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> best
	) {
		this.current = current;
		this.best = best;
	}

	@Override
	public String toString() {
		return current.generation() + ": " +best.stream()
			.map(Objects::toString)
			.collect(Collectors.joining("; "));
	}
}
