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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jenetics.example.tsp.gpx.GPX;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@JsonAdapter(WayPoint.Adapter.class)
public class WayPoint {

	private final String _name;
	private final Point _point;

	private WayPoint(final String name, final Point point) {
		_name = requireNonNull(name);
		_point = requireNonNull(point);
	}

	public String getName() {
		return _name;
	}

	public Point getPoint() {
		return _point;
	}

	@Override
	public String toString() {
		return format("WayPoint[name=%s, %s]", _name, _point);
	}

	public static WayPoint of(final String name, final Point point) {
		return new WayPoint(name, point);
	}



	static final class Adapter extends TypeAdapter<WayPoint> {
		private static final String NAME = "name";
		private static final String POINT = "point";

		@Override
		public void write(final JsonWriter out, final WayPoint point)
			throws IOException
		{
			out.beginObject();
			out.name(NAME).value(point.getName());
			out.name(POINT);
			new Point.Adapter().write(out, point.getPoint());
			out.endObject();
		}

		@Override
		public WayPoint read(final JsonReader in) throws IOException {
			String name = null;
			Point point = null;

			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
					case NAME: name = in.nextString(); break;
					case POINT: point = new Point.Adapter().read(in); break;
				}
			}
			in.endObject();

			return of(name, point);
		}
	}

	public static void main(final String[] args) throws Exception {
		final Map<String, GPX> points = points();
		for (Entry<String, GPX> cities : points.entrySet()) {
			final File file = new File(
				"/home/fwilhelm/Temp",
				cities.getKey() + ".gpx"
			);

			/*
			final Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

			final GPX gpx = WayPoints.of(
				cities.getKey(),
				cities.getValue().stream().collect(ISeq.toISeq())
			);
			*/

			//System.out.println(gson.toJson(points));

			/*
			try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
				final WayPoints.Adapter wpa = new WayPoints.Adapter();
				wpa.write(writer, wpoints);
			}
			*/

			try (OutputStream out = new FileOutputStream(file)) {
				GPX.write(cities.getValue(), out);
			}
		}

		final GPX gpx = null;//new GPX();
		//points.values().stream()
		//	.flatMap(g -> g.getWayPoints().stream())
		//	.forEach(gpx::addWayPoint);

		try (OutputStream out = new FileOutputStream("/home/fwilhelm/Temp/Österreich.gpx")) {
			GPX.write(gpx, out);
		}

		/*
		final ISeq<WayPoint> all = points.values().stream()
			.flatMap(v -> v.stream())
			.collect(ISeq.toISeq());

		final WayPoints allWayPoints = WayPoints.of("Österreich", all);
		try (JsonWriter writer = new GsonBuilder()
			.setPrettyPrinting()
			.create().newJsonWriter(new FileWriter("/home/fwilhelm/Temp/Österreich.json")))
		{
			final WayPoints.Adapter wpa = new WayPoints.Adapter();
			wpa.write(writer, allWayPoints);
		}
		*/
	}

	private static Map<String, GPX> points() throws Exception {
		final Scanner scanner = new Scanner(Fetch.class.getResourceAsStream(
			"/org/jenetics/example/tsp/AustrianDistrictsCities.csv"
		));

		final Map<String, GPX> pts = new HashMap<>();
		String line = scanner.nextLine();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			System.out.println(line);

			if (!line.trim().isEmpty()) {
				final String[] parts = line.split(",");

				final String city = parts[0];
				final String state = parts[1];
				final double lat = Double.parseDouble(parts[2]);
				final double log = Double.parseDouble(parts[3]);
				final double ele = Double.parseDouble(parts[4]);

				//final GPX gpx = pts.computeIfAbsent(state, s -> new GPX());
				//gpx.addWayPoint(org.jenetics.example.tsp.gpx.WayPoint.of(city, lat, log, ele));
			}
		}

		return pts;
	}

}
