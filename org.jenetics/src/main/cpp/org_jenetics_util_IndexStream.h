/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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
