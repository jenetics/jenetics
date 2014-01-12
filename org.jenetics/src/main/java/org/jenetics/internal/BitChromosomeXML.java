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
import javax.xml.bind.annotation.XmlRootElement;

import org.jenetics.internal.util.XMLAdapter;

import org.jenetics.BitChromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-12 $</em>
 * @since @__version__@
 */
@XmlRootElement(name = "org.jenetics.BitChromosome")
@XmlAccessorType(XmlAccessType.FIELD)
public class BitChromosomeXML {
	public int length;
	public double p;

	public static final class Adapter
		extends XMLAdapter<BitChromosomeXML, BitChromosome>
	{

		@Override
		public Class<BitChromosomeXML> getValueType() {
			return BitChromosomeXML.class;
		}

		@Override
		public Class<BitChromosome> getBoundType() {
			return BitChromosome.class;
		}

		@Override
		public BitChromosomeXML marshal(BitChromosome v) throws Exception {
			final BitChromosomeXML xml = new BitChromosomeXML();
			xml.length = v.length();
			xml.p = v.getOneProbability();
			return xml;
		}

		@Override
		public BitChromosome unmarshal(BitChromosomeXML v) throws Exception {
			return null;
		}
	}
}
