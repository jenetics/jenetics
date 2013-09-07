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
/* Header for class org_jenetics_util_IndexStream */

#ifndef _Included_org_jenetics_util_IndexStream
#define _Included_org_jenetics_util_IndexStream
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_jenetics_util_IndexStream
 * Method:    next
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_org_jenetics_util_IndexStream_next
  (JNIEnv *, jclass, jint, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
