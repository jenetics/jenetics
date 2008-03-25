/*
 * Java Genetic Algorithm Library (Jenetics-0.1.0.3).
 * Copyright (c) 2007 Franz Wilhelmstötter
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

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleChromosomeTest.java,v 1.1 2008-03-25 18:31:57 fwilhelm Exp $
 */
public class DoubleChromosomeTest {


    @Test
    public void testMutate() {
        DoubleChromosome c = DoubleChromosome.valueOf(0.0, 10.12, 20);
        NumberGene<Float64> g = c.getGene(12);
        assertTrue(g.equals(c.getGene(12)));
        c.mutate(12);
        assertEquals(g, c.getGene(12));
    }

    @Test
    public void testCreate() {
        DoubleChromosome c1 = DoubleChromosome.valueOf(-12.0, 230.123, 100);
        DoubleChromosome c2 = c1.newChromosome();
        
        for (NumberGene<Float64> g : c2) {
            assertEquals(-12.0, g.getMinValue().doubleValue());
            assertEquals(230.123, g.getMaxValue().doubleValue());
        }
    }

}
