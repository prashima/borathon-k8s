#!/bin/bash

# Copyright 2016 The Kubernetes Authors All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Source common.sh
source $(dirname "${BASH_SOURCE}")/common.sh

# Set MASTER_IP to localhost when deploying a master
MASTER_IP=localhost

# Check if the user specified the etcd IPs
# We do it before we call main because it will set ETCD_IPS.
if [[ ! -z "${ETCD_IPS}" ]]; then
  EDIT_ETCD="true"
fi

kube::multinode::main

# If an etcd server was specified, then update our configuration to point at it
if [[ ! -z ${EDIT_ETCD} ]]; then
  sed -i -e "s@etcd-servers.*\"@etcd-servers=${ETCD_SERVERS}\"@" /etc/kubernetes/manifests-multi/master-multi.json
fi

kube::multinode::log_variables

kube::multinode::turndown

if [[ ${USE_CNI} == "true" ]]; then
  kube::cni::ensure_docker_settings

  kube::multinode::start_flannel
else
  kube::bootstrap::bootstrap_daemon

  kube::multinode::start_flannel

  kube::bootstrap::restart_docker
fi

kube::multinode::start_k8s_master

kube::log::status "Done. It may take about a minute before apiserver is up."
