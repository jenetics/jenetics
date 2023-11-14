package at.jku.dke.harmonic.optimizer.optimization.jenetics.jeneticsMO.jeneticsExtensions;

import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.Combinatorics;
import io.jenetics.util.MSeq;

import java.util.Random;

public class ArbitraryMutatorWithK<
        G extends Gene<?, G>,
        C extends Comparable<? super C>
        >
        extends Mutator<G, C>
{

    public ArbitraryMutatorWithK(final double probability) {
        super(probability);
    }

    public ArbitraryMutatorWithK() {
        this(DEFAULT_ALTER_PROBABILITY);
    }

    @Override
    protected MutatorResult<Chromosome<G>> mutate(
            final Chromosome<G> chromosome,
            final double p,
            final Random random
    ) {
        final MutatorResult<Chromosome<G>> result;
        if(chromosome.length() > 1) {
            double lengthDouble = getDistance(p, random);
            int lengthInt = (int) Math.round(chromosome.length()*lengthDouble);
            if(lengthInt == 0) {
                result = MutatorResult.of(chromosome);
            } else {
                int startingPoint;
                if (lengthInt >= chromosome.length()) {
                    startingPoint = 0;
                } else {
                    startingPoint = random.nextInt(chromosome.length() - lengthInt);
                }

                final MSeq<G> genes = MSeq.of(chromosome);
                genes.subSeq(startingPoint, startingPoint + lengthInt).shuffle();

                result = MutatorResult.of(
                        chromosome.newInstance(genes.toISeq()),
                        lengthInt - 1
                );
            }
        } else {
            result = MutatorResult.of(chromosome);
        }
        return result;
    }

    private double getDistance(double p, Random random) {
        double r = random.nextDouble();
        if(p == 0) {
            return 0;
        } else if(p < 0.292893) {
            double c = (2-Math.pow(2, 0.5))/p;
            double v = -Math.pow(c,2)/2;
            return (-c+Math.pow(Math.pow(c,2)+2*r*v,0.5))/v;
        } else if(p < 0.5){
            double c = (Math.pow(p,2)-0.5)/(Math.pow(p,2)-p);
            double v = 2-2*c;
            return (-c+Math.pow(Math.pow(c,2)+2*r*v,0.5))/v;
        } else if(p == 0.5) {
            return r;
        } else if(p < 0.707107) {
            double c = (Math.pow(1-p,2)-0.5)/(Math.pow(1-p,2)-(1-p));
            double v = 2*c-2;
            c -= v;
            return (-c+Math.pow(Math.pow(c,2)+2*v*(-r+c+0.5*v),0.5))/v;
        } else if(p < 1) {
            double c = (2-Math.pow(2, 0.5))/(1-p);
            double v = Math.pow(c,2)/2;
            c -= v;
            return (-c+Math.pow(Math.pow(c,2)+2*v*(-r+c+0.5*v),0.5))/v;
        } else {
            return 1;
        }
    }
}
