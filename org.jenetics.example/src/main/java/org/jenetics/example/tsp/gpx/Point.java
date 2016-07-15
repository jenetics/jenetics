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
package org.jenetics.example.tsp.gpx;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * A geographic point with optional elevation and time.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Point {

	/**
	 * The latitude of the point, WGS84 datum.
	 *
	 * @return the latitude of the point
	 */
	public Latitude getLatitude();

	/**
	 * The longitude of the point, WGS84 datum.
	 *
	 * @return the longitude of the point
	 */
	public Longitude getLongitude();

	/**
	 * The elevation (in meters) of the point.
	 *
	 * @return the elevation (in meters) of the point
	 */
	public Optional<Length> getElevation();

	/**
	 * Creation/modification timestamp for the point.
	 *
	 * @return creation/modification timestamp for the point
	 */
	public Optional<ZonedDateTime> getTime();

	/**
	 * Return the distance between {@code this} and the {@code other}
	 * {@code WayPoint}.
	 *
	 * @param other the second way-point
	 * @return the distance between {@code this} and the {@code other}
	 * {@code WayPoint}
	 * @throws NullPointerException if the {@code other} way-point is {@code null}
	 */
	public default Length distance(final Point other) {
		return Points.distance(this, other);
	}
}
