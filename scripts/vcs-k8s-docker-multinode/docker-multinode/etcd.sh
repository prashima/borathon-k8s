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

# Add ssh key for control node
(
echo "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAr3drMCO77BXrcH54QEjSEzyorcaTmhFBnxs2vtJ30fVbfv7HVDlpCwitWMQax5vISGQsmC3GuDgvVs4y4Eywx0ZkKQQb6TEjclkppn0AXPYvjoyVl7/KDCatOkuWLttOkqlktR+LCy4J3siunlSeFHT2ZYCAFy/hMz2XuDZxx3ovjRPUc7FKGU0aJy+DUggHVMNUikjTD+15SZuPr9brKAupz8kcKjebJ2+gCT8BWGVRxsLsl3M4Bzr9vJJhzNenZc3OFsROBCh+5BSXdI6+25wyZ8Hz/YmMpQGfmeMja58A1P+jhz6R9CSWU54TN0lUUbAsv7RbB4UTWN962hjvXQ== miaol@pa-dbc1112.eng.vmware.com"
) >> "/root/.ssh/authorized_keys"

kube::multinode::main

kube::multinode::log_variables

kube::multinode::turndown

# We're running etcd in the bootstrap daemon, just
# to make minimal changes to docker-multinode. It's
# not necessary though, and we could remove that. 
kube::bootstrap::bootstrap_daemon
kube::multinode::start_etcd

kube::log::status "Done."
