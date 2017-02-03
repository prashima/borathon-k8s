FROM debian:jessie

RUN DEBIAN_FRONTEND=noninteractive apt-get update -y \
    && DEBIAN_FRONTEND=noninteractive apt-get -yy -q install \
    curl \
    apt-transport-https \
    ca-certificates \
    gnupg2 \
    && apt-key adv \
    --keyserver hkp://ha.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
    && echo "deb https://apt.dockerproject.org/repo debian-jessie main" >> /etc/apt/sources.list.d/docker.list \
    && DEBIAN_FRONTEND=noninteractive apt-get update -y \
    && DEBIAN_FRONTEND=noninteractive apt-get -yy -q install docker-engine

# Manifests for the docker guide
COPY static-pods/master.json \
     static-pods/etcd.json \
     static-pods/addon-manager-singlenode.json \
     static-pods/kube-proxy.json \
        /etc/kubernetes/manifests/

# Manifests for the docker-multinode guide
COPY static-pods/master-multi.json \
     static-pods/addon-manager-multinode.json \
        /etc/kubernetes/manifests-multi/

# Copy over all addons
COPY addons /etc/kubernetes/addons

# Copy other required scripts for the setup
COPY setup-files.sh make-ca-cert.sh copy-addons.sh /

COPY docker-multinode /root/docker-multinode
COPY start.sh start-etcd.sh start-master.sh start-worker.sh /root/
COPY kubectl /root/
COPY demo.yaml /root/
COPY hyperkube.docker /root/
COPY flannel.docker /root/
COPY etcd.docker /root/

ENTRYPOINT cd /root && ./start.sh > /root/start.log 2>&1 && bash
