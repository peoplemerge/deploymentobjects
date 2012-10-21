#!/bin/bash
case $( uname -s ) in
Darwin) export LD_LIBRARY_PATH=/opt/local/lib;echo Using LD_LIBRARY_PATH $LD_LIBRARY_PATH;;
esac
LD_LIBRARY_PATH=$LD_LIBRARY_PATH mvn -q exec:java -Dexec.mainClass="org.deploymentobjects.core.application.EntryPoint" -Dexec.args="$*"
