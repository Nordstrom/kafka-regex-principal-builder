#!/bin/bash

ensure_connection() {
  local host=zookeeper

  while true; do
    ping -W 1 -c 1 -t 10 -o $host >/dev/null 2>&1
    if [[ $? = 0 || $? = 2 ]]; then
      echo "Connected to ${host}"
      break
    fi

    echo "Waiting for ${host}. Trying again in 10 seconds."
    sleep 10
  done
}

setup_admin() {
  local name=admin
  local pass=sgtpepper
  local host=zookeeper

  kafka-configs --alter \
    --zookeeper "${host}:2181" \
    --add-config "SCRAM-SHA-256=[password=${pass}],SCRAM-SHA-512=[password=${pass}]" \
    --entity-type users \
    --entity-name $name
}

ensure_connection
setup_admin

while true; do
  sleep 1000
done
