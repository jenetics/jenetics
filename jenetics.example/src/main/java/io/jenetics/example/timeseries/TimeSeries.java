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
package io.jenetics.example.timeseries;

import static java.util.Objects.requireNonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import io.jenetics.DoubleGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeries
	implements
		EvolutionStreamable<DoubleGene, Double>,
		Observer<double[][]>
{

	private Regression _regression;
	private Disposable _disposable;
	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private final Engine<DoubleGene, Double> _engine;

	public TimeSeries(final Engine<DoubleGene, Double> engine) {
		_engine = requireNonNull(engine);
	}

	@Override
	public EvolutionStream<DoubleGene, Double>
	stream(final Supplier<EvolutionStart<DoubleGene, Double>> start) {
		return null;
	}

	@Override
	public EvolutionStream<DoubleGene, Double>
	stream(final EvolutionInit<DoubleGene> init) {
		return null;
	}

	@Override
	public void onSubscribe(final Disposable disposable) {
		_disposable = disposable;
	}

	@Override
	public void onNext(final double[][] doubles) {
		_regression = new Regression(doubles);
	}

	@Override
	public void onError(final Throwable e) {
		_proceed.set(false);
	}

	@Override
	public void onComplete() {
		_proceed.set(false);
	}


	public static void main(final String[] args) {
	}

}
