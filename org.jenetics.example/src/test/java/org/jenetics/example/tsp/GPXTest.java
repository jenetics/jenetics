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

import static org.jenetics.internal.util.jaxb.context;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.Marshaller;

import org.testng.annotations.Test;

import org.jenetics.example.tsp.gpx.GPX;
import org.jenetics.example.tsp.gpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GPXTest {

	/*
	@Test
	public void writeGPX() throws IOException {
		final GPX gpx = new GPX();
		gpx.addWayPoint(WayPoint.of(23, 34));
		gpx.addWayPoint(WayPoint.of(24, 35));

		GPX.write(gpx, System.out);
	}

	@Test
	public void readGPX() throws IOException {
		final String resource = "/org/jenetics/example/tsp/gpx_track.gpx";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.read(in);
			GPX.write(gpx, System.out);
		}
	}
	*/

	public static void write(final Object object, final OutputStream out)
		throws IOException
	{
		try {
			final Marshaller marshaller = context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(marshal(object), out);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
