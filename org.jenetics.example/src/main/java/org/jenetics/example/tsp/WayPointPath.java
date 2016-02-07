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

import java.io.IOException;
import java.time.Duration;

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
@JsonAdapter(WayPointPath.Adapter.class)
public class WayPointPath {

	private final WayPoint _from;
	private final WayPoint _to;
	private final double _distant;
	private final Duration _time;
	private final ISeq<Point> _path;

	private WayPointPath(
		final WayPoint from,
		final WayPoint to,
		final double distant,
		final Duration time,
		final ISeq<Point> path
	) {
		_from = requireNonNull(from);
		_to = requireNonNull(to);
		_distant = distant;
		_time = requireNonNull(time);
		_path = requireNonNull(path);
	}

	public WayPoint getFrom() {
		return _from;
	}

	public WayPoint getTo() {
		return _to;
	}

	public double getDistant() {
		return _distant;
	}

	public Duration getTime() {
		return _time;
	}

	public ISeq<Point> getPath() {
		return _path;
	}

	public static WayPointPath of(
		final WayPoint from,
		final WayPoint to,
		final double distant,
		final Duration time,
		final ISeq<Point> path
	) {
		return new WayPointPath(from, to, distant, time, path);
	}

	static final class Adapter extends TypeAdapter<WayPointPath> {

		private static final String FROM = "from";
		private static final String TO = "to";
		private static final String DISTANT = "distant";
		private static final String TIME = "time";
		private static final String PATH = "path";

		@Override
		public void write(final JsonWriter out, final WayPointPath path)
			throws IOException
		{
			final Point.Adapter pa = new Point.Adapter();
			final WayPoint.Adapter wpa = new WayPoint.Adapter();

			out.beginObject();
			out.name(FROM);
			wpa.write(out, path.getFrom());
			out.name(TO);
			wpa.write(out, path.getTo());
			out.name(DISTANT).value(path.getDistant());
			out.name(TIME).value(path.getTime().toMillis());
			out.name(PATH);
			out.beginArray();
			for (Point point : path.getPath()) {
				pa.write(out, point);
			}
			out.endArray();
			out.endObject();
		}

		@Override
		public WayPointPath read(final JsonReader in) throws IOException {
			return null;
		}

	}

}
