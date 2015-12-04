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
package org.jenetics.example.tsp;

import static java.util.Objects.requireNonNull;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !_version_!
 * @version !_version_!
 */
public final class Route {

	private final ISeq<Location> _points;
	private final Location _min;
	private final Location _max;
	private final Location _range;

	private Route(final ISeq<Location> points) {
		_points = requireNonNull(points);

		final double[] latitudes = _points.stream()
			.mapToDouble(Location::getLatitude)
			.toArray();

		final double[] longitudes = _points.stream()
			.mapToDouble(Location::getLongitude)
			.toArray();

		_min = Location.of(
			Arrays.stream(latitudes).min().orElse(0),
			Arrays.stream(longitudes).min().orElse(0)
		);

		_max = Location.of(
			Arrays.stream(latitudes).max().orElse(0),
			Arrays.stream(longitudes).max().orElse(0)
		);

		_range = _min.minus(_max);
	}

	public ISeq<Location> getPoints() {
		return _points;
	}

	public double length() {
		double sum = 0;

		if (_points.length() > 1) {
			for (int i = 1; i < _points.length(); ++i) {
				sum += _points.get(i - 1).dist(_points.get(i));
			}
			sum += _points.get(_points.length() - 1).dist(_points.get(0));
		}

		return sum;
	}

	public void draw(final Graphics2D g, final int width, final int height) {
		final ISeq<Location> points = _points.stream()
			.map(p -> scale(p, width, height))
			.collect(ISeq.toISeq());

		if (!points.isEmpty()) {
			final GeneralPath path = new GeneralPath();
			final Location point = points.get(0);
			path.moveTo(
				point.getLongitude(),
				point.getLatitude()
			);

			points.subSeq(1).forEach(p -> {
				path.lineTo(p.getLongitude(), p.getLatitude());
			});

			path.closePath();
			g.draw(path);
		}
	}

	private Location scale(final Location p, final int width, final int height) {
		final double scaleX = _range.getLatitude()/width;
		final double scaleY = _range.getLongitude()/height;
		final double scale = Math.min(scaleX, scaleY);

		return p.mult(scale);
	}

}
