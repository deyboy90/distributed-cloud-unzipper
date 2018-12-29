# distributed-cloud-unzipper
Simple app to demonstrate distributed work

### Creating the docker image
`mvn package`

```
java -jar target/distributed-cloud-unzipper-0.0.1-SNAPSHOT.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker1 \
--cloud ON_PREM
```
Build the image and tag it
`docker build . -t distributed-cloud-unzipper:0.1`

Test the jar on the image
`docker run -it -v /Users/deyarchit/Projects/Eclipse_Workspace/distributed-cloud-unzipper/:/home distributed-cloud-unzipper:0.1`

```
java -jar distributed-cloud-app.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker1 \
--remoteDir /home/remote \
--workDir /home/work \
--cloud ON_PREM
```


### Running on kubernetes

For easier testing with local kubernetes the app can be deployed on a local registry
`docker run -d -p 5000:5000 --restart=always --name registry registry:2`

Now push the image
`docker push localhost:5000/distributed-cloud-unzipper:0.1`

Deploy the job on kubernetes
`kubectl apply -f job-local.yaml`

Check the job status, all pods should be completed
`kubectl get jobs`

