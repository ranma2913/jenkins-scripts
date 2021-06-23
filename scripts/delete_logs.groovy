import hudson.model.Job
import jenkins.model.Jenkins

// Delete old logs that fills up the disk on the master node.
// Run this from the Jenkins console (Manage Jenkins, Manage Nodes, master, Script Console)

//def itemNameMatcher = "/"
//def itemNameMatcher = "Build_And_Deploy/"
//def itemNameMatcher = "Fortify_Scan/"
def itemNameMatcher = "Sonar_Scan/"
def numBuildsKeepLogs = 2

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