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
 * @version $Id: NullAlterer.java,v 1.7 2010-01-14 14:03:28 fwilhelm Exp $
 */
public class NullAlterer<G extends Gene<?, G>> extends Alterer<G> {
	private static final long serialVersionUID = 7892835302001002915L;

	public NullAlterer() {
		this((Alterer<G>)null);
	}
	
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
