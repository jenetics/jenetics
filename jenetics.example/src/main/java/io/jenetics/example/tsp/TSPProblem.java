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
package io.jenetics.example.tsp;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.Route;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TSPProblem implements Problem<Route, EnumGene<Integer>, Double> {

	private final ISeq<Point> _points;

	private TSPProblem(final ISeq<Point> points) {
		_points = requireNonNull(points);
	}

	@Override
	public Function<Route, Double> fitness() {
		return null;
	}

	@Override
	public Codec<Route, EnumGene<Integer>> codec() {
		return null;
	}

	public static TSPProblem of(final ISeq<Point> points) {
		return new TSPProblem(points);
	}
}
