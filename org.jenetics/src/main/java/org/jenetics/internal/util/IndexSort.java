package org.jenetics.internal.util;

/**
 * @author Franz Wilhelmstötter <franz.wilhelmstoetter@emarsys.com>
 */
public abstract class IndexSort<A> {

    public abstract int[] sort(final A array);

    public static int[] newIndexes(final int length) {
        final int[] indexes = new int[length];
        for (int i = 0; i < indexes.length; ++i) {
            indexes[i] = i;
        }
        return indexes;
    }

    public static void swap(final int[] indexes, final int i, final int j) {
        final int temp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = temp;
    }

}
