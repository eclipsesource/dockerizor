#!/usr/bin/env bash

if [ ${VIRGO_FLAVOUR} == "VTS" ]
then
  echo "Virgo Server for Apache Tomcat detected."
  echo "Using hostname ${HOSTNAME} in tomcat-server.xml"

  sed -i "s/address=\"127.0.0.1\"/address=\"${HOSTNAME}\"/" ${VIRGO_HOME}/configuration/tomcat-server.xml
fi

echo "Using hostname ${HOSTNAME} in osgi.console.properties"
sed -i "s/telnet.host=localhost/telnet.host=${HOSTNAME}/" ${VIRGO_HOME}/repository/ext/osgi.console.properties

${VIRGO_HOME}/bin/startup.sh
