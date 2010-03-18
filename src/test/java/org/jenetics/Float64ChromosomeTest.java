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

import static org.testng.Assert.assertEquals;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Float64ChromosomeTest.java 331 2010-02-16 12:53:16Z fwilhelm $
 */
public class Float64ChromosomeTest {


    @Test
    public void testCreate() {
        Float64Chromosome c1 = new Float64Chromosome(-12.0, 230.123, 100);
        Float64Chromosome c2 = c1.newInstance();
        
        for (NumberGene<Float64, ?> g : c2) {
            assertEquals(-12.0, g.getMin().doubleValue());
            assertEquals(230.123, g.getMax().doubleValue());
        }
    }
    
    @Test
    public void serialize() throws XMLStreamException {
    	SerializeUtils.testSerialization(new Float64Chromosome(-12.0, 230.123, 1));
    }

}
