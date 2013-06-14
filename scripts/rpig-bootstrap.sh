#!/bin/bash

# The MIT License (MIT)
#
# Copyright (c) 2013 Connor Woodson
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
# IN THE SOFTWARE.


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
  rm -f ${PIG_CONF}
fi

echo "RPIG_JAR=\"${RPIG_JAR}\"" >> ${PIG_CONF}
echo "export JRI_HOME=${JRI_HOME}" >> ${PIG_CONF}
echo "export R_HOME=${R_HOME}" >> ${PIG_CONF}
echo "export R_LIBS_USER=\"${R_LIB_DIR}\"" >> ${PIG_CONF}
echo "export PIG_OPTS=\"\$PIG_OPTS -Drpig.gfx.width=640 -Drpig.gfx.height=480 -Drpig.gfx.ps=12\"" >> ${PIG_CONF}
echo "export LD_LIBRARY_PATH=\"\${LD_LIBRARY_PATH}:\${R_HOME}/bin:\${JRI_HOME}\"" >> ${PIG_CONF}
echo "export PIG_CLASSPATH=\"\${RPIG_JAR}\"" >> ${PIG_CONF}