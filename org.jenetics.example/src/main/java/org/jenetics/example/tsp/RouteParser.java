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

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteParser {

	static final String ROUTE_PATH = "/home/fwilhelm/Workspace/Development/Projects/" +
		"Jenetics/org.jenetics.example/src/main/resources/org/jenetics/example/tsp/routes/";

	static final String PATH = "47.059320,16.324490--47.726070,16.081210.path";

	public static void main(final String[] args) throws Exception {
		final String raw = new String(Files.readAllBytes(
			new File(ROUTE_PATH, PATH).toPath()));

		final List<Point> points = shapePoints(raw);
		System.out.println(points);
	}

	private static List<Point> shapePoints(final String json) throws Exception {
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
				points.add(Point.ofDegrees(lat, lng));
			}
			reader.endArray();
			reader.endObject();
		}

		System.out.println("SIZE: " + points.size());
		return points;
	}

	static String shapePointsJson(final String raw) {
		final int start = raw.indexOf("\"shapePoints\"");
		final int end = raw.indexOf("]", start);

		return "{" + raw.substring(start, end + "]".length()) + "}";
	}

	static JsonReader parse(final String raw) {
		String result = raw;
		if (result.startsWith("renderAdvancedNarrative(")) {
			result = result.substring("renderAdvancedNarrative(".length());
		}
		if (result.endsWith(");")) {
			result = result.substring(0, result.length() - ");".length());
		}

		return new JsonReader(new StringReader(result));
	}

}
