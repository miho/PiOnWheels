#!/bin/bash

CALL_PATH=$(pwd)

APPDIR="$(dirname "$0")"
cd "$APPDIR"
APPDIR="$(pwd)"

rm log.log

java -jar pow-server-0.1-SNAPSHOT.jar
