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
package org.jenetics.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.BitChromosome;
import org.jenetics.util.bit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
@XmlRootElement(name = "org.jenetics.BitChromosome")
@XmlAccessorType(XmlAccessType.FIELD)
public final class BitChromosomeXML {
	@XmlAttribute
	int length;
	@XmlAttribute double probability;
	@XmlValue
	String value;

	public final static class Adapter
		extends XmlAdapter<BitChromosomeXML, BitChromosome>
	{
		@Override
		public BitChromosomeXML marshal(final BitChromosome chromosome) {
			final BitChromosomeXML xml = new BitChromosomeXML();
			xml.length = chromosome.length();
			xml.probability = 0.5;
			xml.value = bit.toByteString(chromosome.toByteArray());
			return xml;
		}

		@Override
		public BitChromosome unmarshal(final BitChromosomeXML xml) {
			final BitChromosome chromosome = new BitChromosome(
				bit.fromByteString(xml.value)
			);
			//chromosome._p = xml.probability;
			//chromosome._length = xml.length;
			return chromosome;
		}
	}
}
