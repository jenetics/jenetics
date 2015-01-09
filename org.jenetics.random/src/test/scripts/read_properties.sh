#!/bin/bash

# eg. ./parser.sh assets.url antx.properties
# eg. cat antx.properties | ./parser.sh assetsServer.url

set -e

target=$1

findStr()
{
    local target=$1
    local file=$2
    #echo target : $target
    #echo file : $file
    sed '/^\#/d' ${file} | grep ${target} | sed -e 's/ //g' |
        while read LINE
        do
            local KEY=`echo $LINE | cut -d "=" -f 1`
            local VALUE=`echo $LINE | cut -d "=" -f 2`
            [ ${KEY} == ${target} ] && {
                local UNKNOWN_NAME=`echo $VALUE | grep '\${.*}' -o | sed 's/\${//' | sed 's/}//'`
                if [ $UNKNOWN_NAME ];then
                    local UNKNOWN_VALUE=`findStr ${UNKNOWN_NAME} ${file}`
                    echo ${VALUE} | sed s/\$\{${UNKNOWN_NAME}\}/${UNKNOWN_VALUE}/
                else
                    echo $VALUE
                fi
                return
            }
        done
    return
}

if [ $2 ];then
    findStr ${target} $2
else
    while read data; do
        echo ${data} >> .__tmpfile19890102_
    done
    findStr ${target} .__tmpfile19890102_
    rm -f .__tmpfile19890102_
fi

