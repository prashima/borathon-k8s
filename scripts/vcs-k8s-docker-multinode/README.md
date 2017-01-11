All the local image files are missing due to large file limitation on git.

BUILD:

MY_REGISTRY=[your registry] MY_REPO=[your repo name] MY_VERSION=[your version] make build VERSION=[a released K8S version, e.g, v1.5.1]

docker push [your registry]/[your repo nane]:[your version]


USAGE:

docker --tls run -e ETCD_IP=[etcd ip address] -e MASTER_IP=[master ip address] -e NODETYPE=etcd --ip [etcd ip address] -dit --name etcd [your registry]/[your repo nane]:[your version] bash

docker --tls run -e ETCD_IP=[etcd ip address] -e MASTER_IP=[master ip address] -e NODETYPE=master --ip [master ip address] -dit --name master [your registry]/[your repo nane]:[your version] bash

docker --tls run -e ETCD_IP=[etcd ip address] -e MASTER_IP=[master ip address] -e NODETYPE=worker -dit --name worker [your registry]/[your repo nane]:[your version] bash
