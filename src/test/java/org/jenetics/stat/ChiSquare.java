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
package org.jenetics.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class ChiSquare {
	private static final String CHI = "/org/jenetics/stat/chi.txt";
	
	private static final double[] PROPS = {
			0.9, 0.95, 0.975, 0.99, 0.995, 0.999, 0.9999
		};
	
	private static final double[][] TABLE = new double[100][PROPS.length];
	
	static {
		final InputStream in = ChiSquare.class.getResourceAsStream(CHI);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		int index = 0;
		String line = null;
		while ((line = readLine(reader)) != null) {
			final String[] parts = line.split("\\s");
			assert (parts.length == PROPS.length + 1);
			
			for (int i = 0; i < PROPS.length; ++i) {
				TABLE[index][i] = Double.parseDouble(parts[i + 1]);
			}
			
			++index;
		}
	}
	
	private ChiSquare() {
		throw new AssertionError();
	}
	
	private static String readLine(final BufferedReader reader) {
		try {
			String line = reader.readLine();
			while (line != null && line.startsWith("#")) {
				line = reader.readLine();
			}
			
			return line;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	public static double chi_9(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][0];
	}
	
	public static double chi_95(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][1];
	}
	
	public static double chi_975(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][2];
	}
	
	public static double chi_99(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][3];
	}
	
	public static double chi_995(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][4];
	}
	
	public static double chi_999(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][5];
	}
	
	public static double chi_9999(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][6];
	}
	
}
