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
package org.jenetics.util;

import static java.lang.String.format;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public class IOTest {

	@XmlRootElement(name = "data-class")
	@XmlType(name = "DataClass")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class DataClass {
		@XmlAttribute public String name;
		@XmlValue public String value;
	}

	public static void main(final String[] args) throws IOException {
		final DataClass data = new DataClass();
		data.name = "name";
		data.value = "value";

		IO.JAXB.register(DataClass.class);
		IO.jaxb.write(data, System.out);
	}

	@Test
	public void jaxbRegister() throws IOException {
		IO.JAXB.register(DataClass.class);

		final DataClass data = new DataClass();
		data.name = "name";
		data.value = "value";

		IO.jaxb.write(data, System.out);
		System.out.flush();
	}

	@Test(expectedExceptions = IOException.class)
	public void jaxbDeregister() throws IOException {
		IO.JAXB.deregister(DataClass.class);

		final DataClass data = new DataClass();
		data.name = "name";
		data.value = "value";

		IO.jaxb.write(data, System.out);
	}

}
