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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jenetics.internal.util.XMLAdapter;

import org.jenetics.BitGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public class BitGeneXMLAdapter extends XMLAdapter<BitGeneXML, BitGene> {

	public final Class<BitGeneXML> valueType = BitGeneXML.class;
	public final Class<BitGene> boundType = BitGene.class;

	@Override
	public Class<BitGeneXML> getValueType() {
		return BitGeneXML.class;
	}

	@Override
	public Class<BitGene> getBoundType() {
		return BitGene.class;
	}

	@Override
	public BitGeneXML marshal(final BitGene v) {
		BitGeneXML gene = new BitGeneXML();
		gene.value = v.booleanValue();
		return gene;
	}

	@Override
	public BitGene unmarshal(final BitGeneXML v) {
		return BitGene.valueOf(v.value);
	}
}
