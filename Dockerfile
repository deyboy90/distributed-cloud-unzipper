FROM java:8
WORKDIR	/
ADD target/distributed-cloud-unzipper-0.0.1-SNAPSHOT.jar distributed-cloud-app.jar
ADD startup.sh startup.sh
