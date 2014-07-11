/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix.random;

import static org.jenetics.internal.util.Equality.eq;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random64;
import org.jenetics.util.math;


/**
 * <q align="justified" cite="http://www.nr.com/"><em>
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>64</sup> - 1 on 64 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * </em></q>
 *
 * <p align="left">
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br/>
 * <em>Chapter 7. Random Numbers, Section 7.1.2, Page 345</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 *
 * <p><b>
 * The <i>main</i> class of this PRNG is not thread safe. To create an thread
 * safe instances of this PRNG, use the {@link XOR64ShiftRandom.ThreadSafe} class.
 * </b></p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-11 $</em>
 */
public class XOR64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;


	/**
	 * This field can be used to initial the {@link org.jenetics.util.RandomRegistry}
	 * with a fast and thread safe random engine of this type; each thread gets
	 * a <i>local</i> copy of the {@code XORShiftRandom} engine.
	 *
	 * [code]
	 * RandomRegistry.setRandom(new XORShiftRandom.ThreadLocal());
	 * [/code]
	 *
	 * Calling the {@link XOR64ShiftRandom#setSeed(long)} method on the returned
	 * instance will throw an {@link UnsupportedOperationException}.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2014-07-11 $</em>
	 */
	public static final class ThreadLocal extends java.lang.ThreadLocal<XOR64ShiftRandom> {

		private final long _seed = math.random.seed();

		@Override
		protected XOR64ShiftRandom initialValue() {
			return new TLXOR64ShiftRandom(math.random.seed(_seed));
		}
	}

	private static final class TLXOR64ShiftRandom extends XOR64ShiftRandom {

		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLXOR64ShiftRandom(final long seed) {
			super(seed);
		}

		@Override
		public void setSeed(final long seed) {
			if (_sentry != null) {
				throw new UnsupportedOperationException(
					"The 'setSeed(long)' method is not supported " +
					"for thread local instances."
				);
			}
		}

	}

	/**
	 * This class is a <i>thread safe</i> version of the {@code XORShiftRandom}
	 * engine. Instances of <i>this</i> class and instances of the non-thread
	 * safe variants, with the same seed, will generate the same sequence of
	 * random numbers.
	 * [code]
	 * final XORShiftRandom a = new XORShiftRandom(123);
	 * final XORShiftRandom b = XORShiftRandom.ThreadSafe(123);
	 * for (int i = 0; i < 1000;  ++i) {
	 *     assert (a.nextLong() == b.nextLong());
	 *     assert (a.nextDouble() == b.nextDouble());
	 * }
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2014-07-11 $</em>
	 */
	public static final class ThreadSafe extends XOR64ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG, with
		 * an seed of {@link System#nanoTime()}.
		 */
		public ThreadSafe() {
		}

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG.
		 *
		 * @param seed the seed of the PRNG.
		 */
		public ThreadSafe(final long seed) {
			super(seed);
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized long nextLong() {
			return super.nextLong();
		}

	}

	private long _x;

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG, with
	 * an seed of {@link System#nanoTime()}.
	 */
	public XOR64ShiftRandom() {
		this(math.random.seed());
	}

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG.
	 *
	 * @param seed the seed of the PRNG.
	 */
	public XOR64ShiftRandom(final long seed) {
		_x = init(seed);
	}

	private static long init(final long seed) {
		return seed == 0 ? 0xdeadbeef : seed;
	}

	@Override
	public long nextLong() {
//		The list of the suggested shift values:
//		21, 35, 4
//		20, 41, 5
//		17, 31, 8
//		11, 29, 14
//		14, 29, 11
//		30, 35, 13
//		21, 37, 4
//		21, 43, 4
//		23, 41, 18

		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
	}

	@Override
	public void setSeed(final long seed) {
		_x = init(seed);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_x).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_x, random._x)
		);
	}

	@Override
	public String toString() {
		return String.format("%s[%d]", getClass().getName(), _x);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.XOR64ShiftRandom (2014-07-11 17:08)             #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-30-generic (amd64)                                             #
# java version "1.8.0_05"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_05-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02)                          #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.15e+07  | 899887096|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.72532674|  PASSED
      diehard_operm5|   0|   1000000|     100|0.86530788|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.74197779|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.86467672|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.57644705|  PASSED
        diehard_opso|   0|   2097152|     100|0.44105427|  PASSED
        diehard_oqso|   0|   2097152|     100|0.93970509|  PASSED
         diehard_dna|   0|   2097152|     100|0.74053280|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.99988615|   WEAK
diehard_count_1s_byt|   0|    256000|     100|0.01732362|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.10060329|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.41511826|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.57108638|  PASSED
     diehard_squeeze|   0|    100000|     100|0.08895979|  PASSED
        diehard_sums|   0|       100|     100|0.48536194|  PASSED
        diehard_runs|   0|    100000|     100|0.84993201|  PASSED
        diehard_runs|   0|    100000|     100|0.05232516|  PASSED
       diehard_craps|   0|    200000|     100|0.56497870|  PASSED
       diehard_craps|   0|    200000|     100|0.76532291|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.43111443|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.50072273|  PASSED
         sts_monobit|   1|    100000|     100|0.38244154|  PASSED
            sts_runs|   2|    100000|     100|0.39862563|  PASSED
          sts_serial|   1|    100000|     100|0.98286058|  PASSED
          sts_serial|   2|    100000|     100|0.58595101|  PASSED
          sts_serial|   3|    100000|     100|0.65507890|  PASSED
          sts_serial|   3|    100000|     100|0.91106581|  PASSED
          sts_serial|   4|    100000|     100|0.31834703|  PASSED
          sts_serial|   4|    100000|     100|0.28630775|  PASSED
          sts_serial|   5|    100000|     100|0.93164636|  PASSED
          sts_serial|   5|    100000|     100|0.25276569|  PASSED
          sts_serial|   6|    100000|     100|0.53531973|  PASSED
          sts_serial|   6|    100000|     100|0.91382694|  PASSED
          sts_serial|   7|    100000|     100|0.58489339|  PASSED
          sts_serial|   7|    100000|     100|0.11330915|  PASSED
          sts_serial|   8|    100000|     100|0.10842716|  PASSED
          sts_serial|   8|    100000|     100|0.84226268|  PASSED
          sts_serial|   9|    100000|     100|0.03715209|  PASSED
          sts_serial|   9|    100000|     100|0.32341102|  PASSED
          sts_serial|  10|    100000|     100|0.47102355|  PASSED
          sts_serial|  10|    100000|     100|0.99662665|   WEAK
          sts_serial|  11|    100000|     100|0.48935718|  PASSED
          sts_serial|  11|    100000|     100|0.96010949|  PASSED
          sts_serial|  12|    100000|     100|0.09222138|  PASSED
          sts_serial|  12|    100000|     100|0.55380464|  PASSED
          sts_serial|  13|    100000|     100|0.29230001|  PASSED
          sts_serial|  13|    100000|     100|0.40548455|  PASSED
          sts_serial|  14|    100000|     100|0.52906123|  PASSED
          sts_serial|  14|    100000|     100|0.34442793|  PASSED
          sts_serial|  15|    100000|     100|0.54648985|  PASSED
          sts_serial|  15|    100000|     100|0.73355434|  PASSED
          sts_serial|  16|    100000|     100|0.79938493|  PASSED
          sts_serial|  16|    100000|     100|0.70095610|  PASSED
         rgb_bitdist|   1|    100000|     100|0.91282660|  PASSED
         rgb_bitdist|   2|    100000|     100|0.40456888|  PASSED
         rgb_bitdist|   3|    100000|     100|0.04396109|  PASSED
         rgb_bitdist|   4|    100000|     100|0.76475730|  PASSED
         rgb_bitdist|   5|    100000|     100|0.69769657|  PASSED
         rgb_bitdist|   6|    100000|     100|0.92844358|  PASSED
         rgb_bitdist|   7|    100000|     100|0.75304526|  PASSED
         rgb_bitdist|   8|    100000|     100|0.06222910|  PASSED
         rgb_bitdist|   9|    100000|     100|0.99622040|   WEAK
         rgb_bitdist|  10|    100000|     100|0.77068695|  PASSED
         rgb_bitdist|  11|    100000|     100|0.04142726|  PASSED
         rgb_bitdist|  12|    100000|     100|0.09298225|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.11022520|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.82910788|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.90488851|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.01247970|  PASSED
    rgb_permutations|   2|    100000|     100|0.67212346|  PASSED
    rgb_permutations|   3|    100000|     100|0.99917694|   WEAK
    rgb_permutations|   4|    100000|     100|0.91137718|  PASSED
    rgb_permutations|   5|    100000|     100|0.78369700|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.86343021|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.66277599|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.07730052|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.23129263|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.01809188|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.62757229|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.63911069|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.28436913|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.17064248|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.30736283|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.37476453|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.28314236|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.91355149|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.93015831|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.68270183|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.49140323|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.96127264|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.69889486|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.84445150|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.19023984|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.98243204|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.53131960|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.91859883|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.58944690|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.25063680|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.98413704|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.64444623|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.09897186|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.02000163|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.32180198|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.94896985|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.08432418|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.85780396|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.09226182|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.98786740|  PASSED
             dab_dct| 256|     50000|       1|0.61420594|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.98034792|  PASSED
        dab_filltree|  32|  15000000|       1|0.99331613|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.67182246|  PASSED
       dab_filltree2|   1|   5000000|       1|0.89259864|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Runtime: 0:38:15                                                            #
#=============================================================================#
*/
