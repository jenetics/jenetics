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
	public default Optional<Length> getElevation() {
		return Optional.empty();
	}

	/**
	 * Creation/modification timestamp for the point.
	 *
	 * @return creation/modification timestamp for the point
	 */
	public default Optional<ZonedDateTime> getTime() {
		return Optional.empty();
	}

	/**
	 * Calculate the distance between points on an ellipsoidal earth model.
	 *
	 * @see <a href="http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf">DIRECT AND
	 *               INVERSE SOLUTIONS OF GEODESICS 0 THE ELLIPSOID
	 *               WITH APPLICATION OF NESTED EQUATIONS</a>
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">
	 *     Vincenty solutions of geodesics on the ellipsoid</a>
	 *
	 * @param end the end point
	 * @return the distance between {@code this} and {@code end} in meters
	 * @throws NullPointerException if the {@code end} point is {@code null}
	 */
	public default Length distance(final Point end) {
		return Points.distance(this, end);
	}

}
