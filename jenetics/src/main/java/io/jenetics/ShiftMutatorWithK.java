package at.jku.dke.harmonic.optimizer.optimization.jenetics.jeneticsMO.jeneticsExtensions;

import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.util.MSeq;

import java.util.Random;

public class ShiftMutatorWithK<
        G extends Gene<?, G>,
        C extends Comparable<? super C>
        >
        extends Mutator<G, C> {

    public ShiftMutatorWithK(final double probability) {
        super(probability);
    }

    public ShiftMutatorWithK() {
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
            try {
                double lengthDouble = getDistance(p, random);
                int lengthInt = (int) Math.round(chromosome.length() * lengthDouble);
                if(lengthInt == 0) {
                    result = MutatorResult.of(chromosome);
                } else {
                    int startingPoint;
                    if (lengthInt >= chromosome.length()) {
                        startingPoint = 0;
                    } else {
                        startingPoint = random.nextInt(chromosome.length() - lengthInt);
                    }
                    int endPoint = startingPoint + lengthInt;
                    int middlePoint = startingPoint + random.nextInt(lengthInt);
                    final MSeq<G> genes = MSeq.of(chromosome);
                    MSeq<G> firstSeq = genes.subSeq(startingPoint, middlePoint).copy();
                    int difOne = endPoint - middlePoint;
                    MSeq<G> secondSeq = genes.subSeq(middlePoint, endPoint).copy();
                    int difTwo = middlePoint - startingPoint;
                    int i = 0;
                    for (G g : firstSeq) {
                        genes.set(startingPoint + i + difOne, g);
                        i++;
                    }
                    i = 0;
                    for (G g : secondSeq) {
                        genes.set(middlePoint + i - difTwo, g);
                        i++;
                    }

                    result = MutatorResult.of(
                            chromosome.newInstance(genes.toISeq()),
                            endPoint - startingPoint - 1
                    );
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                return null;
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
