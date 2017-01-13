# Building and running VCS as a container
*The updated start_vcs.sh is in the same directory. Replace the vcs/scripts/start_vcs.sh with this one.*<br/>

**Steps**<br/>
1. Copy the extracted VCS binary to the current folder and rename the root of extracted VCS binary as "vcs"<br/>
2. Run the following command to build an image<br/>
*docker build -t sandeeppissay/vcs:v0.3 .*<br/>
3. The built VCS container image takes the vic-engine IP as a parameter when deployed. So, to test the VCS container locally (on local docker daemon), run this command<br/>
*docker run --name vcs -d -it -p 19000:19000 sandeeppissay/vcs:v0.3 <VCH IP Address>*<br/>
4. The above command should deploy VCS on your local docker environment. To login to the VCS container's shell, run this command<br/>
*docker exec -it vcs /bin/bash*<br/>
5. Repeat step 3 on VIC engine to deploy VCS into VIC.<br/>

# Running VCS in kubernetes
**NOTE: Work in progress**<br/>
This can be useful if we need high availability of VCS service itself. Here's the sample vcs.yaml content:<br/>
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

Deploy VCS in kubernetes with this command:<br/>
*kubectl -s <master IP>:8080 create -f vcs.yaml*
<br/>
Note that this will create one single pod having VCS app in it. Need to investigate on how to deploy multiple such pods and also connect them (xenon peer nodes).
