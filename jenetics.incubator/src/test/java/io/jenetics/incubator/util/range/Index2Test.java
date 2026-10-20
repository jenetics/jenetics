package io.jenetics.incubator.util.range;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Index2Test {

	@Test(dataProvider = "shapes")
	public void rankAndNavigation(final int[] sizes) {
		final var coordinates = new ArrayList<int[]>();
		for (int x = 0; x < sizes.length; ++x) {
			for (int y = 0; y < sizes[x]; ++y) {
				coordinates.add(new int[] {x, y});
			}
		}

		final var first = new Index2(sizes);
		assertThat(first.size()).isEqualTo(coordinates.size());
		var current = first;
		for (int rank = 0; rank < coordinates.size(); ++rank) {
			final int[] expected = coordinates.get(rank);
			assertPosition(current, expected[0], expected[1], rank);
			assertPosition(current.rank(rank), expected[0], expected[1], rank);
			assertPosition(
				new Index2(expected[0], expected[1], sizes),
				expected[0], expected[1], rank
			);
			if (rank + 1 < coordinates.size()) {
				current = current.next();
			}
		}
		assertThatThrownBy(current::next).isInstanceOf(NoSuchElementException.class);

		for (int rank = coordinates.size() - 1; rank >= 0; --rank) {
			final int[] expected = coordinates.get(rank);
			assertPosition(current, expected[0], expected[1], rank);
			if (rank > 0) {
				current = current.prev();
			}
		}
		assertThatThrownBy(first::prev).isInstanceOf(NoSuchElementException.class);
		assertPosition(first, coordinates.getFirst()[0], 0, 0);
		assertThatThrownBy(() -> first.rank(-1))
			.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> first.rank(first.size()))
			.isInstanceOf(IndexOutOfBoundsException.class);
	}

	@DataProvider
	public Object[][] shapes() {
		return new Object[][] {
			{new int[] {1}},
			{new int[] {5}},
			{new int[] {1, 1, 1, 1}},
			{new int[] {2, 3, 1}},
			{new int[] {0, 0, 2, 0, 0, 3, 0, 1, 0, 0}},
			{new int[] {0, 1, 0}},
			{new int[] {23, 0, 17, 234, 0, 3, 1, 4, 6}}
		};
	}

	@Test
	public void ownsRowSizes() {
		final int[] sizes = {2, 0, 3};
		final var index = new Index2(2, 1, sizes);
		final var previous = index.prev();
		sizes[0] = 100;
		sizes[2] = 0;

		assertThat(index.size()).isEqualTo(5);
		assertPosition(index, 2, 1, 3);
		assertPosition(previous, 2, 0, 2);
		assertPosition(index.rank(4), 2, 2, 4);
		assertPosition(previous.prev(), 0, 1, 1);
	}

	@Test
	public void rejectsInvalidShapes() {
		for (int[] sizes : new int[][] {
			{}, {0}, {0, 0}, {1, -1}, {Integer.MAX_VALUE, 1}
		}) {
			assertThatThrownBy(() -> new Index2(sizes))
				.isInstanceOf(IllegalArgumentException.class);
		}
		assertThatThrownBy(() -> new Index2(null))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void rejectsInvalidCoordinates() {
		final int[] sizes = {2, 0, 3};
		for (int[] coordinate : new int[][] {
			{-1, 0}, {3, 0}, {0, -1}, {0, 2}, {1, 0}, {2, 3}
		}) {
			assertThatThrownBy(() -> new Index2(coordinate[0], coordinate[1], sizes))
				.isInstanceOf(IndexOutOfBoundsException.class);
		}
	}

	@Test
	public void supportsMaximumSize() {
		final var first = new Index2(new int[] {0, Integer.MAX_VALUE, 0});
		final var last = first.rank(Integer.MAX_VALUE - 1);
		assertPosition(last, 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 1);
		assertPosition(last.prev().next(), 1, last.y(), last.rank());
		assertThatThrownBy(last::next).isInstanceOf(NoSuchElementException.class);
	}

	private static void assertPosition(
		final Index2 index,
		final int x,
		final int y,
		final int rank
	) {
		assertThat(index.x()).isEqualTo(x);
		assertThat(index.y()).isEqualTo(y);
		assertThat(index.rank()).isEqualTo(rank);
	}
}
