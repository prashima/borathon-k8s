This is a prototype of kubernetes deployment on vCenter.

# Setup
## For VM deployment
* 1 vCenter.
* 1 ESX.
* 1 datastore with at least 40GB of space.

## For running this kubernetes deployment service
Currently the prototype has been tested by running the service in eclipse only.

### Installations
* Install ISO generation utility genisoimage or mkisofs.
* Download and install JDK 8 (latest patch).
* Download and install latest version of gradle from https://gradle.org/gradle-download/.
* Install latest version of Eclipse IDE.
* Install gradle eclipse plugin (buildship) in eclipse.

### Building and importing project in eclipse
* Clone this repo and go to java directory> cd [project-dir]/vcs-proto1/java
* Build the project by executing command> gradle build
* Once the project has built successfully import [project-dir]/vcs-proto1/java as gradle project in eclipse. The parent gradle project (build.gradle file) is in vcs-proto1/java directory.

### Property files
* Edit java/common/src/main/resources/vcs.properties file as per your dev environment setup.
* Edit java/vsphere-client/src/main/resources/vsphere-client.properties to point to your vCenter setup.
  * One of the working kubernetes image is available here http://pa-dbc1131.eng.vmware.com/prashimas/misc/images/kubernetes/, currently accessible from within VMware network (will fix this soon!).

### Running the service
* The main class for this service is com.vmware.vcs.core.Main. Run this as Java application with the following program and VM arguments.
  * Under program arguments specify “java/common/src/main/resources/vcs.properties” file as an argument.
  * Under VM arguments specify “-Dvsphere-client-properties=vsphere-client.properties –Dssl.trustAll.enabled=true”


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

# Note
* There will be a parent image VM and snapshot created in vCenter. Do not delete the VM and snapshot.
* First cluster creation might take some time due to image upload.