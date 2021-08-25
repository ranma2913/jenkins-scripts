# jenkins-scripts
Handy Groovy Console Scripts for Jenkins and CloudBees Jenkins Platform

# CAUTION THE SCRIPTS CONSOLE IS VERY POWERFUL AND THUS DANGEROUS. It's recommended you backup your jenkins instance before running any scripts.

## Requirements:
Install the following tools with HomeBrew
- gnu-sed
- html-xml-utils

## Checking out the project from Git
This project has a [submodule(s)](https://git-scm.com/book/en/v2/Git-Tools-Submodules). 
When you clone such a project, by default you get the directories that contain submodules, 
but none of the files within them yet:
```
git clone --recurse-submodules https://github.optum.com/riptide-devops/jenkins-scripts.git && \
cd jenkins-scripts && \
git config --local include.path .gitconfig
```
To also initialize, fetch and checkout any nested submodules, you can use the foolproof 
```
git submodule update --init --recursive
```
If you run `git submodule update --remote`, Git will go into your submodules and fetch and update for you.

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

Kill Zombie Jobs

```
jcli groovy =< scripts/findAndKillZombieJobs.groovy
jcli groovy =< optum/Jenkins_Abort_Jobs.groovy
jcli groovy =< optum/Jenkins_Abort_All_Jobs.groovy
jcli groovy =< optum/Jenkins_Abort_Build_Queue.groovy
```

## Plugin Tips:

- Uninstall: [Job Configuration History Plugin](https://plugins.jenkins.io/jobConfigHistory) if you have any GitHub Org
  Jobs

## Shell Script Commands: Cleanup File System

### Useful Shell Commands

Show file sizes in a directory

```bash
ls -l --block-size=M /var/lib/jenkins
```

Summary Disk Usage Recursive

```bash
du -shc /var/lib/jenkins/*
du -h --max-depth=1 /var/lib/jenkins
du -h /var/lib/jenkins/ | sort -rh | head -5
```

Shows disk space in human-readable format

```bash
df -h
```
Check Logs Directory Usage

```bash
du -ah $JENKINS_HOME/logs
```

### Cleanup Shell Commands
Delete .gz log files

```bash
find $JENKINS_HOME/logs \
  -name "*.gz" \
  -printf '%p' \
  -delete
```

Delete rolled logs

```bash
echo "Files to Delete:" && ls $JENKINS_HOME/logs/**/* | grep -P "^.+\.log\.\d+$"
ls $JENKINS_HOME/logs/**/* | grep -P "^.+\.log\.\d+$" | xargs -d "\n" -I {} rm -v {}
```

Delete HTML Audit Logs (One of the largest File System Hogs)

```bash
du -ah $JENKINS_HOME/logs/audit/html
ls $JENKINS_HOME/logs/audit/html | grep -P "^audit-\d{4}-\d{2}-\d{2}.html$" | xargs -d "\n" -I {} rm -v {}
du -ah $JENKINS_HOME/logs/audit/html
```

Delete Pipeline Config History

```bash
du -h -d 1 $JENKINS_HOME/pipeline-config-history
rm -rfv $JENKINS_HOME/pipeline-config-history/**/* && \
du -ah $JENKINS_HOME/pipeline-config-history
```
