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
package io.jenetics.prog.op;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class OperationTest {

	@Test(expectedExceptions = {NotSerializableException.class})
	public void notSerialize() throws IOException {
		final Operation<Double> object = new Operation<>(
			"some operation name",
			2,
			v -> Math.pow(v[0], v[1])
		);
		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}

	@Test
	public void serialize() throws IOException {
		final Operation<Double> object = new Operation<>(
			"some operation name",
			2,
			(Function<Double[], Double> & Serializable)v -> v[0] + v[1]
		);
		final byte[] data = IO.object.toByteArray(object);
		@SuppressWarnings("unchecked")
		final Operation<Double> op = (Operation<Double>)IO.object.fromByteArray(data);
		Assert.assertEquals(IO.object.fromByteArray(data), object);

		Assert.assertEquals(
			op.apply(new Double[]{1.0, 2.0}).doubleValue(),
			3.0
		);
	}
}
