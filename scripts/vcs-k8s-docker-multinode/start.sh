#!/bin/bash

if [[ "$NODETYPE" == "etcd" ]]; then
./start-etcd.sh
elif [[ "$NODETYPE" == "master" ]]; then
./start-master.sh
elif [[ "$NODETYPE" == "worker" ]]; then
./start-worker.sh
else
echo "No NODETYPE is set for this K8S CVM: please set etcd, master, or worker"
fi
