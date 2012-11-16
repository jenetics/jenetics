//#include<stdio.h>
#include<stdlib.h>
//#include<time.h>


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


