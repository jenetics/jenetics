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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@JsonAdapter(WayPoints.Adapter.class)
public class WayPoints {

	private final String _name;
	private final ISeq<WayPoint> _points;

	private WayPoints(final String name, final ISeq<WayPoint> points) {
		_name = requireNonNull(name);
		_points = requireNonNull(points);
	}

	public String getName() {
		return _name;
	}

	public ISeq<WayPoint> getPoints() {
		return _points;
	}

	public static WayPoints of(final String name, final ISeq<WayPoint> points) {
		return new WayPoints(name, points);
	}

	public static WayPoints read(final File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			return read(in);
		}
	}

	public static WayPoints read(final InputStream in) throws IOException {
		final JsonReader reader = new JsonReader(new InputStreamReader(in));
		return new Adapter().read(reader);
	}

	static final class Adapter extends TypeAdapter<WayPoints> {

		private static final String NAME = "name";
		private static final String POINTS = "way-points";

		@Override
		public void write(final JsonWriter out, final WayPoints points)
			throws IOException
		{
			final WayPoint.Adapter wpa = new WayPoint.Adapter();

			out.beginObject();
			out.name(NAME).value(points.getName());
			out.name(POINTS);
			out.beginArray();
			for (WayPoint point : points.getPoints()) {
				wpa.write(out, point);
			}
			out.endArray();
			out.endObject();
		}

		@Override
		public WayPoints read(final JsonReader in) throws IOException {
			final WayPoint.Adapter wpa = new WayPoint.Adapter();

			String name = null;
			final List<WayPoint> points = new ArrayList<>();

			in.beginObject();
			while (in.hasNext()) {
				final String n = in.nextName();
				switch (n) {
					case NAME:
						name = in.nextString();
						break;
					case POINTS:
						in.beginArray();
						while (in.hasNext()) {
							points.add(wpa.read(in));
						}
						in.endArray();
				}
			}
			in.endObject();

			return of(name, points.stream().collect(ISeq.toISeq()));
		}
	}



	public static void main(final String[] args) throws IOException {
		final String name = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/org.jenetics.example/src/main/resources/org/jenetics/" +
			"example/tsp/waypoints/Tirol.json";

		try (InputStream in = WayPoints.class
			.getResourceAsStream("/org/jenetics/example/tsp/waypoints/Österreich.json"))
		{
			final WayPoints points = WayPoints.read(in);
			System.out.println(points.getPoints());
		}

	}

}
