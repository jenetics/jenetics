package org.jenetics.util;

public class ForeachTest {

	public interface P1<T> {
		public boolean apply(final T value);
	}
	
	public interface P2<T> {
		public Boolean apply(final T value);
	}
	
	public static void main(final String[] args) throws Exception {
		Thread.sleep(100);
		
		final int iterations = 2000000000;
		
		long begin = System.currentTimeMillis();
		iterate1(iterations, new P1<Long>() {
			@Override public boolean apply(Long value) {
				return value.intValue() < iterations;
			}
		});
		long end = System.currentTimeMillis();
		System.out.println("P1: " + (end - begin));
		
		
		begin = System.currentTimeMillis();
		iterate2(iterations, new P2<Long>() {
			@Override public Boolean apply(Long value) {
				return value.intValue() < iterations ? Boolean.TRUE : Boolean.FALSE;
			}
		});
		end = System.currentTimeMillis();
		System.out.println("P2: " + (end - begin));
		
	}
	
	
	static long iterate1(final long iterations, final P1<Long> predicate) {
		long i = 0;
		while (i < iterations && predicate.apply(i)) {
			i += 1;
			
		}
		
		return i;
	}
	
	static long iterate2(final long iterations, final P2<Long> predicate) {
		long i = 0;
		while (i < iterations && predicate.apply(i)) {
			i += 1;
		}
		
		return i;
	}
	
}
