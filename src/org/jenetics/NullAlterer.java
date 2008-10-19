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

import org.jenetics.util.Probability;

/**
 * This alterer does nothing.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NullAlterer.java,v 1.5 2008-10-19 19:58:44 fwilhelm Exp $
 */
public class NullAlterer<G extends Gene<?>> extends Alterer<G> {
	private static final long serialVersionUID = 7892835302001002915L;

	public NullAlterer(final Alterer<G> component) {
		super(component);
	}

	public NullAlterer(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}

	public NullAlterer(final Probability probability) {
		super(probability);
	}

	@Override
	protected <C extends Comparable<C>> void change(
		final Population<G, C> population, final int generation
	) {
	}

}
