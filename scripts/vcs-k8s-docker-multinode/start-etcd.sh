echo "Starting Kubernetes etcd"
service docker start
sleep 10
docker load -i hyperkube.docker
export ETCD_NODE_ID=0
export ETCD_CLUSTER=etcd0=http://${ETCD_IP}:2380
export ETCD_PEER_URL=http://${ETCD_IP}:2380
export ETCD_ADVERTISE_URL=http://${ETCD_IP}:2379
echo "ETCD_NODE_ID ${ETCD_NODE_ID}"
echo "ETCD_CLUSTER ${ETCD_CLUSTER}"
echo "ETCD_PEER_URL ${ETCD_PEER_URL}"
echo "ETCD_ADVERTISE_URL ${ETCD_ADVERTISE_URL}"
/root/docker-multinode/etcd.sh
echo "done"
