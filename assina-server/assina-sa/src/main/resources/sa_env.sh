#!/usr/bin/env bash
# Sets up the environment for the SA

# Port this SA run on - must be specified in the client app
export SERVER_PORT=8081
export ASSINA_RSSP_BASE_URL=http://13.93.7.40
#export ASSINA_RSSP_BASE_URL=http://localhost
export ASSINA_RSSP_PORT=80
#export ASSINA_RSSP_PORT=8080

export JAVA_HOME=/usr/bin/java

export FILE_UPLOADDIR=./files
# Must point to the redirect on the node client server and port
export LOGS=./logs
mkdir -p $LOGS

mkdir -p $FILE_UPLOADDIR
export CSC_BASE_URL=${ASSINA_RSSP_BASE_URL}:${ASSINA_RSSP_PORT}/csc/v1

export SPRING_SERVLET_MULTIPART_MAX-FILE-SIZE=1MB
export SPRING_SERVLET_MULTIPART_MAX-REQUEST-SIZE=1MB
export SPRING_SERVLET_MULTIPART_FILE-SIZE-THRESHOLD=20KB

export FILE_EXTENSIONS=pdf
