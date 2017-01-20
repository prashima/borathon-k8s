This is a prototype of kubernetes deployment on vCenter using vSphere Integrated Containers.

# Installation of vSphere Container Service (VCS) in vSphere Container Host (VCH)
There are 2 ways of deploying VCS in VCH -
* Installing VCS inside VCH VM
-- VCS runs as a separate service along side VIC engine in the same VM
* Deploying VCS as a container VM on VIC engine
-- VCS runs as a container VM outside VCH VM.

## Installation of VCS in vSphere Container Host(VCH) VM
* Deploy VCH to a Basic vCenter Server Cluster - https://vmware.github.io/vic-product/assets/files/html/0.8/vic_installation/deploy_vch_vcenter.html
Here's a sample command to create VCH -

vic-machine-windows.exe create --name san-7  --target 10.20.104.101/DC --compute-resource DRS1 --user "Administrator@vsphere.local" --bridge-network vic-bridge --image-store nfsDatastore/san-7 --no-tlsverify --force --timeout 15m0s

* Enable ssh on VCH VM as we need to copy VCS binary and start it on VCH VM. Sample command -

vic-machine-windows.exe debug --target 10.20.104.101 --compute-resource DRS1 --user "Administrator@vsphere.local" --name san-7 --rootpw "ca$hc0w" -thumbprint "BE:A7:D1:F0:9A:D3:D8:BB:D5:1F:D7:C4:73:7C:9B:4A:70:3B:BE:12"

* In VCS project, build VCS distribution (tar and zip)

cd borathon-k8s/java

gradle build -x test

* Copy VCS zip file to VCH VM

cd vcs/build/distributions

scp vcs-vcs0.1-13e58dc.zip root@10.20.104.161:~/

* In VCH VM, install unzip utility

rpm --rebuilddb

tdnf install -y unzip

* Unzip VCS

unzip vcs-vcs0.1-13e58dc.zip

* Rename vcs-vcs0.1* folder to vcs

mv vcs-vcs0.1-13e58dc vcs

* Remove the zip file to save disk space since there is very limited disk space in VCH VM

rm vcs-vcs0.1-13e58dc.zip

* Run VCS after editing config/vcs.properties with correct properties (give proper value for dockerEngineIp property)

cd vcs/scripts/

nohup ./start_vcs.sh 2>&1 > ~/vcs.log &

## Deploying VCS as a container VM on VIC engine
* Create VCH

vic-machine-windows.exe create --name vcs-vch  --target 10.192.71.215/VSAN-DC --compute-resource VSAN-Cluster --user "Administrator@vsphere.local" --bridge-network vic-bridge --image-store vsanDatastore/vcs-vch --no-tlsverify --force --timeout 15m0s

* Using the VIC engine

export DOCKER_API_VERSION=1.23

export DOCKER_HOST=10.192.67.93:2376

docker --tls ps -a

* Deploying VCS as container VM in VIC

docker --tls run --name vcs -dit -p 19000:19000 sandeeppissay/vcs:v0.3 <VCH VM IP>

Example: docker --tls run --name vcs -dit -p 19000:19000 sandeeppissay/vcs:v0.3 10.192.67.93

# Download vcs client tool
wget https://raw.githubusercontent.com/prashima/borathon-k8s/vcs0.1/scripts/vcs-cli/vcs

chmod +x vcs

# Test VCS is running
export VCS_HOST=10.192.67.93:19000

vcs info

vcs --help

vcs cluster ls

# Create a simple k8s cluster
vcs cluster create --name=k8s-3 --type=KUBERNETES --etcd_ips=172.16.0.30 --master_ip=172.16.0.31 --slave_count=1

# List container VMS under the cluster
vcs cluster lsvm --uuid=<uuid>

# List the container VMs from VIC
docker --tls ps

# Deploy a demo app (login to VCH VM and then run the commands)
./kubectl -s 172.16.0.21:8080 create -f nginx-demo.yaml

./kubectl -s 172.16.0.11:8080 describe pod demo-1

# Resize the k8s cluster
vcs cluster resize --uuid=<uuid> --slave_count=2

# Stop one of the slave
docker --tls stop worker-177321dd-05bd-49f7-a4b4-6a40b8f6202b

# Check health
vcs cluster ls

# Building and importing project in eclipse
* Clone this repo and go to java directory> cd [project-dir]/vcs-proto1/java
* Build the project by executing command> gradle build
* Once the project has built successfully import [project-dir]/vcs-proto1/java as gradle project in eclipse. The parent gradle project (build.gradle file) is in vcs-proto1/java directory.

## Property files
* Edit java/common/src/main/resources/vcs.properties file as per your dev environment setup.
* Edit java/vsphere-client/src/main/resources/vsphere-client.properties to point to your vCenter setup.
  * One of the working kubernetes image is available here http://pa-dbc1131.eng.vmware.com/prashimas/misc/images/kubernetes/, currently accessible from within VMware network (will fix this soon!).

## Running the service
* The main class for this service is com.vmware.vcs.core.Main. Run this as Java application with the following program and VM arguments.
  * Under program arguments specify “java/common/src/main/resources/vcs.properties” file as an argument.
  * Under VM arguments specify “-Dvsphere-client-properties=vsphere-client.properties –Dssl.trustAll.enabled=true”
