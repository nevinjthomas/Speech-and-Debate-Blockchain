#!/bin/bash
#
docker stop $(docker ps -a -q)
./network.sh down
docker container prune --force 
docker volume prune --force
docker volume ls
docker ps -a
