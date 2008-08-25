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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;

import org.jenetics.util.Validator;

import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: EnumGene.java,v 1.2 2008-08-25 19:35:25 fwilhelm Exp $
 */
public class EnumGene<E extends Enum<E>> 
	implements Gene<E>, Mean<EnumGene<E>>, Realtime
{
	private static final long serialVersionUID = 3892516872458977205L;

	private E _value;
	
	protected EnumGene() {
	}
	
	@Override
	public E getAllele() {
		return _value;
	}
	
	@Override
	public EnumGene<E> mean(EnumGene<E> that) {
		Validator.notNull(that, "Enum value");
		
		if (that._value == _value) {
			return that;
		} else {
			final Class<?> type = that.getClass();
			@SuppressWarnings("unchecked")
			final E[] values = (E[])type.getEnumConstants();
			final int ordinal = (that.getAllele().ordinal() + _value.ordinal()) / 2;
			return newInstance(values[ordinal]);
		}
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public EnumGene<E> copy() {
		return valueOf(_value);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		if (_value != null) {
			hash += 37*_value.ordinal() + 17;
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof EnumGene)) {
			return false;
		}
		
		EnumGene<?> gene = (EnumGene<?>)obj;
		if (gene._value != null) {
			return gene._value.equals(_value);
		} else if (_value != null) {
			return _value.equals(gene._value);
		} else {
			return false;
		}
	}
	

	@SuppressWarnings("unchecked")
	private static final ObjectFactory<EnumGene> FACTORY = new ObjectFactory<EnumGene>() {
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
		Validator.notNull(value, "Enum value");
		return newInstance(value);
	}

	public Text toText() {
		return Text.valueOf(_value.toString());
	}
	
//	static final XMLFormat<EnumGene> XML = new XMLFormat<EnumGene>(EnumGene.class) {
//		@Override
//		public EnumGene newInstance(final Class<EnumGene> cls, final InputElement xml) 
//			throws XMLStreamException 
//		{
//			return null;
//		}
//		@Override
//		public void write(final EnumGene gene, final OutputElement xml) 
//			throws XMLStreamException 
//		{
//			xml.setAttribute("value", gene._value);
//		}
//		@Override
//		public void read(final InputElement element, final EnumGene gene) {
//		}
//	};

}






