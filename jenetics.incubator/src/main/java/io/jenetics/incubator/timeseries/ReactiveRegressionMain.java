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
package io.jenetics.incubator.timeseries;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import io.jenetics.engine.Codec;
import io.jenetics.engine.EvolutionParams;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.prog.regression.Complexity;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ReactiveRegressionMain {

	public static void main(final String[] args) throws InterruptedException {

		// The allowed operations.
		final ISeq<Op<Double>> operations = ISeq.of(
			MathOp.ADD,
			MathOp.SUB,
			MathOp.MUL
		);

		// The terminal operations.
		final ISeq<Op<Double>> terminals = ISeq.of(
			Var.of("x", 0),
			EphemeralConst.of(() -> (double)RandomRegistry.random().nextInt(10))
		);

		// The program (problem) encoding.
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec = Regression.codecOf(
				operations,
				terminals,
				5
			);

		// The error function.
		final Error<Double> error = Error.of(
			LossFunction::mse,
			Complexity.ofNodeCount(50)
		);

		// The sample source (time series).
		final PeriodicPublisher<List<Sample<Double>>> samples = new PeriodicPublisher<>(
			() -> List.of(),
			Duration.ofSeconds(5)
		);

		try (samples){
			// The regression analysis.
			final ReactiveRegression<Double> regression = new ReactiveRegression<>(
				codec,
				error,
				EvolutionParams.<ProgramGene<Double>, Double>builder().build(),
				50
			);

			try (regression) {
				samples.subscribe(regression);

				regression.subscribe(new Subscriber<>() {
					private Subscription _subscription;
					@Override
					public void onSubscribe(final Subscription subscription) {
						(_subscription = subscription).request(1);
					}
					@Override
					public void onNext(final RegressionResult<Double> result) {
						final TreeNode<Op<Double>> tree = TreeNode.ofTree(result.program());
						MathExpr.REWRITER.rewrite(tree);
						System.out.println(
							"Error: " + result.error() + ", program: " +
							MathExpr.parse(tree.toString())
						);

						_subscription.request(1);
					}
					@Override
					public void onError(final Throwable throwable) {
						throwable.printStackTrace();
					}
					@Override
					public void onComplete() {
						System.out.println("Finished");
					}
				});

				// Block until terminated.
				Thread.currentThread().join();
			}
		}
	}

}
