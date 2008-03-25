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
 * @version $Id: IdentityScaler.java,v 1.1 2008-03-25 18:31:56 fwilhelm Exp $
 */
public class IdentityScaler implements FitnessScaler {
	private static final long serialVersionUID = -8781746035187241816L;
	
	public static final IdentityScaler INSTANCE = new IdentityScaler();

	public IdentityScaler() {
		super();
	}

	/**
	 *  Return the input {@code value}.
	 *  
	 *  @return the input {@code value}.
	 */
	@Override
	public double scale(double value) {
		return value;
	}

}
