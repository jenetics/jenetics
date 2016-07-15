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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.jenetics.example.tsp.gpx.GPX;
import org.jenetics.example.tsp.gpx.TrackSegment;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class WayPointPathParser {

	/*
	static final String ROUTE_PATH = "/home/fwilhelm/Workspace/Development/Projects/" +
		"Jenetics/org.jenetics.example/src/main/resources/org/jenetics/example/tsp/routes/";

	static final String PATH = "47.059320,16.324490--47.726070,16.081210.path";
	*/

	private final Path _basePath;

	public WayPointPathParser(final Path basePath) {
		_basePath = requireNonNull(basePath);
	}

	public WayPointPath parse(final WayPoint from, final WayPoint to)
		throws IOException
	{
		final Path file = Paths.get(
			_basePath.toString(),
			format(
				"%s--%s.path",
				toString(from.getPoint()), toString(to.getPoint())
			)
		);

		final String raw = readString(file);

		final ISeq<Point> path = shapePoints(raw).stream().collect(ISeq.toISeq());
		final double distant = jsonValue(raw, "distance");
		final Duration time = Duration.ofMinutes((long)jsonValue(raw, "time"));

		return WayPointPath.of(from, to, distant, time, path);
	}

	private static String toString(final Point point) {
		return format("%f,%f",
			point.getLatitude(),
			point.getLongitude()
		);
	}

	private static String readString(final Path file) throws IOException {
		return  new String(Files.readAllBytes(file));
	}

//	public static void main(final String[] args) throws Exception {
//		final WayPointPathParser parser = new WayPointPathParser(Paths.get(
//			Fetch.ROUTE_PATH
//		));
//
//		final Gson gson = new GsonBuilder()
//			.setPrettyPrinting()
//			.create();
//
//		try (InputStream in = WayPoints.class
//			.getResourceAsStream("/org/jenetics/example/tsp/waypoints/Österreich.json"))
//		{
//			final ISeq<WayPoint> points = WayPoints.read(in).getPoints();
//			for (int i = 0; i < points.length(); ++i) {
//				final WayPoint from = points.get(i);
//
//				for (int j = i; j < points.length(); ++j) {
//					final WayPoint to = points.get((j + 1)%points.length());
//
//					try {
//						final WayPointPath path = parser.parse(from, to);
//						final File file = new File(
//							"/home/fwilhelm/Temp/out.xml/" +
//								from.getName() + "---" + to.getName() + ".gpx"
//						);
//
//						final GPX gpx = new GPX();
//						gpx.addWayPoint(org.jenetics.example.tsp.gpx.WayPoint.of(
//							from.getName(),
//							from.getPoint().getLatitude(),
//							from.getPoint().getLongitude()
//						));
//						gpx.addWayPoint(org.jenetics.example.tsp.gpx.WayPoint.of(
//							to.getName(),
//							to.getPoint().getLatitude(),
//							to.getPoint().getLongitude()
//						));
//
//						final org.jenetics.example.tsp.gpx.Route route =
//							new org.jenetics.example.tsp.gpx.Route(from.getName() + "---" + to.getName());
//						path.getPath()
//							.map(p -> org.jenetics.example.tsp.gpx.WayPoint.of(
//								p.getLatitude(),
//								p.getLongitude()
//							))
//							.forEach(route::add);
//
//						gpx.addRoute(route);
//
//						try (OutputStream out = new FileOutputStream(file)) {
//							GPX.write(gpx, out);
//						}
//
//						/*
//						try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
//							final WayPointPath.Adapter wpa = new WayPointPath.Adapter();
//							wpa.write(writer, path);
//						}
//						*/
//					} catch (Exception e) {
//						System.out.println("Not found: " + from + "--" + to);
//					}
//				}
//			}
//		}


		/*
		final String raw = new String(Files.readAllBytes(
			new File(ROUTE_PATH, PATH).toPath()));
		System.out.println(raw);

		final List<Point> points = shapePoints(raw);
		System.out.println(points);
		System.out.println("Distance: " + jsonValue(raw, "distance"));
		System.out.println("Time: " + jsonValue(raw, "time"));

		final WayPointPath path = WayPointPath.of(
			WayPoint.of("Linz", Point.of(48, 116)),
			WayPoint.of("Salzburg", Point.of(47, 115)),
			234, Duration.ofMinutes(234),
			points.stream().collect(ISeq.toISeq())
		);

		final GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		final Gson gson = builder.create();
		System.out.println(gson.toJson(path));
		*/
	//}

	private static List<Point> shapePoints(final String json)
		throws IOException
	{
		final List<Point> points = new ArrayList<>();

		try (JsonReader reader =
				 new JsonReader(new StringReader(shapePointsJson(json))))
		{
			reader.beginObject();
			reader.nextName();
			reader.beginArray();
			while (reader.hasNext()) {
				final double lat = reader.nextDouble();
				final double lng = reader.nextDouble();
				points.add(Point.of(lat, lng));
			}
			reader.endArray();
			reader.endObject();
		}

		System.out.println("SIZE: " + points.size());
		return points;
	}

	private static String shapePointsJson(final String raw) {
		final int start = raw.indexOf("\"shapePoints\"");
		final int end = raw.indexOf("]", start);

		return "{" + raw.substring(start, end + "]".length()) + "}";
	}

	private static double jsonValue(final String raw, final String name) {
		final String pattern = "\"" + name + "\":";
		final int start = raw.indexOf(pattern);
		final int end = raw.indexOf(",", start);

		final String numberString = raw.substring(start + pattern.length(), end);
		return Double.parseDouble(numberString);
	}

}
