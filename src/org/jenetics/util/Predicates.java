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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Predicates {

	private Predicates() {
	}
	
	public static class Nil implements Predicate<Object> {

		@Override
		public boolean evaluate(final Object object) {
			return object == null;
		}
		
	}
	
	public static class Not<T> implements Predicate<T> {
		private final Predicate<? super T> _predicate;
		
		public Not(final Predicate<? super T> predicate) {
			_predicate = Validator.nonNull(predicate);
		}
		
		@Override
		public boolean evaluate(final T object) {
			return !_predicate.equals(object);
		}
		
	}
	
	public static class And<T> implements Predicate<T> {
		private final Predicate<? super T> _a;
		private final Predicate<? super T> _b;
		
		public And(final Predicate<? super T> a, final Predicate<? super T> b) {
			_a = Validator.nonNull(a);
			_b = Validator.nonNull(b);
		}
		
		@Override
		public boolean evaluate(final T object) {
			return _a.equals(object) && _b.evaluate(object);
		}
	}
	
	public static class Or<T> implements Predicate<T> {
		private final Predicate<? super T> _a;
		private final Predicate<? super T> _b;
		
		public Or(final Predicate<? super T> a, final Predicate<? super T> b) {
			_a = Validator.nonNull(a);
			_b = Validator.nonNull(b);
		}
		
		@Override
		public boolean evaluate(final T object) {
			return _a.equals(object) || _b.evaluate(object);
		}
	}
	
	public static Predicate<Object> nil() {
		return new Nil();
	}
	
	public static <T> Predicate<T> not(final Predicate<? super T> predicate) {
		return new Not<T>(predicate);
	}
	
	public static <T> Predicate<T> and(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new And<T>(a, b);
	}
	
	public static <T> Predicate<T> or(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new Or<T>(a, b);
	}
	
}






