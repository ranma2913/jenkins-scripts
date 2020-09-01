#!/bin/bash
echo "This script installs or updates the Jenkins CLI"
read -p "Are you sure you want to continue? (y/n): " -n 1 -r IS_CONTINUE
function enableXtrace() {
  set -o xtrace
}
function disableXtrace() {
  set +o xtrace
}
JENKINS_SCRIPTS_HOME=$(pwd)

if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
  enableXtrace
  DOWNLOAD_CLI_JAR=false
  if test -f 'jenkins-cli.jar'; then
    read -p "Do you want to update the jenkins-cli.jar? (y/n): " -n 1 -r IS_CONTINUE
    if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
      DOWNLOAD_CLI_JAR=true
    fi
  else
    DOWNLOAD_CLI_JAR=true
  fi

  if [ "$DOWNLOAD_CLI_JAR" = true ]; then
    read -p 'Please enter your jenkins url. (ex. https://riptide-jenkins-cloud.optum.com/): ' JENKINS_URL
    JENKINS_URL=${JENKINS_URL:-https://riptide-jenkins-cloud.optum.com/}

    KEYSTOREFILE="$JENKINS_SCRIPTS_HOME/jenkinsKeyStore"
    KEYSTOREPASS=changeme

    # Initialize an empty keystore
    rm $KEYSTOREFILE
    keytool -genkeypair -alias boguscert -storepass $KEYSTOREPASS -keypass $KEYSTOREPASS -keystore $KEYSTOREFILE -dname "CN=Developer, OU=Department, O=Company, L=City, ST=State, C=CA"
    keytool -delete -alias boguscert -storepass $KEYSTOREPASS -keystore $KEYSTOREFILE

    # get the SSL certificate
    curl https://repo1.uhc.com/artifactory/UHG-certificates/optum/Optum_Root_CA.cer --output Optum_Root_CA.cer

    # create a keystore and import certificate
    keytool -import -noprompt -trustcacerts -alias Optum_Root_CA -file Optum_Root_CA.cer -keystore ${KEYSTOREFILE} -storepass ${KEYSTOREPASS}

    # verify that the certificate is listed
    keytool -list -v -keystore ${KEYSTOREFILE} -storepass ${KEYSTOREPASS}

    # get jenkins-cli
    rm 'jenkins-cli.jar'
    curl ${JENKINS_URL}/jnlpJars/jenkins-cli.jar --output jenkins-cli.jar

    echo '' >>dotfiles/.jenkins-cli
    ALIAS_COMMAND="java -jar $JENKINS_SCRIPTS_HOME/jenkins-cli.jar -Djavax.net.ssl.trustStore=${KEYSTOREFILE} -Djavax.net.ssl.trustStorePassword=${KEYSTOREPASS} -s "'$JENKINS_URL'
    git checkout -- dotfiles/.jenkins-cli
    echo "alias jcli='$ALIAS_COMMAND'" >>dotfiles/.jenkins-cli
  fi

  echo "Update .bash_profile with Jenkins CLI info"
  BASH_PROFILE=~/.bash_profile
  touch $BASH_PROFILE
  ADD_SOURCE=false
  grep -qxF '# import `~/.jenkins-cli` if it exists' $BASH_PROFILE || ADD_SOURCE=true
  if [ "$ADD_SOURCE" = true ]; then
    echo '' >>$BASH_PROFILE
    echo '#########################################' >>$BASH_PROFILE
    echo '# Jenkins CLI' >>$BASH_PROFILE
    echo "export JENKINS_SCRIPTS_HOME=$JENKINS_SCRIPTS_HOME" >>$BASH_PROFILE
    echo 'export PATH="$JENKINS_SCRIPTS_HOME:$PATH"' >>$BASH_PROFILE
    echo '# import `~/.jenkins-cli` if it exists' >>$BASH_PROFILE
    echo 'if [ -f $JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli ]; then' >>$BASH_PROFILE
    echo '  source $JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli' >>$BASH_PROFILE
    echo 'fi' >>$BASH_PROFILE
    echo '' >>$BASH_PROFILE
  fi
fi

JENKINS_SOURCE="$JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli"
source $JENKINS_SOURCE
jcli help
