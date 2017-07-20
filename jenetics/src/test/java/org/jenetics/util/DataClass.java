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

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "data-class")
@XmlType(name = "DataClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataClass {
	@XmlAttribute public String name;
	@XmlValue public String value;

	public DataClass(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public DataClass() {
	}

	public static void main(final String[] args) throws IOException {
		IO.JAXB.register(DataClass.class);

		final DataClass data = new DataClass("some name", "some value");
		IO.jaxb.write(data, System.out);
	}
}
