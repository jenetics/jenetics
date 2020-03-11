package io.jenetics.example.timeseries;

import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;

public class EvolutionPublisher<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends SubmissionPublisher<EvolutionResult<G, C>>
{

	private final EvolutionStream<G, C> _stream;

	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	public EvolutionPublisher(final EvolutionStream<G, C> stream) {
		_stream = stream.limit(er -> _proceed.get());
		_stream.forEach(this::submit);
	}

	@Override
	public void close() {
		_proceed.set(false);
		super.close();
	}


}
