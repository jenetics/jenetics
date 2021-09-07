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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SerialIOTest {

	@Test(dataProvider = "intValues")
	public void writeInt(final int v, final byte[] bytes) throws IOException {
		final var zigzag = intToBytes(v);
		assertThat(zigzag).isEqualTo(bytes);
	}

	@Test(dataProvider = "intValues")
	public void readInt(final int v, final byte[] bytes) throws IOException {
		final var zigzag = bytesToInt(bytes);
		assertThat(zigzag).isEqualTo(v);
	}

	@Test(dataProvider = "longValues")
	public void writeLong(final long v, final byte[] bytes) throws IOException {
		final var zigzag = longToBytes(v);
		assertThat(zigzag).isEqualTo(bytes);
	}

	@Test(dataProvider = "longValues")
	public void readLong(final long v, final byte[] bytes) throws IOException {
		final var zigzag = bytesToLong(bytes);
		assertThat(zigzag).isEqualTo(v);
	}


	private static byte[] intToBytes(final int v) throws IOException {
		try (var bout = new ByteArrayOutputStream();
			var dout = new DataOutputStream(bout))
		{
			SerialIO.writeInt(v, dout);
			return bout.toByteArray();
		}
	}

	private static int bytesToInt(final byte[] bytes) throws IOException {
		try (var bin = new ByteArrayInputStream(bytes);
			 var din = new DataInputStream(bin))
		{
			return SerialIO.readInt(din);
		}
	}

	private static byte[] longToBytes(final long v) throws IOException {
		try (var bout = new ByteArrayOutputStream();
			 var dout = new DataOutputStream(bout))
		{
			SerialIO.writeLong(v, dout);
			return bout.toByteArray();
		}
	}

	private static long bytesToLong(final byte[] bytes) throws IOException {
		try (var bin = new ByteArrayInputStream(bytes);
			 var din = new DataInputStream(bin))
		{
			return SerialIO.readLong(din);
		}
	}

	@DataProvider
	public Object[][] intValues() {
		return new Object[][] {
			{-10, new byte[] {19}},
			{-9, new byte[] {17}},
			{-8, new byte[] {15}},
			{-7, new byte[] {13}},
			{-6, new byte[] {11}},
			{-5, new byte[] {9}},
			{-4, new byte[] {7}},
			{-3, new byte[] {5}},
			{-2, new byte[] {3}},
			{-1, new byte[] {1}},
			{0, new byte[] {0}},
			{1, new byte[] {2}},
			{2, new byte[] {4}},
			{3, new byte[] {6}},
			{4, new byte[] {8}},
			{5, new byte[] {10}},
			{6, new byte[] {12}},
			{7, new byte[] {14}},
			{8, new byte[] {16}},
			{9, new byte[] {18}},
			{27112, new byte[] {-48, -89, 3}},
			{23437, new byte[] {-102, -18, 2}},
			{1877, new byte[] {-86, 29}},
			{-28932, new byte[] {-121, -60, 3}},
			{10067, new byte[] {-90, -99, 1}},
			{20728, new byte[] {-16, -61, 2}},
			{21799, new byte[] {-50, -44, 2}},
			{-28062, new byte[] {-69, -74, 3}},
			{-23126, new byte[] {-85, -23, 2}},
			{21815, new byte[] {-18, -44, 2}},
			{-495797281, new byte[] {-63, -112, -22, -40, 3}},
			{-1408217539, new byte[] {-123, -57, -3, -66, 10}},
			{-1356607317, new byte[] {-87, -67, -31, -115, 10}},
			{1959965842, new byte[] {-92, -46, -107, -51, 14}},
			{-124071904, new byte[] {-65, -65, -87, 118}},
			{-1416473820, new byte[] {-73, -77, -19, -58, 10}},
			{-1146259917, new byte[] {-103, -89, -108, -59, 8}},
			{-1873603042, new byte[] {-61, -89, -25, -6, 13}},
			{-285925923, new byte[] {-59, -120, -41, -112, 2}},
			{-1985970492, new byte[] {-9, -124, -4, -27, 14}}
		};
	}

	@DataProvider
	public Object[][] longValues() {
		return new Object[][] {
			{-10, new byte[] {19}},
			{-9, new byte[] {17}},
			{-8, new byte[] {15}},
			{-7, new byte[] {13}},
			{-6, new byte[] {11}},
			{-5, new byte[] {9}},
			{-4, new byte[] {7}},
			{-3, new byte[] {5}},
			{-2, new byte[] {3}},
			{-1, new byte[] {1}},
			{0, new byte[] {0}},
			{1, new byte[] {2}},
			{2, new byte[] {4}},
			{3, new byte[] {6}},
			{4, new byte[] {8}},
			{5, new byte[] {10}},
			{6, new byte[] {12}},
			{7, new byte[] {14}},
			{8, new byte[] {16}},
			{9, new byte[] {18}},
			{-8588, new byte[] {-105, -122, 1}},
			{-12086, new byte[] {-21, -68, 1}},
			{14562, new byte[] {-60, -29, 1}},
			{12119, new byte[] {-82, -67, 1}},
			{-30766, new byte[] {-37, -32, 3}},
			{30599, new byte[] {-114, -34, 3}},
			{-31374, new byte[] {-101, -22, 3}},
			{13156, new byte[] {-56, -51, 1}},
			{2100, new byte[] {-24, 32}},
			{15546, new byte[] {-12, -14, 1}},
			{559984399, new byte[] {-98, -68, -123, -106, 4}},
			{-1898814382, new byte[] {-37, -18, -20, -110, 14}},
			{-1980238794, new byte[] {-109, -81, -64, -32, 14}},
			{-179500454, new byte[] {-53, -42, -105, -85, 1}},
			{572795674, new byte[] {-76, -84, -95, -94, 4}},
			{1083564340, new byte[] {-24, -124, -81, -119, 8}},
			{-1314410566, new byte[] {-117, -63, -62, -27, 9}},
			{1041488910, new byte[] {-100, -16, -98, -31, 7}},
			{-834973570, new byte[] {-125, -66, -91, -100, 6}},
			{-2090210073, new byte[] {-79, -52, -80, -55, 15}},
			{-8736823572565048846L, new byte[] {-101, -8, -3, -65, -4, -81, -73, -65, -14, 1}},
			{1416695606955489398L, new byte[] {-20, -31, -127, -9, -64, -55, -114, -87, 39}},
			{6333018175258741658L, new byte[] {-76, -82, -98, -58, -64, -98, -77, -29, -81, 1}},
			{-499232509330329665L, new byte[] {-127, -63, -80, -81, -49, -44, -48, -19, 13}},
			{-3718796680863309994L, new byte[] {-45, -126, -109, -84, -102, -8, -24, -101, 103}},
			{-469163413725615018L, new byte[] {-45, -50, -52, -117, -13, -24, -26, -126, 13}},
			{-8334725852103261769L, new byte[] {-111, -7, -119, -82, -102, -64, -14, -86, -25, 1}},
			{-7605435930965462360L, new byte[] {-81, -75, -126, -77, -30, -21, -9, -117, -45, 1}},
			{5969015904234426706L, new byte[] {-92, -59, -26, -24, -34, -107, -102, -42, -91, 1}},
			{2770230521303604579L, new byte[] {-58, -11, -8, -125, -21, -2, -22, -15, 76}}
		};
	}

}
