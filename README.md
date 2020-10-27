# jenkins-scripts
Handy Groovy Console Scripts for Jenkins and CloudBees Jenkins Platform

## Requirements:
Install the following tools with Home Brew

- gnu-sed
- html-xml-utils

# Jenkins CLI Installation MacOS
Reference: https://www.jenkins.io/doc/book/managing/cli/#using-the-cli-client
1. Checkout this repository in your machine. 
2. Run the setup shell script:
```
Setup-Update_JenkinsCli.sh
```

## Test your installation
Run the following command in your terminal
```
jcli
```
The output should look something like the following if everything is working correctly:


# Usage
Reference: https://xanderx.com/post/run-jenkins-script-console-scripts-from-command-line-without-remoting/
```
jcli groovy =< scripts/delete_job_history.groovy
```
