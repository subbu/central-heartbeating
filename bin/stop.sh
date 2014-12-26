#!/bin/bash

source lib.sh

eval "array=({1..${total_processes}})"

for processNum in "${array[@]}"; do
    PID=`cat ./${processNum}.pid`
    if [ $? -ne 1 ]; then
        echo "killing process $processNum"
        kill "${PID}"
        rm "${processNum}.pid"
        rm "$processNum-system.log"
    fi
done