#!/bin/bash
export JAVA_HOME=/var/opt/OpenJDK-1.8.0.112-bin
echo "Starting VCS service ..." >> /vcs/vcs.log
echo "Script arguments: $*" >>/vcs/vcs.log
sed "s/dockerEngineIp=.*/dockerEngineIp=$1/" /vcs/config/vcs.properties > /tmp/tmp
mv /tmp/tmp /vcs/config/vcs.properties
/vcs/bin/vcs /vcs/config/vcs.properties >> /vcs/vcs.log 2>&1
