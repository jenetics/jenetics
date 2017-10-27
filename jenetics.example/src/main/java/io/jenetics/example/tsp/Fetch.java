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

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.jenetics.jpx.Point;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Fetch {

	static final String ROUTE_PATH = "/home/fwilhelm/Workspace/Development/Projects/" +
		"Jenetics/org.jenetics.example/src/main/resources/org/jenetics/example/tsp/routes/";

	static final String URL_PATTERN = "http://open.mapquestapi.com/directions/v2/route?" +
		"key=${MAP_QUEST_API_KEY}&callback=renderAdvancedNarrative&" +
		"avoids=Limited Access&avoids=Toll road&avoids=Approximate Seasonal Closure&" +
		"avoids=Unpaved&avoids=Ferry&avoids=Country border crossing&outFormat=json&" +
		"routeType=fastest&timeType=1&narrativeType=text&enhancedNarrative=false&" +
		"shapeFormat=raw&generalize=0&locale=en_US&unit=k&" +
		"from=${FROM}0&to=${TO}&" +
		"drivingStyle=2&highwayEfficiency=21.0";

	public static void main(final String[] args) throws Exception {
		final ISeq<Point> points = points();
		for (int i = 0; i < points.length(); ++i) {
			final String from = toString(points.get(i));

			for (int j = i; j < points.length(); ++j) {
				final String to = toString(points.get((j + 1)%points.length()));
				final File routeFile = file(from, to);

				if (!routeFile.isFile()) {
					final URL url = new URL(URL_PATTERN
						.replace("${MAP_QUEST_API_KEY}", System.getenv("MAP_QUEST_API_KEY"))
						.replace("${FROM}", from)
						.replace("${TO}", to)
						.replace(" ", "%20"));

					System.out.println("ROUTE: " + routeFile.getName());
					final String output = GET(url);
					Files.write(routeFile.toPath(), output.getBytes());
				}
			}
		}
	}

	private static File file(final String from, final String to) {
		return new File(
			ROUTE_PATH,
			format("%s--%s.path", from, to));
	}

	private static String GET(final URL url) throws Exception {
		final HttpURLConnection http = (HttpURLConnection)url.openConnection();
		try {
			http.setRequestMethod("GET");
			http.setDoOutput(true);
			http.setRequestProperty("Accept", "application/json");

			/*
			if (http.getResponseCode() != 200) {
				throw new RuntimeException(
					"HTTP GET Request Failed with Error code : " +
						http.getResponseCode()
				);
			}
			*/

			final BufferedReader responseBuffer =
				new BufferedReader(new InputStreamReader(http.getInputStream()));

			String output;
			final StringBuilder out = new StringBuilder();
			while ((output = responseBuffer.readLine()) != null) {
				out.append(output);
			}

			return out.toString();
		} finally {
			http.disconnect();
		}
	}

	private static String toString(final Point point) {
		return format("%f,%f",
			point.getLatitude(),
			point.getLongitude()
		);
	}

	private static ISeq<Point> points() throws Exception {
		final Scanner scanner = new Scanner(Fetch.class.getResourceAsStream(
			"/org/jenetics/example/tsp/AustrianDistrictsCities.csv"
		));
		final List<Point> pts = new ArrayList<>();
		String line = scanner.nextLine();
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			System.out.println(line);
			if (!line.trim().isEmpty()) {
				//pts.add(Point.ofCSVLine(line.split(",")));
			}
		}

		return pts.stream().collect(ISeq.toISeq());
	}

}
