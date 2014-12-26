#!/bin/sh

source lib.sh

function killRandomProcess {
    processNum=$1
    echo "Randomly chose process $processNum"
    if [ $processNum == $leader_process_id ]; then
        echo "  Can't kill the leader"
        return 0
    fi

    PID=`cat ./${processNum}.pid`
    if [ $? -ne 1 ]; then
        echo "  killing it"
        kill "${PID}"
        rm "${processNum}.pid"
        rm "$processNum-system.log"
    fi
}

random=$(( ( RANDOM % $total_processes )  + 1 ))
killRandomProcess $random

random=$(( ( RANDOM % $total_processes )  + 1 ))
killRandomProcess $random

