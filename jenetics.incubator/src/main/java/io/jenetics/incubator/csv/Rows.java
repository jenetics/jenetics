package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Represents the result of the {@link RowReader#read(Readable)} method.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.1
 * @since 9.1
 */
public final class Rows implements Stream<Row> {
	private final Stream<Row> stream;

	Rows(Stream<Row> stream) {
		requireNonNull(stream);
		this.stream = stream;
	}

	/**
	 * Applies the row {@code parser} to {@code this} stream of {@link Row}
	 * objects.
	 *
	 * @param parser the rows result parser
	 * @return the parse result
	 * @param <T> the parse result type
	 */
	public <T> T as(final RowsParser<T> parser) {
		requireNonNull(parser);
		return parser.parse(this);
	}

	/* *************************************************************************
	 * Stream delegates.
	 * ************************************************************************/

	@Override
	public void close() {
		stream.close();
	}

	@Override
	public Stream<Row> filter(Predicate<? super Row> predicate) {
		return stream.filter(predicate);
	}

	@Override
	public <R> Stream<R> map(Function<? super Row, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Row> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super Row> mapper) {
		return stream.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super Row> mapper) {
		return stream.mapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Row, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(Function<? super Row, ? extends IntStream> mapper) {
		return stream.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super Row, ? extends LongStream> mapper) {
		return stream.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super Row, ? extends DoubleStream> mapper) {
		return stream.flatMapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> mapMulti(BiConsumer<? super Row, ? super Consumer<R>> mapper) {
		return stream.mapMulti(mapper);
	}

	@Override
	public IntStream mapMultiToInt(BiConsumer<? super Row, ? super IntConsumer> mapper) {
		return stream.mapMultiToInt(mapper);
	}

	@Override
	public LongStream mapMultiToLong(BiConsumer<? super Row, ? super LongConsumer> mapper) {
		return stream.mapMultiToLong(mapper);
	}

	@Override
	public DoubleStream mapMultiToDouble(BiConsumer<? super Row, ? super DoubleConsumer> mapper) {
		return stream.mapMultiToDouble(mapper);
	}

	@Override
	public Stream<Row> distinct() {
		return stream.distinct();
	}

	@Override
	public Stream<Row> sorted() {
		return stream.sorted();
	}

	@Override
	public Stream<Row> sorted(Comparator<? super Row> comparator) {
		return stream.sorted(comparator);
	}

	@Override
	public Stream<Row> peek(Consumer<? super Row> action) {
		return stream.peek(action);
	}

	@Override
	public Stream<Row> limit(long maxSize) {
		return stream.limit(maxSize);
	}

	@Override
	public Stream<Row> skip(long n) {
		return stream.skip(n);
	}

	@Override
	public Stream<Row> takeWhile(Predicate<? super Row> predicate) {
		return stream.takeWhile(predicate);
	}

	@Override
	public Stream<Row> dropWhile(Predicate<? super Row> predicate) {
		return stream.dropWhile(predicate);
	}

	@Override
	public void forEach(Consumer<? super Row> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super Row> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public Object[] toArray() {
		return stream.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	@Override
	public Row reduce(Row identity, BinaryOperator<Row> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<Row> reduce(BinaryOperator<Row> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Row, U> accumulator, BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	@Override
	public <R> Stream<R> gather(Gatherer<? super Row, ?, R> gatherer) {
		return stream.gather(gatherer);
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Row> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super Row, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public List<Row> toList() {
		return stream.toList();
	}

	@Override
	public Optional<Row> min(Comparator<? super Row> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<Row> max(Comparator<? super Row> comparator) {
		return stream.max(comparator);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public boolean anyMatch(Predicate<? super Row> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super Row> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super Row> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<Row> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<Row> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<Row> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<Row> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public Stream<Row> sequential() {
		return stream.sequential();
	}

	@Override
	public Stream<Row> parallel() {
		return stream.parallel();
	}

	@Override
	public Stream<Row> unordered() {
		return stream.unordered();
	}

	@Override
	public Stream<Row> onClose(Runnable closeHandler) {
		return stream.onClose(closeHandler);
	}

	public Stream<Row> stream() {
		return stream;
	}

	@Override
	public String toString() {
		return "Rows[" + "stream=" + stream + ']';
	}

}
