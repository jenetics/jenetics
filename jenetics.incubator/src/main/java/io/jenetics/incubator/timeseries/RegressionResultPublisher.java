package io.jenetics.incubator.timeseries;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.StreamPublisher;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.regression.Sample;

public class RegressionResultPublisher<T>
	extends SubmissionPublisher<RegressionResult<T>>
	implements Flow.Processor<List<? extends Sample<? extends T>>, RegressionResult<T>>
{

	private final Stream<EvolutionResult<ProgramGene<T>, Double>> _evolution;

	private final StreamPublisher<RegressionResult<T>> _publisher;

	public RegressionResultPublisher(Stream<EvolutionResult<ProgramGene<T>, Double>> evolution) {
		_evolution = requireNonNull(evolution);
		_publisher = new StreamPublisher<>();
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
	}

	@Override
	public void onNext(final List<? extends Sample<? extends T>> samples) {
	}

	@Override
	public void onError(final Throwable throwable) {
	}

	@Override
	public void onComplete() {
	}

}
