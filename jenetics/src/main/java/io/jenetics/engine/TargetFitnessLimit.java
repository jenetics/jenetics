package io.jenetics.engine;

import java.util.function.Predicate;

/**
 * @author <a href="mailto:i.osipov.develop@gmail.com">Ivan Osipov</a>
 * @since 4.0
 * @version 4.0
 */
final class TargetFitnessLimit<N extends Number & Comparable<? super N>> implements Predicate<EvolutionResult<?, N>> {

    private N targetFitness;

    TargetFitnessLimit(N targetFitness) {
        if(targetFitness == null) {
            throw new NullPointerException("Target estimation is null");
        }
        this.targetFitness = targetFitness;
    }

    @Override
    public boolean test(EvolutionResult<?, N> result) {
        return result.getBestFitness() == null || targetFitness.compareTo(result.getBestFitness()) != 0;
    }

}
