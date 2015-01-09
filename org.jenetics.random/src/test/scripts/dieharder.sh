#!/bin/bash

SCRIPT_DIR=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_DIR}`
BASE_DIR=`readlink -f ${SCRIPT_DIR}/../../../../`

PROPERTIES="${BASE_DIR}/project.properties"
VERSION=`${SCRIPT_DIR}/read_properties.sh jenetics.version ${PROPERTIES}`

CLASSPATH="${BASE_DIR}/org.jenetics/build/libs/org.jenetics-${VERSION}.jar"
CLASSPATH="${CLASSPATH}:${BASE_DIR}/org.jenetics.random/build/libs/org.jenetics.random-${VERSION}.jar"

RANDOM_CLASS=$1
java -cp "${CLASSPATH}" org.jenetics.internal.util.DieHarder ${RANDOM_CLASS} -a
