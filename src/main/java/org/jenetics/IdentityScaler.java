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

import java.io.Serializable;

import javolution.lang.Immutable;

import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class IdentityScaler<C extends Comparable<? super C>>
	implements
		Function<C, C>,
		Immutable,
		Serializable
{
	private static final long serialVersionUID = 1L;
	
	public IdentityScaler() {
	}

	/**
	 * Return the input {@code value}.
	 *
	 * @return the input {@code value}.
	 */
	@Override
	public C apply(final C value) {
		return value;
	}

	@Override
	public int hashCode() {
		return 37;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IdentityScaler<?>;
	}
	
	@Override
	public String toString() {
		return String.format("%s", getClass().getSimpleName());
	}
	
}
