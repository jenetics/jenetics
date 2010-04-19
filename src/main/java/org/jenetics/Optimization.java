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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public enum Optimization {
	MINIMIZE(new Min()),
	MAXIMIZE(new Max());
	
	private final Comp _comparator;
	
	private Optimization(final Comp comparator) {
		_comparator = comparator;
	}
	
	public <T extends Comparable<T>> int compare(final T o1, final T o2) {
		return _comparator.compare(o1, o2);
	}
	
	
	private static interface Comp {
		public <T extends Comparable<T>> int compare(final T o1, final T o2);
	}
	
	private static final class Min implements Comp {
		@Override
		public <T extends Comparable<T>> int compare(final T o1, final T o2) {
			return -o1.compareTo(o2);
		}
	}
	
	private static final class Max implements Comp {
		@Override
		public <T extends Comparable<T>> int compare(final T o1, final T o2) {
			return o1.compareTo(o2);
		}
	}
	
}
