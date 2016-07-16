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

import java.io.IOException;
import java.time.ZonedDateTime;

import org.testng.annotations.Test;

import org.jenetics.internal.util.JAXBContextCache;

import org.jenetics.util.IO;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MetadataTest {

	@Test
	public void jaxb() throws IOException {
		JAXBContextCache.addPackage("org.jenetics.example.tsp.gpx");

		final Metadata object = Metadata.of(
			"Franz Wilhelmsötter",
			"Some importend description",
			Person.of(
				"Franz Wilhelmstötter",
				Email.of("franz.wilhelmstoetter", "gmail.com"),
				Link.of("http://jenetics.io")
			),
			Copyright.of("Me", 1982, "http://jenetics.io"),
			ISeq.of(
				Link.of("http://jenetics.io/1", "foo", "bar"),
				Link.of("http://jenetics.io/2", "foo", "bar"),
				Link.of("http://jenetics.io/3", "foo", "bar")
			),
			ZonedDateTime.now(),
			"foo, bar",
			Bounds.of(
				Latitude.ofDegrees(43.32),
				Longitude.ofDegrees(13.23),
				Latitude.ofDegrees(45.3232),
				Longitude.ofDegrees(11.21923)
			)
		);

		IO.jaxb.write(object, System.out);
	}


}
