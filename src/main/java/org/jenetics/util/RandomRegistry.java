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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.util;

import static org.jenetics.util.Validator.nonNull;

import java.util.Random;

import javolution.context.LocalContext;

/**
 * This class holds the {@link Random} engine used for the GA. The RandomRegistry
 * is thread safe. The default value for the random engine is an instance of
 * the Java {@link Random} engine with the {@link System#currentTimeMillis()} as
 * seed value.
 * <p/>
 * You can temporarily (and locally) change the implementation of the random engine 
 * by using the {@link LocalContext} from the 
 * <a href="http://javolution.org/">javolution</a> project.
 * 
 * [code]
 *     LocalContext.enter();
 *     try {
 *         RandomRegistry.setRandom(new MyRandom());
 *         ...
 *     } finally {
 *         LocalContext.exit(); // Restore the previous random engine.
 *     }
 * [/code]
 * 
 * @see LocalContext
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class RandomRegistry {
	private static final LocalContext.Reference<Random> RANDOM = 
		new LocalContext.Reference<Random>(new Random(System.currentTimeMillis()));
	
	private RandomRegistry() {
		throw new AssertionError("Don't create an 'RandomRegistry' instance.");
	}
	
	/**
	 * Return the global {@link Random} object.
	 * 
	 * @return the global {@link Random} object.
	 */
	public static Random getRandom() {
		return RANDOM.get();
	}
	
	/**
	 * Set the new global {@link Random} object for the GA.
	 * 
	 * @param random the new global {@link Random} object for the GA.
	 * @throws NullPointerException if the {@code random} object is {@code null}.
	 */
	public static void setRandom(final Random random) {
		RANDOM.set(nonNull(random, "Random object"));
	}

}
