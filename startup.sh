#!/bin/bash

# Static wait for all pods to come up, so that we get all pods which are expected to run
sleep 10

# On local k8, the secrets are under /var/run/... , on GCK it's under /run/secrets...
if [ -f /var/run/secrets/kubernetes.io/serviceaccount/token ]
then
    KUBE_TOKEN=$(</var/run/secrets/kubernetes.io/serviceaccount/token)
else
	KUBE_TOKEN=$(</run/secrets/kubernetes.io/serviceaccount/token)
fi


# Curl command to get the list of pods in the default namespace, then using greps, seds, awk to extract pod ip's
PODS=$( curl -sSk -H "Authorization: Bearer $KUBE_TOKEN"       https://$KUBERNETES_SERVICE_HOST:$KUBERNETES_PORT_443_TCP_PORT/api/v1/namespaces/default/pods/ | grep \"podIP\"\: | awk -F':' '{print $2}' | awk '{printf("%s", $0)}' | sed 's/ //g' | sed 's/\,$//' | sed 's/\"//g' )

echo "Pods found: $PODS"

java -jar distributed-cloud-app.jar \
--participatingWorkers $PODS \
--currentWorker $MY_POD_IP \
--remoteDir $REMOTE_DIR \
--workDir $WORK_DIR \
--cloud $CLOUD


