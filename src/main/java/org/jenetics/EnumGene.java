/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *  
 */
package org.jenetics;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.io.IOException;
import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.text.TextFormat;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Factory;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class EnumGene<E extends Enum<E>> 
	implements 
		Gene<E, EnumGene<E>>, 
		Mean<EnumGene<E>>, 
		Realtime, 
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private E _value;
	
	EnumGene() {
	}
	
	@Override
	public E getAllele() {
		return _value;
	}
	
	/**
	 * Return the gene which lies between {@code this} and {@code that}, 
	 * according to it's ordinal number ({@link Enum#ordinal()}).
	 * 
	 * @return the gene which lies between {@code this} and {@code that}.
	 */
	@Override
	public EnumGene<E> mean(final EnumGene<E> that) {
		nonNull(that, "Enum value");
		
		if (that._value == _value) {
			return that;
		} else {
			final Class<?> type = that.getClass();
			@SuppressWarnings("unchecked")
			final E[] values = (E[])type.getEnumConstants();
			final int ordinal = (that.getAllele().ordinal() + _value.ordinal())/2;
			return newInstance(values[ordinal]);
		}
	}
	
	/**
	 * @return always {@code true}.
	 */
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public EnumGene<E> copy() {
		return valueOf(_value);
	}
	
	@Override
	public EnumGene<E> newInstance() {
		final Random random = RandomRegistry.getRandom();
		final E[] values = _value.getDeclaringClass().getEnumConstants();
		final int index = random.nextInt(values.length);
		
		return valueOf(values[index]);
	}
	
	/**
	 * Return the {@link Factory} view of this gene.
	 * 
	 * @return the {@link Factory} view of this gene.
	 */
	Factory<EnumGene<E>> asFactory() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_value).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof EnumGene<?>)) {
			return false;
		}
		
		EnumGene<?> gene = (EnumGene<?>)obj;
		return eq(_value, gene._value);
	}
	

	@SuppressWarnings({"rawtypes" })
	private static final ObjectFactory<EnumGene> FACTORY = 
		new ObjectFactory<EnumGene>() 
	{
		@Override protected EnumGene<?> create() {
			return new EnumGene();
		}
	};
	
	static <T extends Enum<T>> EnumGene<T> newInstance(final T value) {
		@SuppressWarnings("unchecked")
		EnumGene<T> e = FACTORY.object();
		e._value = value;
		return e;
	}
	
	public static <T extends Enum<T>> EnumGene<T> valueOf(final T value) {
		return newInstance(nonNull(value, "Enum value"));
	}

	@Override
	public Text toText() {
		return Text.valueOf(_value.toString());
	}
	
	
	static final TextFormat<String> STRING_FORMAT = new TextFormat<String>() {
		@Override
		public Appendable format(final String value, final Appendable appendable) 
			throws IOException 
		{
			return appendable.append(value);
		}

		@Override
		public String parse(final CharSequence seq, final TextFormat.Cursor curs) {
			return seq.toString();
		}
		
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	static final XMLFormat<EnumGene> 
	XML = new XMLFormat<EnumGene>(EnumGene.class) 
	{
		private static final String TYPE = "type";
		
		@Override
		public EnumGene newInstance(
			final Class<EnumGene> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			try {
				final String typeName = xml.getAttribute(TYPE, "");
				final String value = xml.getText().toString();
				final Class<Enum> type = (Class<Enum>)Class.forName(typeName);
				
				return EnumGene.valueOf(Enum.valueOf(type, value));
			} catch (ClassNotFoundException e) {
				throw new XMLStreamException(e);
			}
		}
		@Override
		public void write(final EnumGene gene, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(TYPE, gene._value.getClass().getName());
			xml.addText(gene._value.name());
		}
		@Override
		public void read(final InputElement element, final EnumGene gene) {
		}
	};

}






