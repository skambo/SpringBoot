#!/bin/bash

set -e

if  [ $(export | grep -c ENVIRONMENT_VARIABLES_SECRET_FILE) -ge "1" ]
then
     eval $(cat ENVIRONMENT_VARIABLES_SECRET_FILE)
fi

# Wait for db to be ready 
./wait-for-it.sh -t 120 $DATABASE_HOST:$DATABASE_PORT

# run application
java -jar springBootExample-1.0-SNAPSHOT.jar
