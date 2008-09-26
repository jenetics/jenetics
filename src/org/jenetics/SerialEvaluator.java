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

import java.util.List;

import org.jenetics.util.Validator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: SerialEvaluator.java,v 1.1 2008-09-26 21:36:33 fwilhelm Exp $
 */
public class SerialEvaluator implements FitnessEvaluator {

	@Override
	public <G extends Gene<?>, C extends Comparable<C>> 
	void evaluate(final List<Phenotype<G, C>> population)
	{
		Validator.notNull(population, "Population");
		
		for (int i = population.size(); --i >= 0;) {
			population.get(i).evaluate();
		}
	}
	
}
