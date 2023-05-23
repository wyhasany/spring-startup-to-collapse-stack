#!/usr/bin/env bash
set -euo pipefail

PROFILE_NAME=${1}

if [ ! -d "reports" ]; then
  mkdir reports
fi

curl --fail-with-body localhost:8080/actuator/startup > "reports/$PROFILE_NAME.json"

if [ ! -f "converter-spring-boot-startup.jar" ]; then
  ./gradlew shadowJar
  cp build/libs/converter-spring-boot-startup.jar .
fi
java -jar converter-spring-boot-startup.jar "reports/$PROFILE_NAME.json" "reports/$PROFILE_NAME.collapse"

if [ ! -f "converter.jar" ]; then
    curl -L https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.9/converter.jar --output converter.jar
fi
java -cp converter.jar FlameGraph "reports/$PROFILE_NAME.collapse" "reports/$PROFILE_NAME.html"