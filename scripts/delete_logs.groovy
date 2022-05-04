import hudson.model.Job
import jenkins.model.Jenkins

// Delete old logs that fills up the disk on the master node.
// Run this from the Jenkins console (Manage Jenkins, Manage Nodes, master, Script Console)

def numBuildsKeepLogs = 5
def itemNameMatcher=""
try{
    itemNameMatcher=args[0]
}catch(e){
    println ("usage: jcli groovy =< scripts/delete_logs.groovy <JenkinsItemName>")
    return 1;
}


def totalSize = 0
println("************************************************** START ***************************************************************")
Jenkins.instance.getAllItems(Job.class).each { job ->
    if ((job.fullName =~ itemNameMatcher).find()) {
        def recent = job.getBuilds().limit(numBuildsKeepLogs)
        for (build in job.getBuilds()) {
            if (!recent.contains(build)) {
                try {
                    File logFile = build.getLogFile()
                    totalSize += logFile.length()
                    println("$logFile, ${logFile.length()}")
                    logFile.delete()
                    logFile.createNewFile()
                } catch (e) {
                    println("Unable to delete the log for $build. An exception happened: $e")
                }
            }
        }
    }
}
println "Total size: ${totalSize}"
return "************************************************* END ******************************************************************"