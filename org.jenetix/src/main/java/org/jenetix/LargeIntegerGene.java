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
package org.jenetix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.context.StackContext;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.LargeInteger;

import org.jenetics.NumberGene;

import org.jenetix.util.LargeIntegerRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-25 $</em>
 */
public class LargeIntegerGene
	extends NumberGene<LargeInteger, LargeIntegerGene>
{
	private static final long serialVersionUID = 1L;

	private static final LargeInteger TWO = LargeInteger.valueOf(2);

	LargeIntegerGene() {
	}

	@Override
	protected Builder<LargeInteger, LargeIntegerGene> getBuilder() {
		return Builder;
	}

	public LargeIntegerGene divide(final LargeIntegerGene gene) {
		return newInstance(_value.divide(gene._value));
	}

	@Override
	public LargeIntegerGene mean(final LargeIntegerGene that) {
		StackContext.enter();
		try {
			return newInstance(StackContext.outerCopy(
				_value.plus(that._value.minus(_value).divide(TWO))
			));
		} finally {
			StackContext.exit();
		}
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	private static final ObjectFactory<LargeIntegerGene>
	FACTORY = new ObjectFactory<LargeIntegerGene>() {
		@Override protected LargeIntegerGene create() {
			return new LargeIntegerGene();
		}
	};

	public static final Builder<LargeInteger, LargeIntegerGene>
	Builder = new Builder<LargeInteger, LargeIntegerGene>() {

		@Override
		protected LargeInteger next(
			final Random random,
			final LargeInteger min,
			final LargeInteger max
		) {
			return LargeIntegerRandom.next(random, min, max);
		}

		@Override
		protected LargeInteger box(final Number value) {
			return LargeInteger.valueOf(value.longValue());
		}

		@Override
		public LargeIntegerGene build(
			final LargeInteger value,
			final LargeInteger min,
			final LargeInteger max
		) {
			final LargeIntegerGene gene = FACTORY.object();
			gene.set(value, min, max);
			return gene;
		}

	};


	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<LargeIntegerGene>
	XML = new XMLFormat<LargeIntegerGene>(LargeIntegerGene.class)
	{
		private static final String MIN = "min";
		private static final String MAX = "max";

		private final static String MIN_VALUE = "0";
		private final static String MAX_VALUE = "1000000";

		@Override
		public LargeIntegerGene newInstance(
			final Class<LargeIntegerGene> cls, final InputElement element
		)
			throws XMLStreamException
		{
			final LargeInteger min = LargeInteger.valueOf(
				element.getAttribute(MIN, MIN_VALUE)
			);
			final LargeInteger max = LargeInteger.valueOf(
				element.getAttribute(MAX, MAX_VALUE)
			);
			final LargeInteger value = element.<LargeInteger>getNext();
			return LargeIntegerGene.Builder.build(value, min, max);
		}
		@Override
		public void write(final LargeIntegerGene gene, final OutputElement element)
			throws XMLStreamException
		{
			element.setAttribute(MIN, gene.getMin().toString());
			element.setAttribute(MAX, gene.getMax().toString());
			element.add(gene.getAllele());
		}
		@Override
		public void read(final InputElement element, final LargeIntegerGene gene)
			throws XMLStreamException
		{
		}
	};


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeObject(_value);
		out.writeObject(_min);
		out.writeObject(_max);
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		set(
			(LargeInteger)in.readObject(),
			(LargeInteger)in.readObject(),
			(LargeInteger)in.readObject()
		);
	}


}








