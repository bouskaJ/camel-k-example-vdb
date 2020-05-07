#!/bin/bash

function waitFor() {
  for i in {1..50}; do
    eval "echo  $i waiting for command $1"
    sleep 20
    eval "$1" && return
  done
  exit 1
}

TEST_HOME=$( dirname "${BASH_SOURCE[0]}")/..

sed "s/YAKS_NAMESPACE/${YAKS_NAMESPACE}/" "$TEST_HOME"/resources/operatorGroup.yaml | oc create -f - -n ${YAKS_NAMESPACE}

oc create -f "$TEST_HOME"/resources/dv-subscription.yaml -n ${YAKS_NAMESPACE}
waitFor "oc wait pod -l name=dv-operator --for condition=Ready -n ${YAKS_NAMESPACE} --timeout=5m"

oc create -f "$TEST_HOME"/../dv-dispatch.yaml -n ${YAKS_NAMESPACE}
waitFor '[[ "$(oc get vdb dv-dispatch -o 'jsonpath={.status.phase}' -n ${YAKS_NAMESPACE})" == "Running" ]]'
