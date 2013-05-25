#!/bin/bash

SCRIPT_DIRECTORY=$(dirname $(readlink -f $0))
RJAVA_VERSION=0.9-4
R_LIB_DIR=/home/$USER/R_libs
JRI_HOME=${R_LIB_DIR}/rJava/jri
RPIG_VERSION=0.1-SNAPSHOT
RPIG_JAR=$(readlink -f "${SCRIPT_DIRECTORY}/../target/pigaddons-${RPIG_VERSION}.jar")

if [ ! -f "${PIG_HOME}/bin/pig" ]
then
  echo "PIG_HOME not set"
  exit 1
fi

sudo apt-get install -y r-base

export R_HOME="$(R RHOME)"

mkdir -p ${R_LIB_DIR}
if [ ! -f "${R_LIB_DIR}/rJava_${RJAVA_VERSION}.tar.gz" ]
then
  wget -O ${R_LIB_DIR}/rJava_${RJAVA_VERSION}.tar.gz http://cran.r-project.org/src/contrib/rJava_${RJAVA_VERSION}.tar.gz
fi
R CMD INSTALL -l ${R_LIB_DIR} ${R_LIB_DIR}/rJava_0.9-4.tar.gz

PIG_CONF=${PIG_HOME}/conf/pig-env.sh

if [ -f ${PIG_CONF} ]
then
  rm ${PIG_CONF}
fi

echo "RPIG_JAR=\"${RPIG_JAR}\"" >> ${PIG_CONF}
echo "export JRI_HOME=${JRI_HOME}" >> ${PIG_CONF}
echo "export R_HOME=${R_HOME}" >> ${PIG_CONF}
echo "export R_LIBS_USER=\"${R_LIB_DIR}\"" >> ${PIG_CONF}
echo "export PIG_OPTS=\"\$PIG_OPTS -Drpig.gfx.width=640 -Drpig.gfx.height=480 -Drpig.gfx.ps=12\"" >> ${PIG_CONF}
echo "export LD_LIBRARY_PATH=\"\${LD_LIBRARY_PATH}:\${R_HOME}/bin:\${JRI_HOME}\"" >> ${PIG_CONF}
echo "export PIG_CLASSPATH=\"\${RPIG_JAR}\"" >> ${PIG_CONF}