#!/usr/bin/env bash

read_link() {
	local path=$1
	if [ -d ${path} ] ; then
		local abspath=$(cd ${path}; pwd)
	else
		local prefix=$(cd $(dirname -- ${path}); pwd)
		local suffix=$(basename ${path})
		local abspath="$prefix/$suffix"
	fi
	echo ${abspath}
}

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JRUN=`read_link "${SCRIPT_DIR}/../../../../jrun"`

main() {
	#${JRUN} org.jenetics.random.internal.DieHarder org.jenetics.random.KISS32Random -a
	#${JRUN} org.jenetics.random.internal.DieHarder org.jenetics.random.KISS64Random -a
	${JRUN} org.jenetics.random.internal.DieHarder org.jenetics.random.LCG64Random -a
}

main $*
