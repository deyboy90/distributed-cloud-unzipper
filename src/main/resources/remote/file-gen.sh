#!/bin/bash

TOTAL_FILES=$1
TOTAL_ZIPS=$2
TOTAL_FILES_PER_ZIP=$(( $TOTAL_FILES / $TOTAL_ZIPS ))
COUNT=1

for (( n=1; n<=$TOTAL_FILES; n++)); do
    dd if=/dev/urandom of=file$( printf %03d "$n" ).bin bs=1 count=$(( RANDOM + 1024 ))
    
    NUM=$(( $n % $TOTAL_FILES_PER_ZIP ))
    echo "num = $NUM"
    if [ $NUM -eq 0 ]; then
       zip "file_$COUNT.zip" *.bin
       rm -rf *.bin
       COUNT=$(( $COUNT + 1))
    fi
done
