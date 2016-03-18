package org.jenetix;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.stat.MinMax;

/**
 * @author Franz Wilhelmstötter <franz.wilhelmstoetter@emarsys.com>
 */
public class WeaselSelector<
    G extends Gene<?, G>,
    C extends Comparable<? super C>
>
    implements Selector<G, C>
{
    @Override
    public Population<G, C> select(
        final Population<G, C> population,
        final int count,
        final Optimize opt
    ) {
        requireNonNull(population, "Population");
        requireNonNull(opt, "Optimization");
        if (count < 0) {
            throw new IllegalArgumentException(format(
                "Selection count must be greater or equal then zero, but was %s",
                count
            ));
        }

        final MinMax<Phenotype<G, C>> minMax = MinMax.of(opt.ascending());
        population.forEach(minMax);

        final Population<G, C> selection = new Population<>(count);
        selection.fill(minMax::getMax, count);
        return selection;
    }
}
