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
package org.jenetics.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Random;

import javolution.context.LocalContext;
import javolution.lang.Immutable;
import javolution.lang.Reflection;
import javolution.lang.Reflection.Method;
import javolution.xml.XMLSerializable;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class ObjectTester<T> {
	
	protected abstract Factory<T> getFactory();
	
	protected Array<T> newSameObjects(final int nobjects) {
		final Array<T> objects = new Array<>(nobjects);
			
		for (int i = 0; i < nobjects; ++i) {
			LocalContext.enter();
			try {
				RandomRegistry.setRandom(new Random(23487589));
				objects.set(i, getFactory().newInstance());
			} finally {
				LocalContext.exit();
			}
		}
		
		return objects;
	}
	
	@Test
	public void equals() {
		final Array<T> same = newSameObjects(5);
		
		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);
			
			Assert.assertEquals(other, other);
			Assert.assertEquals(other, that);
			Assert.assertEquals(that, other);
			Assert.assertFalse(other.equals(null));
		}
	}
	
	@Test
	public void notEquals() {
		for (int i = 0; i < 10; ++i) {
			final Object that = getFactory().newInstance();
			final Object other = getFactory().newInstance();
			
			if (that.equals(other)) {
				Assert.assertTrue(other.equals(that));
				Assert.assertEquals(that.hashCode(), other.hashCode());
			} else {
				Assert.assertFalse(other.equals(that));
			}
		}
	}
	
	@Test
	public void notEqualsDifferentType() {
		final Object that = getFactory().newInstance();
		Assert.assertFalse(that.equals(null));
		Assert.assertFalse(that.equals(""));
		Assert.assertFalse(that.equals(23));
	}

	@Test
	public void hashcode() {
		final Array<T> same = newSameObjects(5);
		
		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);
			
			Assert.assertEquals(that.hashCode(), other.hashCode());
		}
	}
	
	@Test
	public void cloning() {
		final Object that = getFactory().newInstance();
		if (that instanceof Cloneable) {
			final Method clone = Reflection.getMethod(String.format(
				"%s.clone()", that.getClass().getName()
			));
			final Object other = clone.invoke(that);
			
			Assert.assertEquals(other, that);
			Assert.assertNotSame(other, that);
		}
	}
	
	@Test
	public void copying() {
		final Object that = getFactory().newInstance();
		if (that instanceof Copyable<?>) {
			final Object other = ((Copyable<?>)that).copy();
			if (other.getClass() == that.getClass()) {
				Assert.assertEquals(other, that);
				Assert.assertNotSame(other, that);
			}
		}
	}
	
	@Test
	public void tostring() {
		final Array<T> same = newSameObjects(5);
		
		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);
			
			Assert.assertEquals(that.toString(), other.toString());
			Assert.assertNotNull(other.toString());
		}
	}
	
	@Test
	public void isValid() {
		final T a = getFactory().newInstance();
		if (a instanceof Verifiable) {
			Assert.assertTrue(((Verifiable)a).isValid());
		}
	}
	
	@Test
	public void typeConsistency() throws Exception {
		final T a = getFactory().newInstance();
		
		Assert.assertFalse(a instanceof Cloneable && a instanceof Immutable);
		if (a instanceof Copyable<?>) {
			final Object b = ((Copyable<?>)a).copy();
			if (a.getClass() == b.getClass()) {
				Assert.assertFalse(a instanceof Copyable<?> && a instanceof Immutable);
			}
		}
		
		
		if (a instanceof Immutable) {
			final BeanInfo info = Introspector.getBeanInfo(a.getClass());
			for (PropertyDescriptor prop : info.getPropertyDescriptors()) {
				Assert.assertNull(prop.getWriteMethod());
			}
		}
	}

	
	@Test
	public void xmlSerialize() throws Exception {
		final Object object = getFactory().newInstance();
		
		if (object instanceof XMLSerializable) {
			for (int i = 0; i < 10; ++i) {
				final XMLSerializable serializable =
					(XMLSerializable)getFactory().newInstance();
				
				Serialize.xml.test(serializable);
			}
		}
	}
	
	@Test
	public void objectSerialize() throws Exception {
		final Object object = getFactory().newInstance();
		
		if (object instanceof Serializable) {
			for (int i = 0; i < 10; ++i) {
				final Serializable serializable =
					(Serializable)getFactory().newInstance();
				
				Serialize.object.test(serializable);
			}
		}
	}
	
}







