package at.jku.dke.harmonic.optimizer.optimization.jenetics.jeneticsMO.jeneticsExtensions;

import io.jenetics.Crossover;
import io.jenetics.EnumGene;
import io.jenetics.util.MSeq;

import java.util.*;

public class UniformOderBasedCrossover<T, C extends Comparable<? super C>> extends Crossover<EnumGene<T>, C> {

    public UniformOderBasedCrossover(double probability) {
        super(probability);
    }

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
