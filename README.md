This is a prototype of kubernetes deployment on vCenter using vSphere Integrated Containers.

# Installation and running vSphere Container Service (VCS) in vSphere Container Host(VCH) VM
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

* Run VCS after editing config/vcs.properties and config/vsphere-client.properties with correct properties

cd vcs-vcs0.1-13e58dc/scripts/

nohup ./start_vcs.sh 2>&1 > ~/vcs.log &

# Using this service
Users will need REST API client to interacte with this service.

* GET http://127.0.0.1:19000/
  * Lists various services running within this service. Most of which are not of any use at this point. Except for the ones listed below.

* GET http://127.0.0.1:19000/vcs/cloudstore/clusters
  * Lists all clusters created with this service.

* DELETE http://127.0.0.1:19000/vcs/cloudstore/clusters/<cluster-uuid>
  * Deletes the cluster.

* POST http://127.0.0.1:19000/vcs/clustermanager/vcs-cluster-create
  * Creates a kubernestes cluster as per the passed arguments passed and set in the properties files.
  * Sample Body

``` json
 { "clusterState" : "CREATING",
   "clusterName" : "cluster1", 
   "clusterType" : "KUBERNETES", 
   "imageId" : "Kubernetes-image.vmdk", 
   "projectId" : "k8s-project", 
   "vmNetworkId" : "VM Network", 
   "diskFlavorName" : "Disk flavor", 
   "masterVmFlavorName" : "Master flavor", 
   "otherVmFlavorName" : "Slave Flavor", 
   "slaveCount" : "1", 
   "extendedProperties" : { 
      "etcd_ips" : "10.20.104.90", 
      "dns" : "10.20.145.1", 
      "gateway" : "10.20.107.253", 
      "netmask" : "255.255.252.0", 
      "master_ip" : "10.20.104.92", 
      "container_network" : "10.20.0.1/20" 
     } 
  }
```
  * Modify clusterName, etcd_ips, dns, gateway, netmask, master_ip to appropriate values as per your setup.

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

