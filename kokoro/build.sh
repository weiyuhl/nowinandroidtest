#!/bin/bash

set -e
# Display commands to stderr.
set -x

GRADLE_FLAGS=()
if [[ -n "$GRADLE_DEBUG" ]]; then
  GRADLE_FLAGS=( --debug --stacktrace )
fi

# Install the build tools and accept all licenses
export ANDROID_HOME=/opt/android-sdk/current
echo "Installing build-tools..."
echo y | ${ANDROID_HOME}/tools/bin/sdkmanager "build-tools;30.0.3" > /dev/null
echo y | ${ANDROID_HOME}/tools/bin/sdkmanager --licenses

cd $KOKORO_ARTIFACTS_DIR/git/nowtest

# The build needs Java 17, set it as the default Java version.
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk
sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
java -version

# Also clear JAVA_HOME variable so java -version is used instead
export JAVA_HOME=

./gradlew "${GRADLE_FLAGS[@]}" build
