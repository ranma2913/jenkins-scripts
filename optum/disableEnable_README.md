# disable
```bash
echo $JENKINS_URL
curl --user 'jsticha:118ada2f5ce00bc14f1132a42f9732f124' --data-urlencode "script=$(< ./optum/disableAllJobs.groovy)" $JENKINS_URL/scriptText
```

# enable
```bash
echo $JENKINS_URL
curl --user 'jsticha:118ada2f5ce00bc14f1132a42f9732f124' --data-urlencode "script=$(< ./optum/enableAllJobs.groovy)" $JENKINS_URL/scriptText
```