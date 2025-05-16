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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.metamodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.metamodel.property.PropertiesTest;

import io.jenetics.jpx.GPX;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MetaModelTest {

	private static final GPX AUSTRIA = gpx();

	private static GPX gpx() {
		try (var in = PropertiesTest.class
			.getResourceAsStream("/io/jenetics/incubator/metamodel/Austria.gpx"))
		{
			assert in != null;
			return GPX.Reader.DEFAULT.read(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Test(dataProvider = "objectPaths")
	public void pathAt(final Object object, final Path expected) {
		final var model = MetaModel.of(AUSTRIA);

		var path = model.pathOf(object);
		assertThat((Object)path.orElseThrow()).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] objectPaths() {
		return new Object[][] {
			{AUSTRIA.getWayPoints(), Path.of("{wayPoints}")},
			{AUSTRIA.getWayPoints().get(10), Path.of("wayPoints[10]")},
			{AUSTRIA.getWayPoints().get(12), Path.of("wayPoints[12]")},
			{AUSTRIA.getWayPoints().get(24), Path.of("wayPoints[24]")}
		};
	}

}
