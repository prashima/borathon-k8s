# Building and running VCS as a container
*The updated start_vcs.sh is in the same directory. Replace the vcs/scripts/start_vcs.sh with this one.*

**Steps**
1. Copy the extracted VCS binary to the current folder and rename the root of extracted VCS binary as "vcs"
2. Run the following command to build an image
*docker build -t sandeeppissay/vcs:v0.3 .*
3. The built VCS container image takes the vic-engine IP as a parameter when deployed. So, to test the VCS container locally (on local docker daemon), run this command
*docker run --name vcs -d -it -p 19000:19000 sandeeppissay/vcs:v0.3 <VCH IP Address>*
4. The above command should deploy VCS on your local docker environment. To login to the VCS container's shell, run this command
*docker exec -it vcs /bin/bash*
5. Repeat step 3 on VIC engine to deploy VCS into VIC.

# Running VCS in kubernetes
**NOTE: Work in progress**
This can be useful if we need high availability of VCS service itself. Here's the sample vcs.yaml content:
```javascript
apiVersion: v1
kind: Pod
metadata:
  name: vcs-demo-1
spec:
  containers:
  - name: vcs
    image: sandeeppissay/vcs:v0.3
    args: [vic-engine IP]
    ports:
    - containerPort: 19000
      hostPort: 19000
  restartPolicy: "OnFailure"
```

Deploy VCS in kubernetes with this command:
*kubectl -s <master IP>:8080 create -f vcs.yaml*

Note that this will create one single pod having VCS app in it. Need to investigate on how to deploy multiple such pods and also connect them (xenon peer nodes).
