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
import static org.testng.Assert.assertTrue;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleChromosomeTest.java,v 1.5 2008-09-23 19:18:27 fwilhelm Exp $
 */
public class DoubleChromosomeTest {


    @Test
    public void testMutate() {
        DoubleChromosome c = new DoubleChromosome(0.0, 10.12, 20);
        NumberGene<Float64> g = c.getGene(12);
        assertTrue(g.equals(c.getGene(12)));
        c.mutate(12);
        assertEquals(g, c.getGene(12));
    }

    @Test
    public void testCreate() {
        DoubleChromosome c1 = new DoubleChromosome(-12.0, 230.123, 100);
        DoubleChromosome c2 = c1.newChromosome();
        
        for (NumberGene<Float64> g : c2) {
            assertEquals(-12.0, g.getMinValue().doubleValue());
            assertEquals(230.123, g.getMaxValue().doubleValue());
        }
    }
    
    @Test
    public void serialize() throws XMLStreamException {
    	SerializeUtils.testSerialization(new DoubleChromosome(-12.0, 230.123, 1));
    }

}
