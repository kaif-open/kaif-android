#!/bin/bash
apks="build/outputs/apk/app-debug-1.0.*.apk"

function build {
  assembleType=$1
  target=$2
  ../gradlew --rerun-tasks clean $assembleType

  chmod 600 $apks
  cp $apks "`dirname $target`/`basename $target`/"
}

apks="build/outputs/apk/app-debug-1.0.*.apk"
build assembleDebug $1

apks="build/outputs/apk/app-release-1.0.*.apk"
build assembleRelease $1
