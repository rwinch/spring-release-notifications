#!/bin/bash

APP_NAME=$1
SLACK_OAUTH2_TOKEN=$2
SLACK_CHANNEL_ID=$3
GITHUB_SECRET=$4
INFO_VERSION=$5

##########################################################################################

# the name that the current production application will get temporarily while we deploy
# the new app to APP_NAME
VENERABLE_APP_NAME="$APP_NAME-venerable"

cf delete $VENERABLE_APP_NAME -f
cf rename $APP_NAME $VENERABLE_APP_NAME
cf push $APP_NAME --no-start -p build/libs/*.jar
cf set-env $APP_NAME SLACK_OAUTH2_TOKEN $SLACK_OAUTH2_TOKEN
cf set-env $APP_NAME SLACK_CHANNEL_ID $SLACK_CHANNEL_ID
cf set-env $APP_NAME GITHUB_SECRET $GITHUB_SECRET
cf set-env $APP_NAME INFO_VERSION $INFO_VERSION
cf restage $APP_NAME

if cf start $APP_NAME ; then
  # the app started successfully so remove venerable app
  cf delete $VENERABLE_APP_NAME -f
else
  # the app failed to start so delete the newly deployed app and rename old app back
  cf delete $APP_NAME -f
  cf rename $VENERABLE_APP_NAME $APP_NAME
fi