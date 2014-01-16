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
package org.jenetics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-01-16 $</em>
 */
public class BitGeneTest extends GeneTester<BitGene> {

	@Override protected Factory<BitGene> getFactory() {
		return BitGene.FALSE;
	}

	@Test
	public void testGetValue() {
		assertEquals(BitGene.FALSE.getBit(), false);
		assertEquals(BitGene.ZERO.getBit(), false);
		assertEquals(BitGene.TRUE.getBit(), true);
		assertEquals(BitGene.ONE.getBit(), true);
	}

	@Test
	public void testCompareTo() {
		assertEquals(BitGene.ZERO.compareTo(BitGene.FALSE), 0);
		assertTrue(BitGene.FALSE.compareTo(BitGene.ONE) < 0);
		assertTrue(BitGene.TRUE.compareTo(BitGene.ZERO) > 0);
	}

	@Test
	public void objectSerializationCompatibility() throws IOException {
		String resource = "/org/jenetics/BitGene_TRUE.object";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.object.read(in);

			Assert.assertEquals(object, BitGene.TRUE);
		}

		resource = "/org/jenetics/BitGene_FALSE.object";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.object.read(in);

			Assert.assertEquals(object, BitGene.FALSE);
		}
	}

	@Test
	public void xmlSerializationCompatibility() throws IOException {
		String resource = "/org/jenetics/BitGene_TRUE.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.xml.read(in);

			Assert.assertEquals(object, BitGene.TRUE);
		}

		resource = "/org/jenetics/BitGene_FALSE.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.xml.read(in);

			Assert.assertEquals(object, BitGene.FALSE);
		}
	}

	/*
	@Test
	public void jaxbSerializationCompatibility() throws IOException {
		String resource = "/org/jenetics/BitGene_TRUE.jaxb.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.jaxb.read(BitGene.class, in);

			Assert.assertEquals(object, BitGene.TRUE);
		}

		resource = "/org/jenetics/BitGene_FALSE.jaxb.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object object = IO.jaxb.read(BitGene.class, in);

			Assert.assertEquals(object, BitGene.FALSE);
		}
	}
	*/


	public static void main(final String[] args) throws Exception {
		final Path basePath = Paths.get("/home/fwilhelm/Workspace/Development/Projects/Jenetics/org.jenetics/src/test/resources/org/jenetics/");
		//IO.object.write(BitGene.TRUE, basePath.resolve("BitGene_TRUE.object"));
		//IO.object.write(BitGene.FALSE, basePath.resolve("BitGene_FALSE.object"));
		//IO.xml.write(BitGene.TRUE, basePath.resolve("BitGene_TRUE.xml"));
		//IO.xml.write(BitGene.FALSE, basePath.resolve("BitGene_FALSE.xml"));
		//IO.jaxb.write(BitGene.TRUE, basePath.resolve("BitGene_TRUE.jaxb.xml"));
		//IO.jaxb.write(BitGene.FALSE, basePath.resolve("BitGene_FALSE.jaxb.xml"));

		IO.jaxb.write(BitGene.FALSE, System.out);

		String resource = "/org/jenetics/BitGene_FALSE.jaxb.xml";
		try (InputStream in = BitGeneTest.class.getResourceAsStream(resource)) {
			final Object object = IO.jaxb.read(BitGene.class, in);

			Assert.assertEquals(object, BitGene.FALSE);
		}
	}

}
