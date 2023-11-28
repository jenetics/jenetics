package io.jenetics;

import io.jenetics.util.MSeq;

import java.util.*;

/**
 * The {@code UniformOderBasedCrossover} guarantees that all {@link Gene}s
 * are found exactly once in each chromosome. No gene is duplicated by this
 * crossover. The PMX can be applied usefully in the TSP or other permutation
 * problem encodings. Permutation encoding is useful for all problems where the
 * fitness only depends on the ordering of the genes within the chromosome. This
 * is the case in many combinatorial optimization problems. Other crossover
 * operators for combinatorial optimization are:
 * <ul>
 *     <li>order crossover</li>
 *     <li>cycle crossover</li>
 *     <li>edge recombination crossover</li>
 *     <li>edge assembly crossover</li>
 * 	   <li>partially matched crossover</li>
 * </ul>
 * <p>
 * Within the uniform oder based crossover, a set of positions is chosen randomly.
 * The genes at the positions are reordered in the order they occur in the other
 * parent.
 * <pre>
 *    C1 = 0123456789
 *    C2 = 9876543210
 *    Positions = 2, 4, 5, 7, 8
 * </pre>
 *  The values at the positions are removed
 * <pre>
 *    C1 = 01_3__6__9
 *    C2 = 9__6__3_10
 *    Order of removed values in C1 = 2, 4, 5, 7, 8
 *    Order of removed values in C2 = 8, 7, 5, 4, 2
 * </pre>
 * The removed values are added in the order they occur in the other chromosome
 * <pre>
 *    C1 = 0183756429
 *    C2 = 9246573810
 * </pre>
 * For more details see: Kruse, R., Mostaghim, S., Borgelt, C., Braune, C., &
 * Steinbrecher, M. (2022a). Elements of evolutionary algorithms. In R. Kruse,
 * S. Mostaghim, C. Borgelt, C. Braune, & M. Steinbrecher (Eds.), Computational
 * intelligence: A methodological introduction (pp. 255–285). Springer International Publishing.
 * <a href="https://doi.org/10.1007/978-3-030-42227-1_12">https://doi.org/10.1007/978-3-030-42227-1_12</a>
 *
 * @see PermutationChromosome
 *
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 * @version 7.2
 * @since 7.2
 */

public class UniformOderBasedCrossover<T, C extends Comparable<? super C>> extends Crossover<EnumGene<T>, C> {

	/**
	 * Create a new UniformOrderBasedCrossover instance
	 *
	 * @param probability the recombination probability as defined in
	 *        {@link Crossover#Crossover(double)}. This is the probability that
	 *        a given individual is selected for crossover.
	 * @throws IllegalArgumentException if the probability is not in the
	 *         valid range of {@code [0, 1]}
	 */
    public UniformOderBasedCrossover(double probability) {
        super(probability);
    }

	/**
	 * Applies uniform order based crossover to two sequences. A set of positions is chosen,
	 * the genes at those positions are reordered as they occur in the other sequence.
	 *
	 * @param that first sequence
	 * @param other second sequence
	 *
	 * @throws IllegalArgumentException if the two input sequences have a different length}
	 */
    @Override
    protected int crossover(MSeq<EnumGene<T>> that, MSeq<EnumGene<T>> other) {
        if (that.length() != other.length()) {
            throw new IllegalArgumentException(String.format("Required chromosomes with same length: %s != %s", that.length(), other.length()));
        } else {
            List<Integer> changePositions = getPositions(that.length());
            List<EnumGene<T>> orderValuesRemovedOne = getRemovedValues(changePositions, that);
            List<EnumGene<T>> orderValuesRemovedTwo = getRemovedValues(changePositions, other);
            List<EnumGene<T>> reorderedOne = reorderRemovedValues(orderValuesRemovedOne, other);
            List<EnumGene<T>> reorderedTwo = reorderRemovedValues(orderValuesRemovedTwo, that);
            changePositionsOccurrences(that, reorderedOne, changePositions);
            changePositionsOccurrences(other, reorderedTwo, changePositions);
        }
        return 0;
    }

    private void changePositionsOccurrences(MSeq<EnumGene<T>> sequence, List<EnumGene<T>> orderedValues, List<Integer> changePositions) {
        int i = 0;
        for(Integer pos : changePositions) {
            sequence.set(pos, orderedValues.get(i));
            i++;
        }
    }

    private List<EnumGene<T>> reorderRemovedValues(List<EnumGene<T>> orderAtPositionOne, MSeq<EnumGene<T>> other) {
        return orderAtPositionOne.stream()
                .map(other::lastIndexOf)
                .sorted(Integer::compareTo)
                .map(other::get)
                .toList();
    }

    private List<EnumGene<T>> getRemovedValues(List<Integer> changePositions, MSeq<EnumGene<T>> that) {
        List<EnumGene<T>> orderOfValues = new LinkedList<>();
        for(Integer pos : changePositions) {
            orderOfValues.add(that.get(pos));
        }
        return orderOfValues;
    }

    private List<Integer> getPositions(int length) {
        Random rand = new Random();
        Set<Integer> sortedSet = new TreeSet<>();
        while(sortedSet.size() < length/2) {
            Integer newPosition = rand.nextInt(length);
            sortedSet.add(newPosition);
        }
        return sortedSet.stream().toList();
    }
}
