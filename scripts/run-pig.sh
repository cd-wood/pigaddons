#!/bin/bash

if [ ! -f "${PIG_HOME}/bin/pig" ]
then
  echo "Please define PIG_HOME"
  exit 1
fi

if [ ! -f "${JAVA_HOME}/bin/java" ]
then
  export JAVA_HOME=/usr/lib/jvm/java-6-openjdk
fi

INPUT=./pigaddons/examples
OUTPUT=./
SCRIPTS=./pigaddons/examples
JARS=./pigaddons/target

${PIG_HOME}/bin/pig -x local -p INPUT=$INPUT -p OUTPUT=$OUTPUT -p SCRIPTS=$SCRIPTS -p JARS=$JARS $*
