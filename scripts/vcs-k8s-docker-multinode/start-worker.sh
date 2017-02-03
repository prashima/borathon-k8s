echo "Starting Kubernetes worker"
service docker start
sleep 10
docker load -i hyperkube.docker
export ETCD_IPS=${ETCD_IP}
export FLANNEL_NETWORK=10.2.0.0/16
/root/docker-multinode/worker.sh
echo "done"
