# VCS Command line Interface 

Use `vcs --help` to get up-to-date information on the CLI. 

Usage examples:
```
vcs info 
vcs cluster ls
vcs cluster create --name=testCluster --etcd_ips=10.20.104.95 --master_ip=10.20.104.96 --container_network=10.20.0.1/20
vcs cluster ls -c Name,UUID
vcs cluster rm --uuid=13e7484a-b14d-4a9f-bbf4-2aafc18be465
```

You can remove all clusters using this:
```
for i in `vcs cluster ls -c UUID -q`; do vcs cluster rm --uuid=$i; done
```

# Swagger API yaml for VCS

The vcs.yaml contain swagger definition. It is currently work in progress. 

The easiest way to use it is to cut-n-paste the file to http://editor.swagger.io/#/ to see API doc.

You can also install local swagger editor to try the API out (do not forget to set 'host:' attribute if the host is not local)