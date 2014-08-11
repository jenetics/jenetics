package org.jenetics.internal.util;

/**
 * @author Franz Wilhelmstötter <franz.wilhelmstoetter@emarsys.com>
 */
public abstract class IndexSort<A> {

    public static final IndexSort<double[]> QuickSort = new QuickSort();
    public static final IndexSort<double[]> InsertionSort = new InsertionSort();

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

final class MergeSort extends IndexSort<double[]> {
    @Override
    public int[] sort(final double[] array) {
        final int[] indexes = newIndexes(array.length);

        return indexes;
    }
}

final class QuickSort extends IndexSort<double[]> {
    @Override
    public int[] sort(final double[] array) {
        final int[] indexes = newIndexes(array.length);
        quickSort(array, indexes, 0, array.length - 1);
        return indexes;
    }

    private void quickSort(
        final double[] array,
        final int[] indexes,
        final int left, final int right
    ) {
        if (right > left) {
            final int j = partition(array, indexes, left, right);
            quickSort(array, indexes, left, j - 1);
            quickSort(array, indexes, j + 1, right);
        }
    }

    private int partition(
        final double[] array, final int[] indexes,
        final int left, final int right
    ) {
        final double pivot = array[indexes[left]];
        int i = left;
        int j = right + 1;

        while (true) {
            do ++i; while (i < right && array[indexes[i]] < pivot);
            do --j; while (j > left && array[indexes[j]] > pivot);
            if (j <= i) break;
            swap(indexes, i, j);
        }
        swap(indexes, left, j);

        return j;
    }
}

final class InsertionSort extends IndexSort<double[]> {
    @Override
    public int[] sort(double[] array) {
        final int[] indexes = newIndexes(array.length);

        for (int sz = array.length, i = 1; i < sz; ++i) {
            int j = i;
            while (j > 0) {
                if (array[indexes[j - 1]] > array[indexes[j]]) {
                    swap(indexes, j - 1, j);
                } else {
                    break;
                }
                --j;
            }
        }

        return indexes;
    }
}
