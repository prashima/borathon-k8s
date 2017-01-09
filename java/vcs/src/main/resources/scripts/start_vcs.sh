echo "Installing openjdk ..."
tdnf install -y openjdk || exit $?

echo "Installing docker ..."
tdnf install -y docker || exit $?

echo "Installing perl ..."
tdnf install -y perl || exit $?

export JAVA_HOME=/var/opt/OpenJDK-1.8.0.112-bin
iptables -P INPUT ACCEPT
iptables -F

echo "Starting VCS service ..."
../bin/vcs ../config/vcs.properties
