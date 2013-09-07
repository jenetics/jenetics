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

#include <jni.h>

#include "org_jenetics_util_IndexStream.h"

jint nextInt() {
	static jint x = 314159265;
	x ^= x << 13;
	x ^= x >> 17;
	x ^= x << 5;
	return x;
}

JNIEXPORT jint JNICALL Java_org_jenetics_util_IndexStream_next(
	JNIEnv* env, jclass cls, jint n, jint pos, jint max
) {
	jint index = pos;
	while (index < n && nextInt() >= max) {
		++index;
	}
	return (index < n - 1) ? (index + 1) : -1;
}


