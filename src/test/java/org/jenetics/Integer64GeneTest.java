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

import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Integer64GeneTest {
	
	@Test
	public void serialize() throws XMLStreamException {
		SerializeUtils.testSerialization(Integer64Gene.valueOf(5, 0, 10));
		SerializeUtils.testSerialization(Integer64Gene.valueOf(5, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void set() {
		Integer64Gene gene = Integer64Gene.valueOf(5, 0, 10);
		Assert.assertEquals(gene.getAllele(), Integer64.valueOf(5));
		Assert.assertEquals(gene.getMin(), Integer64.valueOf(0));
		Assert.assertEquals(gene.getMax(), Integer64.valueOf(10));
	}
	
}





