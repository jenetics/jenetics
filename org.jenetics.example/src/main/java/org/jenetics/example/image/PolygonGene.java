/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenetics.example.image;

import static java.util.Objects.requireNonNull;

import org.jenetics.Gene;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Mean;

/**
 * Represents a fixed size polygon with its fill color.
 */
final class PolygonGene
	implements
		Gene<Polygon, PolygonGene>,
		Mean<PolygonGene>
{
	private final Polygon _polygon;

	private PolygonGene(final Polygon polygon) {
		_polygon = requireNonNull(polygon);
	}

	@Override
	public Polygon getAllele() {
		return _polygon;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public PolygonGene newInstance() {
	  return new PolygonGene(Polygon.newRandom(_polygon.length()));
	}

	@Override
	public PolygonGene newInstance(final Polygon polygon) {
		return of(polygon);
	}

	@Override
	public PolygonGene mean(final PolygonGene other) {
		return of(getAllele().mean(other.getAllele()));
	}

	static ISeq<PolygonGene> seq(final int polygonCount, final int polygonLength) {
		return MSeq.<PolygonGene>ofLength(polygonCount)
			.fill(() -> of(Polygon.newRandom(polygonLength)))
			.toISeq();
	}

	public static PolygonGene of(final Polygon polygon) {
		return new PolygonGene(polygon);
	}

}
