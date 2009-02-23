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
package org.jenetics.examples;

import java.io.Serializable;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Chromosome;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutation;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Factory;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Knapsack.java,v 1.7 2009-02-23 20:58:08 fwilhelm Exp $
 */
class Item implements Serializable {
	private static final long serialVersionUID = -2129262369196749572L;
	public double size;
    public double value;
}

class KnappsackFunction implements FitnessFunction<BitGene, Float64> {
	private static final long serialVersionUID = -924756568100918419L;
	
	private final Item[] _items;
    private final double _knapsackSize;
    
    public KnappsackFunction(final Item[] items, double knapsackSize) {
        this._items = items;
        this._knapsackSize = knapsackSize;
    }
    
    public Item[] getItems() {
    	return _items;
    }
    
    public Float64 evaluate(final Genotype<BitGene> genotype) {
        final Chromosome<BitGene> ch = genotype.getChromosome();
        
        double size = 0;
        double value = 0;
        for (int i = 0, n = ch.length(); i < n; ++i) {
            if (ch.getGene(i).getBit()) {
                size += _items[i].size;
                value += _items[i].value;
            }
        }
        
        if (size > _knapsackSize) {
            return Float64.ZERO;
        } else {
            return Float64.valueOf(value);
        }
    }
}

public class Knapsack {
	
	private static KnappsackFunction newFitnessFuntion(int n, double knapsackSize) {
		Item[] items = new Item[n];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new Item();
            items[i].size = (Math.random() + 1)*10;
            items[i].value = (Math.random() + 1)*15;
        }
        
        return new KnappsackFunction(items, knapsackSize);
	}
	
    public static void main(String[] argv) throws Exception {
    	//Defining the fitness function and the genotype.
        final KnappsackFunction ff = newFitnessFuntion(15, 100);
        final Factory<Genotype<BitGene>> genotype = Genotype.valueOf(
            BitChromosome.valueOf(15, Probability.valueOf(0.5))
        );
        
        final GeneticAlgorithm<BitGene, Float64> ga = GeneticAlgorithm.valueOf(genotype, ff);
        ga.setMaximalPhenotypeAge(10);
        ga.setPopulationSize(1000);
        ga.setSelectors(new RouletteWheelSelector<BitGene, Float64>());
        ga.setAlterer(
            new Mutation<BitGene>(Probability.valueOf(0.115), 
            new SinglePointCrossover<BitGene>(Probability.valueOf(0.06)))
        );
        
        GAUtils.execute(ga, 100);
    }
}











