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
 * Interface which delivers a stream of (positive) indexes ({@code int}s)s. The
 * stream ends if {@link #next()} returns {@code -1}. Here some usage examples:
 * 
 * [code]
 * final IndexStream stream = ...;
 * for (int i = stream.next(); i != -1; i = stream.next()) {
 *     System.out.println(i);
 * }
 * [/code]
 * [code]
 * final IndexStream stream = ...;
 * int index = 0;
 * while ((index = stream.next()) != -1) {
 *     System.out.println(i);
 * }
 * [/code]
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface IndexStream {

	/**
	 * Return the next (positive inclusive zero) index, or -1 if the stream has 
	 * reached its end.
	 * 
	 * @return the next index, or -1 if the stream has reached its end.
	 */
	public int next();
	
}
