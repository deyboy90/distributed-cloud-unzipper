#!/bin/bash

# Waiting for all pods to come up
sleep 10

KUBE_TOKEN=$(</var/run/secrets/kubernetes.io/serviceaccount/token)

PODS=$( curl -sSk -H "Authorization: Bearer $KUBE_TOKEN"       https://$KUBERNETES_SERVICE_HOST:$KUBERNETES_PORT_443_TCP_PORT/api/v1/namespaces/default/pods/ | grep \"podIP\"\: | awk -F':' '{print $2}' | awk '{printf("%s", $0)}' | sed 's/ //g' | sed 's/\,$//' | sed 's/\"//g' )

java -jar distributed-cloud-app.jar \
--participatingWorkers $PODS \
--currentWorker $MY_POD_IP \
--remoteDir /home/remote \
--workDir /home/work \
--cloud ON_PREM


