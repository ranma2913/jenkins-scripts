#!/bin/bash

function enableXtrace() {
  echo " ...Enable xtrace"
  set -o xtrace
}
function disableXtrace() {
  echo " ...Disable xtrace"
  set +o xtrace
}

read -p "Verbose Output? (y/n): [n] " -n 1 -r IS_CONTINUE
IS_CONTINUE=${IS_CONTINUE:-n}
if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
  # Enable verbose output
  enableXtrace
else
  disableXtrace
fi

JENKINS_SCRIPTS_HOME=$(pwd)
echo "This utility installs, updates, or configures the Jenkins CLI"
printf "The current .jenkins-cli looks like:\n>>>>\n"
cat "$JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli"
printf '\n<<<<\n'

read -p "Are you sure you want to continue? (y/n): " -n 1 -r IS_CONTINUE
KEYSTOREFILE="$JENKINS_SCRIPTS_HOME/jenkins.jks"
KEYSTOREPASS=changeit

read -p 'Please enter your jenkins url. (ex. https://jenkins-riptide-devops.com/): ' JENKINS_URL
JENKINS_URL=${JENKINS_URL:-}
read -p "Please enter the alias name for switching to $JENKINS_URL. (ex. riptideJenkinsDevops): " JENKINS_ALIAS
JENKINS_ALIAS=${JENKINS_ALIAS:-riptideJenkinsDevops}
read -p "Please enter your MSID: " SCRIPT_MSID
SCRIPT_MSID=${SCRIPT_MSID}
read -p "Please enter your Jenkins API Token (Generate one here $JENKINS_URL/user/$SCRIPT_MSID/configure): " SCRIPT_API_TOKEN
SCRIPT_API_TOKEN=${SCRIPT_API_TOKEN}

