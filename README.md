# jenkins-scripts
Handy Groovy Console Scripts for Jenkins and CloudBees Jenkins Platform

## Requirements:
Install the following tools with HomeBrew
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

![Successful Installation](docs/images/2020-10-27_15-03-23.png)

# Usage
Reference: https://xanderx.com/post/run-jenkins-script-console-scripts-from-command-line-without-remoting/

Delete job history
```
jcli groovy =< scripts/delete_job_history.groovy
```
Delete artifacts from jobs
```
jcli groovy =< scripts/delete_artifacts.groovy
```
Delete logs from jobs
```
jcli groovy =< scripts/delete_logs.groovy
```

## Useful Shell Commands
Show file sizes in a directory
```
ls -l --block-size=M /var/lib/jenkins
```
Summary Disk Usage Recursive
```
du -shc /var/lib/jenkins/*
du -h --max-depth=1 /var/lib/jenkins
du -h /var/lib/jenkins/ | sort -rh | head -5
```
Shows disk space in human-readable format
```
df -h
```