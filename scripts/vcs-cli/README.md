# VCS Command line Interface 

Set environment variable VCS_HOST to point to VCS server.
```
export VCS_HOST=http://<VCS_SERVER_IP>:19000
```

Use `vcs --help` to get up-to-date information on the CLI. 

Usage examples:
```
vcs info 
vcs cluster ls
vcs cluster create --name=testCluster --etcd_ips=10.20.104.95 --master_ip=10.20.104.96
vcs cluster ls -c Name,UUID
vcs cluster resize --uuid=13e7484a-b14d-4a9f-bbf4-2aafc18be465 --slave_count=2
vcs cluster lsvm --uuid=13e7484a-b14d-4a9f-bbf4-2aafc18be465
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
