# jenkins-scripts
Handy Groovy Console Scripts for Jenkins and CloudBees Jenkins Platform

## Requirements:
Install the following tools with Home Brew

- gnu-sed
- html-xml-utils

# Installation
# https://www.jenkins.io/doc/book/managing/cli/#using-the-cli-client
```
Setup-Update_JenkinsCli.sh
```

## Test your installation
```
jcli
```

# Usage
Reference: https://xanderx.com/post/run-jenkins-script-console-scripts-from-command-line-without-remoting/
```
jcli groovy =< optum/deleteJobHistory.groovy
```