if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
  # Update Certificates
  UPDATE_KEYSTORE=false
  RESET_KEYSTORE=false
  if test -f $KEYSTOREFILE; then
    read -p "Do you want to update the jenkins.jks? (y/n): " -n 1 -r IS_CONTINUE
    if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
      UPDATE_KEYSTORE=true
    fi
  else
    RESET_KEYSTORE=true
    UPDATE_KEYSTORE=true
  fi
  if [ "$RESET_KEYSTORE" = true ]; then
    # Initialize an empty keystore
    rm "${KEYSTOREFILE}"
    keytool -genkeypair -alias boguscert -storepass ${KEYSTOREPASS} -keypass ${KEYSTOREPASS} -keystore "${KEYSTOREFILE}" -dname "CN=Developer, OU=Department, O=Company, L=City, ST=State, C=CA"
    keytool -delete -alias boguscert -storepass ${KEYSTOREPASS} -keystore "${KEYSTOREFILE}"
  fi

  if [ "$UPDATE_KEYSTORE" = true ]; then
    CERT_ALIAS=Root_CA
    # Remove old cert
    keytool -delete -alias ${CERT_ALIAS} -keystore "${KEYSTOREFILE}" -storepass ${KEYSTOREPASS} || echo "${CERT_ALIAS} already deleted."
    # get the SSL certificate
    Root_CA_URL='https://r''epo1.u''hc.com/a''rtifactory/U''HG-certificates/o''ptum/O''ptum_Root_CA.cer'
    mkdir -vp build/tmp
    curl $Root_CA_URL --output build/tmp/${CERT_ALIAS}.pem
    # import certificate
    keytool -import -noprompt -trustcacerts -alias ${CERT_ALIAS} -file build/tmp/${CERT_ALIAS}.pem -keystore "${KEYSTOREFILE}" -storepass ${KEYSTOREPASS}

    if [[ $JENKINS_URL == https* ]]; then
      TRIMMED_URL=$(echo "$JENKINS_URL" | awk -F/ '{print $3}')
      read -p "Do you want to update the jenkins.jks with a custom cert from your jenkins instance [$TRIMMED_URL]? (y/n): " -n 1 -r IS_CONTINUE
      if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
        echo "Getting Cert for $TRIMMED_URL"
        echo QUIT |
          openssl s_client -showcerts -connect $TRIMMED_URL:443 -servername $TRIMMED_URL |
          awk '/-----BEGIN CERTIFICATE-----/ {p=1}; p; /-----END CERTIFICATE-----/ {p=0}' >"build/tmp/${TRIMMED_URL}.cer"
        # Remove old cert
        keytool -delete -alias "${TRIMMED_URL}" -keystore "${KEYSTOREFILE}" -storepass ${KEYSTOREPASS} || echo "${TRIMMED_URL} already deleted."
        # import certificate
        keytool -import -noprompt -trustcacerts -alias "${TRIMMED_URL}" -file "build/tmp/${TRIMMED_URL}.cer" -keystore "${KEYSTOREFILE}" -storepass ${KEYSTOREPASS}
      fi
    fi

    # verify that the certificate is listed
    KEYSTOREFILE="$JENKINS_SCRIPTS_HOME/jenkins.jks"
    keytool -list -v -keystore "${KEYSTOREFILE}" -storepass changeit
    echo ''
  fi

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

    # get jenkins-cli
    rm 'jenkins-cli.jar'
    curl ${JENKINS_URL}/jnlpJars/jenkins-cli.jar --output jenkins-cli.jar
  fi

  ALIAS_COMMAND="java -Djavax.net.ssl.trustStore=${KEYSTOREFILE} -Djavax.net.ssl.trustStorePassword=${KEYSTOREPASS} -jar $JENKINS_SCRIPTS_HOME/jenkins-cli.jar -s \\\$JENKINS_URL -webSocket"

  RESET_JENKINS_CLI_FILE=false
  ALIAS_COMMAND_PRESENT=false
  grep -qxF 'alias jcli=' dotfiles/.jenkins-cli || ALIAS_COMMAND_PRESENT=true
  echo $ALIAS_COMMAND_PRESENT
  if [ "$ALIAS_COMMAND_PRESENT" = false ]; then
    RESET_JENKINS_CLI_FILE=true
  else
    read -p "Do you want to reset the .jenkins-cli file and start afresh? (y/n): " -n 1 -r IS_CONTINUE
    if [[ $IS_CONTINUE =~ ^[Yy]$ ]]; then
      RESET_JENKINS_CLI_FILE=true
    fi
  fi

  if [ "$RESET_JENKINS_CLI_FILE" = true ]; then
    git checkout -- dotfiles/.jenkins-cli
    echo '' >>dotfiles/.jenkins-cli
    echo "alias jcli=\"$ALIAS_COMMAND\"" >>dotfiles/.jenkins-cli
  fi

  echo "alias $JENKINS_ALIAS='export JENKINS_URL=$JENKINS_URL JENKINS_USER_ID=$SCRIPT_MSID JENKINS_API_TOKEN=$SCRIPT_API_TOKEN \
&& echo "'"ALIAS: '$JENKINS_ALIAS' JENKINS_URL=$JENKINS_URL JENKINS_USER_ID=$JENKINS_USER_ID"'"'" >>dotfiles/.jenkins-cli
  echo "$JENKINS_ALIAS" >>dotfiles/.jenkins-cli

  echo "Update ~/.zshrc with Jenkins CLI info"
  USER_RC_FILE=~/.zshrc
  touch $USER_RC_FILE

  ADD_SOURCE=false
  grep -qxF "# import '$JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli' if it exists" $USER_RC_FILE || ADD_SOURCE=true
  if [ "$ADD_SOURCE" = true ]; then
    echo '' >>$USER_RC_FILE
    echo '#########################################' >>$USER_RC_FILE
    echo '# Jenkins CLI' >>$USER_RC_FILE
    echo "export JENKINS_SCRIPTS_HOME=$JENKINS_SCRIPTS_HOME" >>$USER_RC_FILE
    echo 'export PATH="$JENKINS_SCRIPTS_HOME:$PATH"' >>$USER_RC_FILE
    echo "# import '$JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli' if it exists" >>$USER_RC_FILE
    echo 'if [ -f $JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli ]; then' >>$USER_RC_FILE
    echo '  source $JENKINS_SCRIPTS_HOME/dotfiles/.jenkins-cli' >>$USER_RC_FILE
    echo 'fi' >>$USER_RC_FILE
    echo '' >>$USER_RC_FILE
  fi
fi
