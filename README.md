# distributed-cloud-unzipper
Simple app to demonstrate distributed work using consistent hashing

### Running it locally

Generating some zip files for testing
```
cd src/main/resources/remote
chmod a+x file-gen.sh
./file-gen <number_of_files> <number_of_zips>
./file-gen 20 5 # should create 5 zips containing 4 files each
```
From the project root, build the jar
`mvn package`

```
java -jar target/distributed-cloud-unzipper-0.0.1-SNAPSHOT.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker1 \
--cloud ON_PREM

java -jar target/distributed-cloud-unzipper-0.0.1-SNAPSHOT.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker2 \
--cloud ON_PREM

java -jar target/distributed-cloud-unzipper-0.0.1-SNAPSHOT.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker3 \
--cloud ON_PREM

```
After those commands, there should be 20 files under `src/main/resources/remote` folder


### Creating the docker image

Build the image and tag it
`docker build . -t distributed-cloud-unzipper:0.1`

Cleanup `rm -rf src/main/resources/remote/*.bin; rm -rf src/main/resources/work/*` from the root folder

Test the jar on the image
`docker run -it -v <path_to_project>/distributed-cloud-unzipper/src/main/resources:/home distributed-cloud-unzipper:0.1`


```
java -jar distributed-cloud-app.jar \
--participatingWorkers worker1,worker2,worker3 \
--currentWorker worker1 \
--remoteDir /home/remote \
--workDir /home/work \
--cloud ON_PREM
```


### Running on local kubernetes

For easier testing with local kubernetes the app can be deployed on a local registry, below command starts a local registry
`docker run -d -p 5000:5000 --restart=always --name registry registry:2`

Now push the image to local repo so that it can be easily pulled down by kubernetes
`docker push localhost:5000/distributed-cloud-unzipper:0.1`

Cleanup `rm -rf src/main/resources/remote/*.bin; rm -rf src/main/resources/work/*` from the root folder

Remove any running jobs/pods/deployments on kubernetes
`kubectl delete pods --all ; kubectl delete jobs --all; kubectl delete deployments --all`

Deploy the job on kubernetes
`kubectl apply -f job-local.yaml`

Check the status, all pods should be completed if they are successful
`kubectl get pods`
`kubectl logs <pod_name>`
`kubectl get jobs`

After those commands, there should be 20 files under `src/main/resources/remote` folder


### Running on local kubernetes with gcs (Used for testing with gcs locally)

Install & initialize gcloud tools for mac: https://cloud.google.com/sdk/docs/quickstart-macos
Run `gcloud init` to initialize api credentials locally. (Note: this will count towards your cloud api usage limits)


### Running on Google Kubernetes Engine

Build and Push the image to docker hub ( or google container registry )
`docker build . -t deyboy90/distributed-cloud-unzipper:0.1 ` 
`docker push deyboy90/distributed-cloud-unzipper:0.1`

Now, setup a standard cluster from the console, with read-write access to cloud storage service. Connect to it using Google cloud shell. Pull this git repo and cd into it. 

Setting up access to the kubernetes API, so that they can be called from inside the container (needed for getting the pods which used to build consistent hash) 
```
# Get the user account
gcloud info | grep Account
<myname>@<example.org>

# Grant cluster-admin to your current identity
kubectl create clusterrolebinding myname-cluster-admin-binding --clusterrole=cluster-admin --user=myname@example.org

# Create cluster role which enables access to reading pods
kubectl create -f pod-reader-clusterrole.yaml

# Create cluster binding to the cluster role created above
kubectl create clusterrolebinding deyboy90-pod-reader --clusterrole=pod-reader --serviceaccount=default:default
```

Creating a storage bucket and upload zips
Create a <bucket>/remote dir under it and use the name in the job-gcp.yaml mount.

Upload the test zips which were created during local testing.
```
cd src/main/resources/remote
chmod a+x file-gen.sh
./file-gen <number_of_files> <number_of_zips>
./file-gen 20 5 # should create 5 zips containing 4 files each

# Upload to gcs using gsutils
gsutil -m cp *.zip gs://coverage-storage/remote
```

Deploy the job on kubernetes
`kubectl apply -f job-gcp.yaml`

Check the status, all pods should be completed if they are successful
`kubectl get pods`
`kubectl logs <pod_name>`
`kubectl get jobs`

After those commands, there should be 20 bin files under <bucket>/remote folder, use gsutil/browser to validate.
`gsutil ls  gs://<bucket>/remote/*.bin | wc -l`

